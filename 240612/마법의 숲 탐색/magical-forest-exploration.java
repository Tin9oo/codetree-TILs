// package SamsungProblem;

import java.util.*;
import java.io.*;

/*
    [요구사항]
    - 골렘 이동
    	- 아래에 이미 골렘이 있는 경우 왼쪽 먼저 확인하고 그 다음이 오른쪽이다.
    	- 아래에 이미 골렘이 있는 경우 "좌->하 / 우->하"로 이동하는 방식이라 4칸을 확인해야함
    	- 골렘이 구르면, 출구는 구르는 방향에 해당하는 시계방향으로 회전한다.
    	- 골렘은 -2 인덱스부터 떨어진다 생각해야함
    	- 골렘이 정지했을 때 경계를 튀어나와 있으면 지도 초기화
    	
    - 정령 이동
    	- 탑승은 어디서든 가능
    	- 하차는 정해진 위치에서만 가능
    	- 정령은 가장 남쪽으로 이동한다.
    
    - 정답
    	- 매 시행에서 정령이 위치하는 "행" 번호의 합
*/

public class Main {
	static final boolean log = false;
	static int R, C, K;
	
	static int[][] forest, golemArr;
	static boolean[] visited;
	
	static int score, maxPos;
	
	static int[] dr = {-1, 0, 1, 0};
	static int[] dc = {0, 1, 0, -1};
	
    public static void main(String[] args) throws IOException {
    	// Input
    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    	StringTokenizer st = new StringTokenizer(br.readLine());
    	
    	R = Integer.parseInt(st.nextToken());
    	C = Integer.parseInt(st.nextToken());
    	K = Integer.parseInt(st.nextToken());
    	
    	// Initialization
    	initForest();
    	golemArr = new int[K][];
    	visited = new boolean[K];
    	
    	// Logic
    	for(int i = 0; i < K; i++) {
        	st = new StringTokenizer(br.readLine());
        	
        	int c = Integer.parseInt(st.nextToken()) - 1;
        	int d = Integer.parseInt(st.nextToken());
        	
        	// Init every try
        	maxPos = 0;
        	visited = new boolean[K];
        	
        	if(log) {
        		System.out.println(i+"-th Golem now dropping !!");
        	}
        	drop(i, -2, c, d);
        	
        	score += maxPos;
    	}
    	
    	System.out.println(score);
    }
    
    static void drop(int idx, int cr, int cc, int d) {
    	while(true) {
    		// 바닥에 닿으면 종료
    		if(cr == R - 2) break;
    		
    		// go down
    		if(cr == -2) {
    			if(forest[cr+2][cc] == -1) {
    				cr++;
    				continue;
    			}
    		} else {
    			if(forest[cr+2][cc] == -1 && forest[cr+1][cc-1] == -1 && forest[cr+1][cc+1] == -1) {
	    			cr++;
	    			continue;
	    		}
    		}
    		
    		// go left
    		if(2 <= cc) {
    			if(cr == -2) {
    				if(forest[cr+2][cc-1] == -1)	{
            			cr++; cc--;
            			d = (d + 3) % 4;
            			continue;
            		}
    			} else if(cr == -1) {
    				if(
        					forest[cr+1][cc-2] == -1 &&
        					forest[cr+1][cc-1] == -1 &&
        					forest[cr+2][cc-1] == -1)
    				{
    					cr++; cc--;
            			d = (d + 3) % 4;
            			continue;
    				}
    			} else {
    				if(
        					forest[cr][cc-2] == -1 &&
        					forest[cr+1][cc-2] == -1 &&
        					forest[cr+1][cc-1] == -1 &&
        					forest[cr+2][cc-1] == -1)
        			{
            			cr++; cc--;
            			d = (d + 3) % 4;
            			continue;
            		}
    			}
    			
    		}
    		
    		// go right
    		if(cc < C-2) {
	    		if(cr == -2) {
	    			if(forest[cr+2][cc+1] == -1) {
		    			cr++; cc++;
		    			d = (d + 1) % 4;
		    			continue;
		    		}
	    		} else if(cr == -1) {
    				if(
	    					forest[cr+1][cc+2] == -1 &&
	    					forest[cr+1][cc+1] == -1 &&
	    					forest[cr+2][cc+1] == -1)
		    		{
		    			cr++; cc++;
		    			d = (d + 1) % 4;
		    			continue;
		    		}
    			} else {
	    			if(
	    					forest[cr][cc+2] == -1 &&
	    					forest[cr+1][cc+2] == -1 &&
	    					forest[cr+1][cc+1] == -1 &&
	    					forest[cr+2][cc+1] == -1)
		    		{
		    			cr++; cc++;
		    			d = (d + 1) % 4;
		    			continue;
		    		}
	    		}
    		}
    		
    		break;
    	}
    	
    	// reset forest
    	if(cr <= 0) {
    		if(log) System.out.println("Overflow !!");
    		initForest();
    		return;
    	}
    	
    	// mark golem
    	forest[cr][cc] = idx;
    	forest[cr-1][cc] = idx;
    	forest[cr][cc+1] = idx;
    	forest[cr+1][cc] = idx;
    	forest[cr][cc-1] = idx;
    	
    	golemArr[idx] = new int[] {cr, cc, d};
    	
    	if(log) {
    		System.out.println("Forest !!");
    		printGraph(forest);
    		System.out.println();
    		System.out.println("golemArr !!");
    		printGraph(golemArr);
    	}
    	
    	moveGolem(idx);
    	if(log) {
    		System.out.println("moveGolem() !!");
    		System.out.println("maxPos="+maxPos);
    	}
    }
    
    static void moveGolem(int idx) {
    	// dfs로 이동 가능한 골렘을 옮겨 타며 가장 아래로 이동한다.
    	visited[idx] = true;
    	
    	// 정령의 이동 경로 중 가장 남쪽인 값으로 갱신
    	int cr = golemArr[idx][0] + 2;
    	maxPos = maxPos < cr ? cr : maxPos;
    	
    	// 탈출구 좌표
    	int d = golemArr[idx][2];
    	cr = golemArr[idx][0] + dr[d];
    	int cc = golemArr[idx][1] + dc[d];
    	
    	// 근처 골렘 찾기
    	for(int i = 0; i < dr.length; i++) {
    		int nr = cr + dr[i];
    		int nc = cc + dc[i];
    		
    		if(nr<0||nc<0||R<=nr||C<=nc) continue;
    		if(forest[nr][nc] != -1 && visited[forest[nr][nc]]) continue;
    		if(forest[nr][nc] == -1) continue;
    		
    		moveGolem(forest[nr][nc]);
    	}
    }
    
    static void initForest() {
    	if(log) System.out.println("initForest() !!");
    	forest = new int[R][C];
    	for(int[] row: forest) {
    		Arrays.fill(row, -1);
    	}
    }
    
    static void printGraph(int[][] g) {
    	for(int[] row: g) {
    		if(row == null) break;
    		for(Integer ele: row) {
    			System.out.print(ele+"\t");
    		}
    		System.out.println();
    	}
    	System.out.println();
    }
}