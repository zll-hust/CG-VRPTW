package VRPTW;

/**
 * @author： zll-hust
 * @date： 2021/2/22 17:08
 * @description： TODO
 */
public class Node {
    public int id;
    public int id_external;
    public double xcoord;
    public double ycoord;
    public double servicet;
    public Graph g;

    public Node(Graph graph, int external_id, double x, double y, double t) {
        this.id = graph.all_nodes.size();
        this.id_external = external_id;
        this.xcoord = x;
        this.ycoord = y;
        this.servicet = t;
        graph.all_nodes.put(this.id, this);
        this.g = graph;
    }

    public double time_to_node(Node node_to) {
        return g.distanceMatrix[this.id][node_to.id];
    }

    public double time_at_node() {
        return servicet;
    }
}
