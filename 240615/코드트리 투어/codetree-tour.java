import java.util.*;
import java.io.*;

/*
    [요구사항]
    - n 개의 도시
    	- 0 ~ n-1 번
    	
    - m 개의 간선
    	- 무방향
    	- 두 도시 사이의 간선 여러개 가능
    	- 자신을 향하는 간선 가능
    	
    	- 자신으로 향하면 없다 생각해도 괜찮을듯
    	- 중복간선은 거리가 짧은 것 만 남기면 될 
    	
    - 출발지는 0번으로 통일
    
    - 명령
    	1. 랜드 건설
    		- (v, u, w) => (도시, 도시, 가중치)
    	2. 상품 생성
    		- (id, revenue, dest) => (식별자, 매출, 도착지)
    	3. 상품 취소
    		- id에 해당하는 상품이 존재하면 제거
    	4. 최적 상품 판매
    		- 이득 (revenue - cost) 이 최대인 상품 우선판매
    		- 같은 이득이면 id가 작은 상품 선택
    		- cost : 출빌지부터 id 상품의 도착지까지의 최단거리
    		- 도달 불가 혹은 손해라면 판매불가상품이 된다.
    		- 판매 가능한 상품 중 우선순위가 가장 높은 상품의 id를 출력하고 목록에서 제거
    		- 판매 가능한 상품이 전혀 없다면 -1을 출력하고 상품 제거하지 않음
    	5. 출발지 변경
    		- 출발지 전부 s로 변경
    		- 출발지에 따라 cost가 변경될 수 있음
    
    - Q 회에 걸쳐 명령을 순서대로 수
*/

public class Main {
	static final boolean log = false;
	
	static int Q;
	static int n, m;
	
	static ArrayList<ArrayList<Node>> graph;
	static int[] dist;
	
	static ArrayList<Item> itemArr = new ArrayList<>();
	static PriorityQueue<Item> itemPriorityQueue = new PriorityQueue<Main.Item>();
	
	static boolean[] isMade = new boolean[30000+1];
	static boolean[] isCancel = new boolean[30000+1];
	
	static int startLand = 0;
	
	static class Item implements Comparable<Item> {
		int id, revenue, dest, profit;
		
		Item(int id, int revenue, int dest, int profit) {
			this.id = id;
			this.revenue = revenue;
			this.dest = dest;
			this.profit = profit;
		}
		
		@Override
		public int compareTo(Item other) {
			if(this.profit == other.profit) {
				return Integer.compare(this.id, other.id);
			}
			return Integer.compare(other.profit, this.profit);
		}
		
		@Override
		public String toString() {
			return "Item [id="+id+", revenue="+revenue+", dest="+dest+"]";
		}
	}
	
	static class Node {
		int idx, cost;
		
		Node(int idx, int cost) {
			this.idx = idx;
			this.cost = cost;
		}
		
		@Override
		public String toString() {
			return "Node [idx="+idx+", cost="+cost+"]";
		}
	}
	
    public static void main(String[] args) throws IOException {
    	// Input
    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    	input(br);
    	
    	dijkstra();
    	
    	// Commands
    	for(int i = 1; i < Q; i++) {
    		if(log) System.out.println(i+"-th command !!");
        	StringTokenizer st = new StringTokenizer(br.readLine());
    		int command = Integer.parseInt(st.nextToken());
    		
    		if(command == 200) createItem(st);
    		else if(command == 300) cancleItem(st);
    		else if(command == 400) sellOptimalItem();
    		else if(command == 500) changeStartLand(st);
    	}
    }
    
    // command 200
    static void createItem(StringTokenizer st) {
    	int id = Integer.parseInt(st.nextToken());
    	int revenue = Integer.parseInt(st.nextToken());
    	int dest = Integer.parseInt(st.nextToken());
    	int profit = revenue - dist[dest];
    	
    	isMade[id] = true;
    	
//    	itemArr.add(new Item(id, revenue, dest));
    	itemPriorityQueue.offer(new Item(id, revenue, dest, profit));
    }
    
    // command 300
    static void cancleItem(StringTokenizer st) {
		int id = Integer.parseInt(st.nextToken());
		
//    	for(int i = 0; i < itemArr.size(); i++) {
//    		if(itemArr.get(i).id == id) {
//    			itemArr.remove(i);
//    			break;
//    		}
//    	}
		
		if(isMade[id]) isCancel[id] = true;
    }
    
    // command 400
    static void sellOptimalItem() {
    	if(log) System.out.println("sellOptimalItem() !!");
//    	int income = -1; // -1로 해줘야 최초 갱신되지 않았을 때 2번째 조건으로 빠지지 않는다.
//    	int id = -1;
//    	
//    	int removeIdx = 0;
//    	
//    	// 상품을 전부 탐색하는게 시간 낭비로 보인다.
//    	for(int i = 0; i < itemArr.size(); i++) {
//    		if(log) System.out.println(itemArr.get(i).toString());
//    		if(income < itemArr.get(i).revenue - dist[itemArr.get(i).dest]) {
//    			if(log) {
//    				System.out.println("Max income updated !!");
//    				System.out.println("revenue: " + itemArr.get(i).revenue+", cost: " + dist[itemArr.get(i).dest]);
//    			}
//    			income = itemArr.get(i).revenue - dist[itemArr.get(i).dest];
//    			id = itemArr.get(i).id;
//    			removeIdx = i;
//    			
//    		}
//    		// income이 최초 갱신되지 않은 경우 이 분기로 빠지지 않게 수정해야한다.
//    		else if(income != -1 && income == itemArr.get(i).revenue - dist[itemArr.get(i).dest]
//    				&& itemArr.get(i).id <= itemArr.get(removeIdx).id) { // id는 unique한데 왜 등호를 제거하면 문제가 풀리지 않지?
//    			// 최초에 수입을 갱신하는 경우, removeIdx = 0이라서 원하는 id 값을 비교하지 못하게 된다.
//    			if(log) {
//    				System.out.println("Max income updated !!");
//    				System.out.println("revenue: " + itemArr.get(i).revenue+", cost: " + dist[itemArr.get(i).dest]);
//    			}
//    			income = itemArr.get(i).revenue - dist[itemArr.get(i).dest];
//    			id = itemArr.get(i).id;
//    			removeIdx = i;
//    		}
//    	}
//    	
//    	if(id != -1) itemArr.remove(removeIdx);
//    	
//    	
//    	
//    	System.out.println(id);
    	
    	while(!itemPriorityQueue.isEmpty()) {
    		Item item = itemPriorityQueue.poll();
    		if(item.profit < 0) {
    			break;
    		}
    		if(!isCancel[item.id]) {
    			System.out.println(item.id);
    			return;
    		}
    	}
    	System.out.println("-1");
    }
    
    // command 500
    static void changeStartLand(StringTokenizer st) {
    	startLand = Integer.parseInt(st.nextToken());
    	dijkstra();
    }

    static void dijkstra() {
    	if(log) System.out.println("dijkstra() !!");
    	
    	dist = new int[n];
    	for(int i = 0; i < n; i++) {
    		dist[i] = Integer.MAX_VALUE;
    	}
    	
    	PriorityQueue<Node> q = new PriorityQueue<Main.Node>((o1, o2) -> Integer.compare(o1.cost, o2.cost));
    	
    	q.offer(new Node(startLand, 0));
    	
    	dist[startLand] = 0;
    	
    	while(!q.isEmpty()) {
    		Node curNode = q.poll();
    		
    		// 꺼낸 노드의 비용이 dist 값보다 크다면 고려할 필요 없다.
    		if(dist[curNode.idx] < curNode.cost) {
    			continue;
    		}
    		
    		for(int i = 0; i < graph.get(curNode.idx).size(); i++) {
    			Node nxtNode = graph.get(curNode.idx).get(i);
    			
    			if(dist[nxtNode.idx] > dist[curNode.idx] + nxtNode.cost) {
    				dist[nxtNode.idx] = dist[curNode.idx] + nxtNode.cost;
    				q.offer(new Node(nxtNode.idx, dist[nxtNode.idx]));
    			}
    		}
    	}
    	
    	if(log) System.out.println(Arrays.toString(dist));
    }
    
    // input, command 100
    static void input(BufferedReader br) throws IOException {
    	Q = Integer.parseInt(br.readLine());
    	
    	StringTokenizer st = new StringTokenizer(br.readLine());

    	st.nextToken(); // abandon command 100
    	n = Integer.parseInt(st.nextToken());
    	m = Integer.parseInt(st.nextToken());
    	
    	graph = new ArrayList<ArrayList<Node>>();
    	for(int i = 0; i < n; i++) graph.add(new ArrayList<Main.Node>());
    	
    	for(int i = 0; i < m; i++) {
    		int v = Integer.parseInt(st.nextToken());
    		int u = Integer.parseInt(st.nextToken());
    		int w = Integer.parseInt(st.nextToken());
    		
    		// 출발지 목적지 같으면 제거
    		if(v == u) continue;
    		
    		// 중복 간선 제거
    		
    		
    		graph.get(v).add(new Node(u, w));
    		graph.get(u).add(new Node(v, w));
    	}
    	
    	if(log) {
    		for(ArrayList<Node> arr: graph) {
    			for(Node n: arr) {
    				System.out.print(n.toString());
    			}
    			System.out.println();
    		}
    	}
    }
}