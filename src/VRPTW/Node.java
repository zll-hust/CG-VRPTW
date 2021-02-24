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
    public double startTw;
    public double endTw;
    private double demand;

    public Node(Graph graph, int external_id, double x, double y, double demand, double startTw, double endTw, double service_time) {
        this.id = graph.all_nodes.size();
        this.id_external = external_id;
        this.xcoord = x;
        this.ycoord = y;
        this.demand = demand;
        this.startTw = startTw;
        this.endTw = endTw;
        this.servicet = service_time;
        graph.all_nodes.put(this.id, this);
        this.g = graph;
    }

    public double time_to_node(Node node_to) {
        return g.distanceMatrix[this.id][node_to.id];
    }

    public double time_at_node() {
        return servicet;
    }


    public double getStartTw() {
        return startTw;
    }

    public void setStartTw(double startTw) {
        this.startTw = startTw;
    }

    public double getEndTw() {
        return endTw;
    }

    public void setEndTw(double endTw) {
        this.endTw = endTw;
    }

    public double getDemand() {
        return demand;
    }

    public void setDemand(double demand) {
        this.demand = demand;
    }
}
