package VRPTW;

/**
 * @author： zll-hust
 * @date： 2021/2/22 17:14
 * @description： TODO
 */
public class Customer extends Node {
    public Customer(Graph graph, int external_id, double x, double y, double demand, double startTw, double endTw, double service_time) {
        super(graph, external_id, x, y, demand, startTw, endTw, service_time);
        graph.all_customers.put(this.id, this);
    }
}
