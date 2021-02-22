package VRPTW;

/**
 * @author： zll-hust
 * @date： 2021/2/22 17:14
 * @description： TODO
 */
public class Customer extends Node {
    private double demand;
    private double startTw;
    private double endTw;

    public Customer(Graph graph, int external_id, double x, double y, double demand, double startTw, double endTw, double service_time) {
        super(graph, external_id, x, y, service_time);
        this.demand = demand;
        this.startTw = startTw;
        this.endTw = endTw;
        graph.all_customers.put(this.id, this);
    }

    public double getDemand() {
        return demand;
    }

    public void setDemand(double demand) {
        this.demand = demand;
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
}
