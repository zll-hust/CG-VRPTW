package Algorithm;

import VRPTW.Customer;
import VRPTW.Graph;
import VRPTW.Instance;

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
    public double objValue;

    public SubProblem_Pulse(Map<Customer, Double> lambda, Graph g, Instance instance, double lower_time, double upper_time, double step) {
        this.lambda = lambda;
        this.g = g;
        this.instance = instance;
        this.lower_time = lower_time;
        this.upper_time = upper_time;
        this.step = step;
        this.lower_bound_matrix = new double[g.all_nodes.size()][(int) ((upper_time - lower_time) / step) + 1];
        for(int i = 0; i < lower_bound_matrix.length; i++)
            Arrays.fill(lower_bound_matrix[i], Double.NEGATIVE_INFINITY);
        this.isVisited = new boolean[g.all_nodes.size()];
        Arrays.fill(isVisited, false);
        this.best_cost_cus = new double[g.all_nodes.size()];
        Arrays.fill(best_cost_cus, Double.POSITIVE_INFINITY);
    }

    public void bounding_scheme() {
        int bound_index = 0;
        time_incumbent = upper_time - step;
        isVisited[g.depot_start.id] = true;
        while (time_incumbent >= lower_time) {
            for (int root : g.all_customers.keySet()) {
                List<Integer> p = new ArrayList<>();
                pulse_procedure(root, root, 0.0, 0.0, time_incumbent, p);
                lower_bound_matrix[root][bound_index] = best_cost_cus[root];
            }
            time_incumbent -= step;
            bound_index += 1;
        }
        isVisited[g.depot_start.id] = false;
    }

    public double reduced_cost(List<Integer> path) {
        if (path.isEmpty() || path.size() == 1)
            return 0.0;
        double total_cost = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            total_cost += g.all_nodes.get(path.get(i)).time_to_node(g.all_nodes.get(path.get(i + 1)));
//            total_cost += g.all_nodes.get(path.get(i)).time_to_node(g.all_nodes.get(path.get(i + 1))) + g.all_nodes.get(path.get(i)).time_at_node(); //todo
            if (g.all_nodes.get(path.get(i)).id != g.depot_start.id) {
                total_cost -= lambda.get(g.all_nodes.get(path.get(i)));
            }
        }
        return total_cost;
    }

    public boolean is_feasible(int cur, double capacity, double time) {
        if (isVisited[cur])
            return false;
        if (capacity + g.all_nodes.get(cur).getDemand() > instance.max_capacity || time > g.all_nodes.get(cur).getEndTw()) //prune condition
            return false;
        return true;
    }

    public boolean check_bounds(int root, int cur, double time, double cost) {
        double lower_bound = lower_bound_matrix[cur][(int) ((upper_time - time) / step)];
        double best_cost = best_cost_cus[root];
        if (cost + lower_bound >= best_cost) //prune condition todo
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
        // 算例若满足三角不等式，时间也应该一定更小。
        if (cost >= reduced_cost(alt_path)) //prune condition
            return false;
        return true;
    }

    /*
     *  root: 要到达的根节点；cur: 当前所在的节点；cost: 目前的总花费（reduce cost）
     *  capacity: 目前的总容量；time: 目前的总时间；path: 目前的路径；flag:
     */
    public void pulse_procedure(int root, int cur, double cost, double capacity, double time, List<Integer> path) {
        if (time < g.all_nodes.get(cur).getStartTw()) // 更新时间
            time = g.all_nodes.get(cur).getStartTw();
        if (!is_feasible(cur, capacity, time) || !check_bounds(root, cur, time, cost) || !rollback(cur, cost, path))
            return;
        List<Integer> opt_path = new ArrayList<>();
        path.add(cur);
        isVisited[cur] = true;
        double nx_cost = 0.0, nx_capacity = capacity + g.all_nodes.get(cur).getDemand(), nx_time = 0.0;

        if(g.all_nodes.get(cur).id != g.depot_end.id){
            for (int nx : g.all_nodes.keySet()) {
                if(nx == cur) continue;
                List<Integer> new_path = new ArrayList<>();
                new_path.addAll(path);
                if(g.all_nodes.get(cur).id == g.depot_start.id){
                    nx_cost = g.all_nodes.get(cur).time_to_node(g.all_nodes.get(nx));
                }else{
                    nx_cost = cost + g.all_nodes.get(cur).time_to_node(g.all_nodes.get(nx)) - lambda.get(g.all_nodes.get(cur)); //todo
//                    nx_cost = cost + g.all_nodes.get(cur).time_to_node(g.all_nodes.get(nx)) + g.all_nodes.get(cur).time_at_node() - lambda.get(g.all_nodes.get(cur));
                }
                nx_time = Math.max(g.all_nodes.get(nx).getStartTw(), time + g.all_nodes.get(cur).time_at_node() + g.all_nodes.get(cur).time_to_node(g.all_nodes.get(nx)));
                if (!isVisited[nx]) {
                    pulse_procedure(root, nx, nx_cost, nx_capacity, nx_time, new_path);
                }
                if (!new_path.isEmpty() && new_path.get(new_path.size() - 1) == g.depot_end.id && ((opt_path.isEmpty()) || reduced_cost(new_path) < reduced_cost(opt_path))) {
                    opt_path = new_path;
                }
            }
        }
        if (path.get(path.size() - 1) != g.depot_end.id) {
            path.clear();
            path.addAll(opt_path);
        }

        if (!path.isEmpty() && path.get(path.size() - 1) == g.depot_end.id) {
            double tmp = reduced_cost(path);
            best_cost_cus[root] = Math.min(best_cost_cus[root], tmp);
        }

        isVisited[cur] = false;
    }

    public List<Integer> runPulseAlgorithm() {
        bounding_scheme();
        List<Integer> opt_path = new ArrayList<>();
        pulse_procedure(g.depot_start.id, g.depot_start.id, 0.0, 0.0, 0.0, opt_path);
        objValue = reduced_cost(opt_path);
//        System.out.println(opt_path);

        return opt_path;
    }
}