package VRPTW;

import java.util.HashMap;
import java.util.Map;

/**
 * @author： zll-hust
 * @date： 2021/2/22 17:10
 * @description： TODO
 */
public class Graph {
    public Depot depot_start;
    public Depot depot_end;
    public Map<Integer, Customer> all_customers;
    public Map<Integer, Node> all_nodes;
    public double[][] distanceMatrix;

    public Graph() {
        this.all_customers = new HashMap<Integer, Customer>();
        this.all_nodes = new HashMap<Integer, Node>();
    }

    public void buildMatrix() {
        distanceMatrix = new double[all_nodes.size()][all_nodes.size()];
        for (int i : all_nodes.keySet()) {
            if (all_nodes.get(i).id == depot_end.id) {
                for (int j : all_nodes.keySet()) {
                    distanceMatrix[all_nodes.get(i).id][all_nodes.get(j).id] = Double.POSITIVE_INFINITY;
                }
            } else {
                for (int j : all_nodes.keySet()) {
                    if (all_nodes.get(j).id == depot_start.id) {
                        distanceMatrix[all_nodes.get(i).id][all_nodes.get(j).id] = Double.POSITIVE_INFINITY;
                    } else if (all_nodes.get(i).id_external == all_nodes.get(j).id_external) {
                        distanceMatrix[all_nodes.get(i).id][all_nodes.get(j).id] =
                                distanceMatrix[all_nodes.get(j).id][all_nodes.get(i).id] =
                                        Double.POSITIVE_INFINITY;
                    } else {
                        distanceMatrix[all_nodes.get(i).id][all_nodes.get(j).id] =
                                distanceMatrix[all_nodes.get(j).id][all_nodes.get(i).id] =
                                        Math.sqrt(Math.pow(all_nodes.get(i).xcoord - all_nodes.get(j).xcoord, 2) + Math.pow(all_nodes.get(i).ycoord - all_nodes.get(j).ycoord, 2));
                    }
                }
            }
        }
    }
}
