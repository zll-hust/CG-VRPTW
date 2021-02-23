package Algorithm;

import VRPTW.Customer;
import VRPTW.Graph;
import VRPTW.Instance;
import VRPTW.Path;

import java.util.*;

/**
 * @author： zll-hust
 * @date： 2021/2/22 17:11
 * @description：
 */
public class SubProblem_Pulse {
    public Graph g;
    public Instance instance;
    public double lower_time;
    public double upper_time;
    public double step;
    public Map<Customer, Double> lambda;
    public boolean[] isVisited;
    public double[] best_cost_cus; // 每个点到终点的最小目标值
    public double[][] lower_bound_matrix;
    public double time_incumbent;

    public SubProblem_Pulse(Map<Customer, Double> lambda, Graph g, double lower_time, double upper_time) {
        for (Customer c : lambda.keySet()) {
            this.lambda.put(c, lambda.get(c));
        }

        this.g = g;
        this.lower_time = lower_time;
        this.upper_time = upper_time;
        this.lower_bound_matrix = new double[g.all_customers.size()][(int) ((upper_time - lower_time) / step)];
        this.isVisited = new boolean[g.all_customers.size()];
        Arrays.fill(isVisited, false);
        this.best_cost_cus = new double[g.all_customers.size()];
        Arrays.fill(best_cost_cus, Double.POSITIVE_INFINITY);
    }

    public void bounding_scheme() {
        int bound_index = 0;
        time_incumbent = upper_time - step;
        List<Integer>[] prePath = new ArrayList[g.all_customers.size()];
        while (time_incumbent >= lower_time) {
            for (int root : g.all_customers.keySet()) {
                List<Integer> p = new ArrayList<>();
                pulse_procedure(root, root, 0.0, 0.0, time_incumbent, p);
                if (!prePath[root].isEmpty() && p.isEmpty())
                    p = prePath[root];
                lower_bound_matrix[root][bound_index] = best_cost_cus[root];
                prePath[root] = p;
            }

            time_incumbent -= step;
            bound_index += 1;
        }
    }

    public double reduced_cost(List<Integer> path) {
        if (path.isEmpty())
            return 0.0;
        double total_cost = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            total_cost += g.all_nodes.get(path.get(i)).time_to_node(g.all_nodes.get(path.get(i + 1)));
            if (i != 0)
                total_cost += lambda.get(g.all_nodes.get(path.get(i)));
        }
        return total_cost;
    }

    public boolean is_feasible(int cur, double capacity, double time) {
        if (capacity + g.all_customers.get(cur).getDemand() > instance.max_capacity || time > g.all_customers.get(cur).getStartTw()) //prune condition
            return false;
        return true;
    }

    public boolean check_bounds(int root, int cur, double time, double cost) {
        double lower_bound = 0;
        if (time < time_incumbent + step) {
//            double diff_time = time_incumbent + step - time;
//            lower_bound = diff_time * naive_dual_bound + overall_best_cost;
        } else {
            if (!isVisited[cur])
                return true;
            lower_bound = lower_bound_matrix[cur][(int) ((upper_time - time) / step)];
        }

        double best_cost = best_cost_cus[root];
        if (cost + lower_bound >= best_cost) //prune condition
            return false;
        return true;
    }

    public boolean rollback(int cur, double cost, List<Integer> path) {
        if (path.size() < 2)
            return true;
        List<Integer> alt_path = new ArrayList<>();
        alt_path.addAll(path);
        alt_path.remove(alt_path.size() - 1);
        alt_path.add(cur);
        if (cost >= reduced_cost(alt_path)) //prune condition
            return false;
        return true;
    }

    public void dynamic_update(int cur, List<Integer> opt_path, double time) {
        List<Integer> part_path = new ArrayList<>();
        boolean flag = false;
        for (int i = 0; i < opt_path.size() - 1; i++) {
            if (i == cur)
                flag = true;
            if (flag)
                part_path.add(opt_path.get(i));
        }
        double path_cost = reduced_cost(part_path);
        lower_bound_matrix[cur][(int) ((upper_time - time) / step)] = Math.min(path_cost, lower_bound_matrix[cur][(int) ((upper_time - time) / step)]);
    }

    /*
     *  root: 要到达的根节点；cur: 当前所在的节点；cost: 目前的总花费（reduce cost）
     *  capacity: 目前的总容量；time: 目前的总时间；path: 目前的路径；flag:
     */
    public void pulse_procedure(int root, int cur, double cost, double capacity, double time, List<Integer> path) {
        if (time < g.all_customers.get(cur).getStartTw()) // 更新时间
            time = g.all_customers.get(cur).getStartTw();
        if (!is_feasible(cur, capacity, time) || !check_bounds(root, cur, time, cost) || !rollback(cur, cost, path))
            return;
        List<Integer> opt_path = new ArrayList<>();
        path.add(cur);
        double nx_cost = 0.0, nx_capacity = capacity + g.all_customers.get(cur).getDemand(), nx_time = 0.0;
        for (int nx : g.all_nodes.keySet()) {
            List<Integer> new_path = new ArrayList<>();
            new_path.addAll(path); //
            nx_cost = cost + g.all_customers.get(cur).time_to_node(g.all_customers.get(nx));
            nx_time = Math.max(g.all_customers.get(nx).getStartTw(), time + g.all_customers.get(cur).time_to_node(g.all_customers.get(nx)));
            if (!isVisited[nx]) {
                isVisited[nx] = true;
                pulse_procedure(root, nx, nx_cost, nx_capacity, nx_time, new_path);
                isVisited[nx] = false;
            }
            if (!new_path.isEmpty() && new_path.get(new_path.size() - 1) == g.depot_end.id && ((opt_path.isEmpty()) || reduced_cost(new_path) < reduced_cost(opt_path))) {
                opt_path = new_path;
                dynamic_update(cur, opt_path, time);
            }

            if (path.get(path.size() - 1) != g.depot_end.id) {
                path = opt_path;
            }
        }

        if (!path.isEmpty() && path.get(path.size() - 1) == g.depot_end.id) {
            double tmp = reduced_cost(path);
            best_cost_cus[root] = Math.min(best_cost_cus[root], tmp);
        }
    }

    public void runPulseAlgorithm() {
        bounding_scheme();
        List<Integer> opt_path = new ArrayList<>();
        pulse_procedure(g.depot_start.id, g.depot_start.id, 0.0, 0.0, 0.0, opt_path);
    }
}