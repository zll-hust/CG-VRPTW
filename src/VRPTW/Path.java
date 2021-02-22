package VRPTW;

import ilog.concert.IloNumVar;

import java.util.ArrayList;
import java.util.List;

/**
 * @author： zll-hust
 * @date： 2021/2/22 17:10
 * @description： TODO
 */
public class Path {
    public int id;
    public List<Customer> customers;
    public IloNumVar theta;
    public double cost;
    public Graph g;
    public List<Path> paths;

    public Path(List<Integer> stops_new_path, List<Path> paths) {
        this.paths = paths;
        customers = new ArrayList<Customer>();
        for (int i = 1; i < stops_new_path.size() - 1; i++) {
            customers.add(g.all_customers.get(stops_new_path.get(i)));
        }
        calculateCost();
        id = paths.size();
        paths.add(this);
    }

    private void calculateCost() {
        if (customers.size() > 0) {
            cost = g.depot_start.time_to_node(customers.get(0)) + customers.get(0).time_at_node();
            for (int i = 1; i < customers.size(); i++) {
                cost += customers.get(i - 1).time_to_node(customers.get(i)) + customers.get(i).time_at_node();
            }
            cost += customers.get(customers.size() - 1).time_to_node(g.depot_end);
        } else {
            cost = 0;
        }
    }

    public int ifContainsCus(Customer customer) {
        return customers.contains(customer) ? 1 : 0;
    }

    public double displayInfo() {
        double trvalCost = 0;
        System.out.println("Path id   : " + id);
        System.out.print("Stops     : depot->");
        trvalCost += g.depot_start.time_to_node(customers.get(0));
        for (int i = 1; i < customers.size(); i++) {
            trvalCost += customers.get(i - 1).time_to_node(customers.get(i));
        }
        trvalCost += customers.get(customers.size() - 1).time_to_node(g.depot_end);
        for (Customer c : customers) {
            System.out.print(c.id_external + "->");
        }
        System.out.println("depot");
        System.out.println("trvalCost : " + trvalCost);
        return trvalCost;
    }
}
