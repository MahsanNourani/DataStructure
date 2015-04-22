import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import java.util.Stack;


public class CA4DS810191390 {
	public static Town t = new Town();
	public static ManageIO io = new ManageIO();
	public static Scanner s = new Scanner (System.in);
	public static DFS  dfs = new DFS();
	public static BFS bfs = new BFS();
	public static Dijkstra dijkstra = new Dijkstra();
	public static MST mst = new MST();
	
	public static void main(String[] args) {
		io.buildTown(t, s);
//		io.PrintNodes(t);
		int dfsComponent = dfs.dfs(t.locations);
		System.out.println("1:\n" + dfsComponent );
		System.out.println("2:");
		for (String string : t.stdinInput) 
			if ((string.charAt(0) + "").equals ("2"))
				System.out.println( 
				bfs.shortestPath(t.locations, io.findAdjSpot(t, (string.charAt(1) + ""))
				,io.findAdjSpot(t, (string.charAt(2) + ""))));
		System.out.println("3:");
		for (String string : t.stdinInput) {
			if ((string.charAt(0) + "").equals ("3"))
				System.out.println( dijkstra.dijkstra(t.locations
						,io.findAdjSpot(t, (string.charAt(1) + ""))
				,io.findAdjSpot(t, (string.charAt(2) + ""))));
		}
		System.out.println("4:");
		mst.prim(t.locations ,dfsComponent);
		for (AdjacentSpot spot : t.locations) {
			if (spot.adjLocale.father == null) continue;
			System.out.println(spot.adjLocale.father.adjLocale.spotName + spot.adjLocale.spotName);
		}
		System.out.println("5:");
		mst.kruskal(t ,t.locations ,t.directions);
	}

}

class ManageIO {
	public void getInputs (Town t ,Scanner s) {
		int edge,vertex,weigh;
		String name;
		vertex = s.nextInt();
		edge = s.nextInt();
		while (vertex != 0) {
			name = s.next();
			Spot spot = new Spot(name);
			AdjacentSpot l = new AdjacentSpot(spot, Integer.MAX_VALUE);
			t.locations.add(l);
			vertex--;
		}
		while (edge != 0) {
			name = s.next();
			weigh = s.nextInt();
			Edge e = new Edge(Character.toString(name.charAt(0)),Character.toString(name.charAt(1)), weigh);
			t.directions.add(e);
			edge--;
		}
		name = "";
		while (true) {
			name = s.next(); 
			if (name.equals("exit"))
				break;
			t.stdinInput.add (name);
		}
	}
	
	public void buildTown (Town t ,Scanner s) {
		getInputs (t ,s);
		for (int i = 0; i < t.locations.size(); i++) {
			for (int j = 0; j < t.directions.size(); j++) {
				if (t.directions.get(j).s1.equals( t.locations.get(i).adjLocale.spotName)) {				
					AdjacentSpot temp = new AdjacentSpot(findSpot(t,t.directions.get(j).s2)
															, t.directions.get(j).weigh);
					t.locations.get(i).adjLocale.adjSpots.add(temp);
				}
				if (t.directions.get(j).s2.equals(t.locations.get(i).adjLocale.spotName)) {
					AdjacentSpot temp = new AdjacentSpot(findSpot(t,t.directions.get(j).s1)
															, t.directions.get(j).weigh);
					t.locations.get(i).adjLocale.adjSpots.add(temp);
				}
			}
		}
	}
	
	public Spot findSpot (Town t ,String name) {
		for (int i = 0;i< t.locations.size();i++)
			if (t.locations.get(i).adjLocale.spotName.equals(name))
				return t.locations.get(i).adjLocale;
		return null;
	}
	
	public AdjacentSpot findAdjSpot (Town t ,String name) {
		for (int i = 0;i< t.locations.size();i++)
			if (t.locations.get(i).adjLocale.spotName.equals(name))
				return t.locations.get(i);
		return null;
	}
	
	public void PrintNodes (Town t) {
		for (AdjacentSpot s : t.locations) {
			System.out.print(s.adjLocale.spotName + " ");
			for (AdjacentSpot a : s.adjLocale.adjSpots) {
				System.out.println(a.adjLocale.spotName + "/" + a.getWeight() + " ");
			}
			System.out.println("");
		}
	}
}

class Town {
	ArrayList <AdjacentSpot> locations = new ArrayList<AdjacentSpot>();
	ArrayList <Edge> directions = new ArrayList<Edge>();
	ArrayList <String> stdinInput = new ArrayList<String>();
}

class Spot {
	public String spotName;
	public boolean discovered = false;
	public AdjacentSpot father = null;
	public AdjacentSpot parent = null;
	public String color = "White";
	public int dist = Integer.MAX_VALUE;
	public int componentNum = 0;
	ArrayList <AdjacentSpot> adjSpots = new ArrayList<AdjacentSpot>(); 
	
	Spot (String name) {
		spotName = name;
		
	}
}

class Edge {
	public String s1,s2;
	public int weigh;
	Edge (String a,String b ,int w) {
		s1 = a;s2 = b;weigh = w;
	}
}

class AdjacentSpot {
	public Spot adjLocale;
	private int edgeWeight;
	
	AdjacentSpot (Spot adjl ,int weight) {
		adjLocale = adjl;edgeWeight = weight;
	}	
	public int getWeight () {
		return edgeWeight;
	}
}

class MST {
	private ManageIO io = new ManageIO();
	private DisjointSet d = new DisjointSet();
	private Queue q = new Queue();
//	private DFS dfs = new DFS();
	
	public void prim (ArrayList<AdjacentSpot> spots ,int dfsComponent) {
		int connectedCompnnents = dfsComponent + 2;
		boolean init[] = new boolean[connectedCompnnents];
		for (boolean b : init) {
			b = false;
		}
		for (AdjacentSpot adjacentSpot : spots) {
			adjacentSpot.adjLocale.father = null;
			if (!init[adjacentSpot.adjLocale.componentNum]) {
				adjacentSpot.adjLocale.dist = 0;
				init[adjacentSpot.adjLocale.componentNum] = true;
			}
			else adjacentSpot.adjLocale.dist = Integer.MAX_VALUE; 
		}
		for (int i = 1;i<connectedCompnnents;i++) {
			for (AdjacentSpot adjacentSpot : spots) {
				if (adjacentSpot.adjLocale.componentNum == i) {
					q.enqueue(adjacentSpot);
				}
			}
				while (!q.isEmpty()) {
					AdjacentSpot temp = q.getMin();
					for (AdjacentSpot adjNode : temp.adjLocale.adjSpots) {
						if ((q.hasElement(adjNode)) && (adjNode.getWeight() < adjNode.adjLocale.dist)) {
							adjNode.adjLocale.dist = adjNode.getWeight();
							adjNode.adjLocale.father = temp;
						}
					}
				}
		}
	}
	
	public void kruskal (Town t ,ArrayList<AdjacentSpot> spots ,ArrayList<Edge> edges) {
		ArrayList<Edge> MST = new ArrayList<Edge>();
		for (AdjacentSpot vertex : spots) 
			d.makeSet (vertex);
		//sort edges in increasing order of weigh
		Collections.sort(edges, new Comparator<Edge>() {
//	        @Override
	        public int compare(Edge e1 ,Edge e2)
	        {
	        	Integer w1 = new Integer (e1.weigh);
	        	Integer w2 = new Integer (e2.weigh);
	            return w1.compareTo(w2);
	        }
	    });
		for (Edge edge : edges) {
			if (d.findSet(io.findAdjSpot(t, edge.s1)) != d.findSet(io.findAdjSpot(t, edge.s2)) ) {
				MST.add(edge);
				d.union(io.findAdjSpot(t, edge.s1), io.findAdjSpot(t, edge.s2));
			}
		}
		for (Edge edge : MST) {
			System.out.println(edge.s1 + edge.s2);
		}
	}
}

class Dijkstra {
	BFS bfs = new BFS();
	
	public String dijkstra (ArrayList<AdjacentSpot> spots ,AdjacentSpot sp ,AdjacentSpot dp) {
		String getDirections = "";
		Queue q = new Queue ();
		bfs.bfs(spots, sp);
		if (dp.adjLocale.father == null)
			getDirections = "$";
		else {
			for (AdjacentSpot adjacentSpot : spots){
				adjacentSpot.adjLocale.father = null;
				adjacentSpot.adjLocale.dist = Integer.MAX_VALUE;
			}
			sp.adjLocale.dist = 0;
			for (AdjacentSpot adjacentSpot : spots) {
				q.enqueue(adjacentSpot);
			}
			while (!q.isEmpty()) {
				AdjacentSpot temp = q.getMin();
				if (temp.adjLocale.spotName.equals(dp.adjLocale.spotName))
					break;
				for (AdjacentSpot adjacentSpot : temp.adjLocale.adjSpots) {
					int newDist = adjacentSpot.getWeight() + temp.adjLocale.dist;
					if (newDist < adjacentSpot.adjLocale.dist) {
						adjacentSpot.adjLocale.dist = newDist;
						adjacentSpot.adjLocale.father = temp;
					}
				}				
			}
			while (dp != null) {
				getDirections = dp.adjLocale.spotName + getDirections;
				dp = dp.adjLocale.father;
			}
		}
		return getDirections;	
	}
}

class BFS {
	public void bfs (ArrayList<AdjacentSpot> spots ,AdjacentSpot sp) {
		Queue q = new Queue();
		for (AdjacentSpot adjacentSpot : spots) {
			if (adjacentSpot == sp) continue;
			adjacentSpot.adjLocale.color = "White";
			adjacentSpot.adjLocale.father = null;
		}
		sp.adjLocale.color = "Gray";
		sp.adjLocale.father = null;
		q.enqueue(sp);
		while (!q.isEmpty()) {
			AdjacentSpot u = q.dequeue();
			for (AdjacentSpot adjacentSpot : u.adjLocale.adjSpots) {
				if (adjacentSpot.adjLocale.color.equals("White")) {
					adjacentSpot.adjLocale.color = "Gray";
					adjacentSpot.adjLocale.father = u;
					q.enqueue(adjacentSpot);
				}
			}
			u.adjLocale.color = "Black";
		}
	}
	public String shortestPath (ArrayList<AdjacentSpot> spots ,AdjacentSpot sp ,AdjacentSpot dp) {
		String getDirections = "";
		bfs (spots ,sp);
		if (dp.adjLocale.father == null) 
			getDirections = "$";
		else {
			while (dp != null) {
				getDirections = dp.adjLocale.spotName + getDirections;
				dp = dp.adjLocale.father;
			}
		}
		return getDirections;
	}
}

class DFS {
	public int dfs (ArrayList<AdjacentSpot> spots) {
		Stack <AdjacentSpot> s = new Stack <AdjacentSpot>();
		int disconnectedComponents = 0;
		for (int i = 0;i<spots.size();i++) {
			if (!spots.get(i).adjLocale.discovered) {
//				spots.get(i).adjLocale.discovered = true;
				disconnectedComponents++;
				s.push (spots.get(i));
				while (!s.isEmpty()) {
					AdjacentSpot temp = s.pop();
					if (temp.adjLocale.discovered == false) {
						temp.adjLocale.discovered = true;
						temp.adjLocale.componentNum = disconnectedComponents;
						for (int j = 0;j<temp.adjLocale.adjSpots.size();j++) {
							s.push(temp.adjLocale.adjSpots.get(j));
						}
					}
				}
			}
		}
		return disconnectedComponents - 1;
	}
}

class Queue {
	public ArrayList <AdjacentSpot> q = new ArrayList<AdjacentSpot>();
	
	public void enqueue (AdjacentSpot sp) {
		q.add(sp);
	}
	public AdjacentSpot dequeue () {
		return q.remove(0);
	}
	public boolean isEmpty () {
		if (q.size() == 0)
			return true;
		else return false;
	}
	public AdjacentSpot getMin () {
		AdjacentSpot s = q.get(0);
		for (AdjacentSpot a : q) 
			if (s.adjLocale.dist > a.adjLocale.dist)
				s = a;
		q.remove(s);
		return s;
	}
	public boolean hasElement (AdjacentSpot adjacentSpot) {
		for (int i = 0;i< q.size();i++)
			if (q.get(i).adjLocale.spotName.equals(adjacentSpot.adjLocale.spotName))
				return true;
		return false;
	}
}

class DisjointSet {
	public void makeSet (AdjacentSpot a) {
		a.adjLocale.parent = a;
	}
	public AdjacentSpot findSet (AdjacentSpot a) {
		if (a.adjLocale.parent == a)
			return a;
		return findSet(a.adjLocale.parent);
	}
	public void union (AdjacentSpot a ,AdjacentSpot b) {
		AdjacentSpot aRoot = findSet(a);
		AdjacentSpot bRoot = findSet(b);
		aRoot.adjLocale.parent = bRoot;
	}
}

