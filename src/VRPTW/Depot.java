package VRPTW;

/**
 * @author： zll-hust
 * @date： 2021/2/22 17:14
 * @description： TODO
 */
public class Depot extends Node {
    public Depot(Graph graph, int external_id, double x, double y, double startTw, double endTw) {
        super(graph, external_id, x, y, 0, startTw, endTw, 0);
    }
}
