import java.io.*;
import java.util.*;

/*
    [요구사항]
    - 그룹 구분
        - bfs로 각 그룹을 마킹함
        - 매 bfs마다 cnt를 늘려서 중복된 id가 없도록 해야함
    
    - 각 그룹의 수를 기억해야함

    - 맞닿은 경계의 수를 기억해야함
        - 상하, 좌우로 탐색하며 숫자 변하는 지점 찾아서 cnt하면 됨
    
    - 조화로움 계산해야함
        - 리그전 같이 모든 조합에 대한 조화로움을 만들어야함

    - 회전 기능 구현

*/

public class Main {
    static final boolean log = false;

    static int n;
    static int[][] map;
    static int[][] gMap;
    static int[][] visited;
    static ArrayList<Node> numArr;
    static int cnt;
    static int score;

    static int answer;

    static int[] dr = {-1, 0, 1, 0};
    static int[] dc = {0, 1, 0, -1};

    static class Node {
        int r;
        int c;
        int cnt;
        int num;

        Node(int r, int c) {
            this.r = r;
            this.c = c;
        }

        Node(int r, int c, int cnt, int num) {
            this.cnt = cnt;
            this.num = num;
        }
    }

    public static void main(String[] args) throws IOException {
        // 여기에 코드를 작성해주세요.
        
        // 입력
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        n = Integer.parseInt(br.readLine());
        map = new int[n][n];
        gMap = new int[n][n];
        for(int i = 0; i < n; i++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            for(int j = 0; j < n; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        if(log) printMap(map);
        
        for(int ii = 0; ii <= 3; ii++) {
            if(log) System.out.println(ii+"-th trial !!");

            // 회전
            if(ii != 0) {
                rotate();

                if(log) {
                    System.out.println("rotate() !!");
                    printMap(gMap);
                }
            }

            // 초기화
            if(ii != 0) {
                for(int i = 0; i < n; i++) {
                    for(int j = 0; j < n; j++) {
                        map[i][j] = numArr.get(gMap[i][j]).num;
                    }
                }

                if(log) {
                    System.out.println("색 번호로 매핑 !!");
                    printMap(map);
                }
            }
            visited = new int[n][n];
            numArr = new ArrayList<>();
            gMap = new int[n][n];

            // 영역 구분
            Integer id = 0;
            for(int i = 0; i < n; i++) {
                for(int j = 0; j < n; j++) {
                    if(visited[i][j] == 1) continue;
                    visited[i][j] = 1;
                    gMap[i][j] = id; // 진입 위치에도 표기를 해야한다.
                    bfs(i, j, id, map[i][j]);
                    id++;
                    numArr.add(new Node(i, j, cnt, map[i][j]));
                    // if(log) printMap(gMap);
                }
            }

            if(log) printMap(gMap);
            if(log) printArr(numArr);

            // logic
            calc();

            answer += score;
        }

        System.out.println(answer);
    }

    static void bfs(int r, int c, int id, int curColor) {
        Queue<Node> q = new LinkedList<>();
        
        cnt = 1;
        q.offer(new Node(r, c));

        while(!q.isEmpty()) {
            Node node = q.poll();

            for(int i = 0; i < dr.length; i++) {
                int nr = node.r + dr[i];
                int nc = node.c + dc[i];

                if(nr<0||nc<0||n<=nr||n<=nc) continue;
                if(visited[nr][nc] == 1) continue;
                if(map[nr][nc] != curColor) continue;

                cnt++;
                visited[nr][nc] = 1;
                gMap[nr][nc] = id;
                q.offer(new Node(nr, nc));
            }
        }
    }

    static void rotate() {
        // 십자가
        int[][] gMap2 = new int[n][n];
        for(int i = 0; i < n; i++) {
            gMap2[i][n/2] = gMap[n/2][n-1-i];
            gMap2[n/2][i] = gMap[i][n/2];
        }

        // 지방 소도시
        // 좌상
        for(int i = 0; i < n/2; i++) {
            for(int j = 0; j < n/2; j++) {
                gMap2[j][n/2-1-i] = gMap[i][j];
            }
        }
        // 우상
        for(int i = 0; i < n/2; i++) {
            for(int j = n/2+1; j < n; j++) {
                gMap2[j-n/2-1][n-1-i] = gMap[i][j];
            }
        }
        // 좌하
        for(int i = n/2+1; i < n; i++) {
            for(int j = 0; j < n/2; j++) {
                gMap2[n/2+1+j][i-n/2-1] = gMap[i][j];
            }
        }
        // 우하
        for(int i = n/2+1; i < n; i++) {
            for(int j = n/2+1; j < n; j++) {
                gMap2[j][n-1-i+n/2+1] = gMap[i][j];
            }
        }

        gMap = gMap2;
    }

    static void calc() {
        score = 0;

        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                for(int di = 1; di <= 2; di++) {
                    int nr = i + dr[di];
                    int nc = j + dc[di];

                    if(nr<0||nc<0||n<=nr||n<=nc) continue;
                    if(gMap[i][j] == gMap[nr][nc]) continue;

                    int g1 = gMap[i][j];
                    int g2 = gMap[nr][nc];

                    int g1c = numArr.get(g1).cnt;
                    int g2c = numArr.get(g2).cnt;

                    int g1n = numArr.get(g1).num;
                    int g2n = numArr.get(g2).num;

                    score += (g1c + g2c) * g1n * g2n;
                }
            }
        }

        if(log) System.out.println("score="+score);
    }

    static void printMap(int[][] map) {
        for(int[] row: map) {
            for(int ele: row) {
                System.out.print(ele+" ");
            }
            System.out.println();
        }
        System.out.println();
    }

    static void printArr(ArrayList<Node> arr) {
        for(Node a: arr) {
            System.out.println(a.cnt + ", " + a.num);
        }
        System.out.println();
    }
}