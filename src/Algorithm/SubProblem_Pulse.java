package Algorithm;

import VRPTW.Customer;
import VRPTW.Graph;
import VRPTW.Path;

import java.util.*;

/**
 * @author： zll-hust
 * @date： 2021/2/22 17:11
 * @description：
 */
public class SubProblem_Pulse {
    public Graph g;
    public double lower_time;
    public double upper_time;
    public double step;
    public List<Integer> optPath;
    public Map<Customer, Double> lambda;
    public boolean[] isVisited;
    public double[][] lower_bound_matrix;
    public double time_incumbent;

    public SubProblem_Pulse(Map<Customer, Double> lambda, Graph g, double lower_time, double upper_time) {
        for(Customer c : lambda.keySet()){
            this.lambda.put(c, lambda.get(c));
        }

        this.optPath = new ArrayList<>();
        this.g = g;
        this.lower_time = lower_time;
        this.upper_time = upper_time;
        this.lower_bound_matrix = new double[g.all_customers.size()][(int)((upper_time - lower_time) / step)];
        this.isVisited = new boolean[g.all_customers.size()];
        Arrays.fill(isVisited, false);
    }

    public void bounding_scheme(){
        int bound_index = 0;
        time_incumbent = upper_time - step;
        List<Integer>[] prePath = new ArrayList[g.all_customers.size()];
        while (time_incumbent >= lower_time) {
            for(int root : g.all_customers.keySet()){
                List<Integer> p = new ArrayList<>();
                pulse_procedure(root, root, 0.0, 0.0, time_incumbent, p, false);
                if (!prePath[root].isEmpty() && p.isEmpty())
                    p = prePath[root];

                prePath[root] = p;
            }
        }
    }

    /*
     *  root: 要到达的根节点；cur: 当前所在的节点；cost: 目前的总花费（reduce cost）
     *  capacity: 目前的总容量；time: 目前的总时间；path: 目前的路径；flag:
     */
    public void pulse_procedure(int root, int cur, double cost, double capacity, double time, List<Integer> path, boolean flag) {
        if (time < g.all_customers.get(cur).getStartTw()) // 更新时间
            time = g.all_customers.get(cur).getStartTw();
        if (!is_feasible(cur, capacity, time) || !check_bounds(root, cur, time, cost, flag) || !rollback(cur, cost, path))
            return;
        if (!concat(root, cur, time, cost, capacity, path, flag)) {
            List<Integer> opt_path = new ArrayList<>(); //todo
            path.add(cur);
            double nx_cost = 0.0, nx_capacity = capacity + g.all_customers.get(cur).getDemand(), nx_time = 0.0;
            for (int nx : g.all_nodes.keySet()) {
                List<Integer> new_path = new ArrayList<>();
                new_path.addAll(path); //
                nx_cost = cost + g.all_customers.get(cur).time_to_node(g.all_customers.get(nx));
                nx_time = Math.max(g.all_customers.get(nx).getStartTw(), time + g.all_customers.get(cur).time_to_node(g.all_customers.get(nx)));
                if (!isVisited[nx]) {
                    isVisited[nx] = true;
                    pulse_procedure(root, nx, nx_cost, nx_capacity, nx_time, new_path, flag);
                    isVisited[nx] = false;
                }
                if (!new_path.isEmpty() && new_path.get(new_path.size() - 1) == g.depot_end.id && ((opt_path.isEmpty()) || reduced_cost(new_path) < reduced_cost(opt_path))) {
                    opt_path = new_path;
                    dynamic_update(cur, opt_path);
                }
            }
        }
    }

    public void runPulseAlgorithm(){
        bounding_scheme();
        pulse_procedure();
    }
}
