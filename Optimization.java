import java.util.*;
import java.util.regex.*;
import java.io.*;

class Optimization {

  class Address {
    int x;
    int y;

    public Address(int x, int y) {
      this.x = x;
      this.y = y;
    }

    public ArrayList<Integer> getCoords() {
      ArrayList<Integer> coords = new ArrayList<Integer>();
      coords.add(x);
      coords.add(y);
      return coords;
    }

    public String toString() {
      return "(" + x + ", " + y + ")";
    }

    public int getX() {
      return x;
    }
    
    public int getY() {
      return y;
    }
  }

  class Edge {
    Address start;
    Address end;
    double weight;

    public Edge(Address start, Address end, double weight) {
      this.start = start;
      this.end = end;
      this.weight = weight;
    } 

    public double getWeight() {
      return weight;
    }

    public static Comparator<Edge> EdgeComparator = new Comparator<Edge>() {
      public int compare(Edge e1, Edge e2) {
        Double edge1 = e1.getWeight();
        Double edge2 = e2.getWeight();
        return edge1.compareTo(edge2);
      }
    };

    public Address getRoot() {
      return start;
    }
    public Address getEnd() {
      return end;
    }
    public String toString() {
      return start.toString() + " to " + end.toString() + " : " + weight;
    }
  }

  class Graph {
    int num_nodes;
    ArrayList<Address> addresses;
    ArrayList<Edge> edges;
    
    public Graph (int num_nodes) {
      this.num_nodes = num_nodes;
      this.addresses = new ArrayList<>();
      this.edges = new ArrayList<>();
    }

    public void addAddress(Address address) {
      addresses.add(address);
    }

    public void addEdges(Edge edge) {
      edges.add(edge);
    }

    public int getNodes() {
      return num_nodes;
    }

    public ArrayList<Edge> getEdges() {
      return edges;
    }
  }

  public double edgeWeight(Address a1, Address a2) {

    double diff_x = a2.x - a1.x;
    double diff_y = a2.y - a1.y;
    
    return Math.sqrt(diff_x*diff_x + diff_y*diff_y);
  }

  public Graph generateGraph(List<Address> addresses) {
    int num_nodes = addresses.size();
    Graph bikini_bottom = new Graph(num_nodes);
    for (Address a: addresses) {
      bikini_bottom.addAddress(a);
    }

    for(int i = 0; i < num_nodes; i++) {
      for (int j = i + 1; j < num_nodes; j++) {
        Address a1 = addresses.get(i);
        Address a2 = addresses.get(j);
        double edge_weight = edgeWeight(a1, a2);
        Edge e = new Edge(a1, a2, edge_weight);
        bikini_bottom.addEdges(e);
      }
    }

    return bikini_bottom;
  }

  public static void sort(ArrayList<Edge> list) {
    Collections.sort(list, Edge.EdgeComparator);
  }
  
  public static ArrayList<Address> kruskalAlgorithm(Graph graph) {
    ArrayList<Edge> edges = graph.getEdges();
    ArrayList<ArrayList<Integer>> visited = new ArrayList<ArrayList<Integer>>();
    ArrayList<Edge> mst = new ArrayList<Edge>();
    int numNodes = graph.getNodes();
    sort(edges);
    int edge_index = 0;
    while (visited.size() < numNodes-1) {
      Edge ed = edges.get(edge_index);
      Address a = ed.getRoot();
      Address b = ed.getEnd();
      ArrayList<Integer> start = a.getCoords();
      ArrayList<Integer> end = b.getCoords();
      boolean hasStart = false;
      boolean hasEnd = false;
      for(ArrayList<Integer> v: visited) {
        if (v.equals(start)) {
          hasStart = true;
        }
        else if (v.equals(end)) {
          hasEnd = true;
        }
      }

      if(!hasStart || !hasEnd) {
        mst.add(ed);
        visited.add(start);
        visited.add(end);
      }
      else if(!hasEnd) {
        mst.add(ed);
        visited.add(end);
      }
      else if (!hasStart) {
        mst.add(ed);
        visited.add(start);
      }
      edge_index++;
    }

    ArrayList<Address> final_mst = new ArrayList<Address>();
    for (Edge ed : mst) {
      Address a = ed.getRoot();
      Address b = ed.getEnd();
      final_mst.add(a);
      final_mst.add(b);
    }
    return final_mst;
  }
}