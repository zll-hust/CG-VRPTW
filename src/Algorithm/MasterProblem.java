package Algorithm;

import Parameters.Parameters;
import VRPTW.Customer;
import VRPTW.Graph;
import VRPTW.Path;
import ilog.concert.*;
import ilog.cplex.IloCplex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author： zll-hust
 * @date： 2021/2/22 17:10
 * @description： TODO
 */
public class MasterProblem {
    public IloCplex cplex;
    public IloObjective total_cost;
    public Map<Customer, IloRange> row_customers; // 存储约束方程
    public Map<Customer, Double> lambda; // 存储对偶变量
    public List<IloConversion> mipConversion;
    public double lastObjValue;
    public Graph g;
    public List<Path> paths;

    public MasterProblem(Graph g, List<Path> paths) {
        this.g = g;
        this.paths = paths;
        createModel();
        createDefaultPaths();
        Parameters.configureCplex(this);
    }

    public void createModel() {
        try {
            cplex = new IloCplex();
            total_cost = cplex.addMinimize();  // objective (10)
            row_customers = new HashMap<Customer, IloRange>();
            lambda = new HashMap<Customer, Double>();
            mipConversion = new ArrayList<IloConversion>();

            for (Customer customer : g.all_customers.values()) //todo  1  Parameters.ColGen.M
                row_customers.put(customer, cplex.addRange(1, 1, "cust " + customer.id));  // constraints (11)
        } catch (IloException e) {
            System.err.println("Concert exception caught: " + e);
        }
    }

    public void createDefaultPaths() {
        for (Customer c : g.all_customers.values()) {
            List<Integer> new_path = new ArrayList<Integer>();
            new_path.add(g.depot_start.id);
            new_path.add(c.id);
            new_path.add(g.depot_end.id);
            addNewColumn(new Path(new_path, paths, g));
        }
    }

    public void addNewColumn(Path path) {
        try {
            IloColumn new_column = cplex.column(total_cost, path.cost);
            for (Customer c : g.all_customers.values())
                new_column = new_column.and(cplex.column(row_customers.get(c), path.ifContainsCus(c)));
            path.theta = cplex.numVar(new_column, 0, 1, "y." + path.id); //todo theta 1  Parameters.ColGen.M
        } catch (IloException e) {
            System.err.println("Concert exception caught: " + e);
        }
    }

    public void saveDualValues() {
        try {
            for (Customer c : g.all_customers.values())
                lambda.put(c, cplex.getDual(row_customers.get(c)));
        } catch (IloException e) {
            System.err.println("Concert exception caught: " + e);
        }
    }

    public void solveRelaxation() {
        try {
//            System.out.println(cplex.getModel());
            if (cplex.solve()) {
                saveDualValues();
                lastObjValue = cplex.getObjValue();

//                System.out.print("[ ");
//                for (Customer c : g.all_customers.values())
//                    System.out.print(String.format("%3.2f", lambda.get(c)) + " ");
//                System.out.print(" ]\n");
            }
        } catch (IloException e) {
            System.err.println("Concert exception caught: " + e);
        }
    }

    public void convertToMIP() {
        try {
            for (Path path : paths) {
                mipConversion.add(cplex.conversion(path.theta, IloNumVarType.Bool));
                cplex.add(mipConversion.get(mipConversion.size() - 1));
            }
        } catch (IloException e) {
            System.err.println("Concert exception caught: " + e);
        }
    }

    public void solveMIP() {
        try {
            convertToMIP();
            if (cplex.solve()) {
                displaySolution();
                //logger.writeLog(instance, cplex.getObjValue(), cplex.getBestObjValue());
            } else {
                System.out.println("Integer solution not found");
            }
        } catch (IloException e) {
            System.err.println("Concert exception caught: " + e);
        }
    }

    public void displaySolution() {
        try {
            double totalCost = 0;
            System.out.println("\n" + "--- Solution >>> ------------------------------");
            for (Path path : paths) {
                if (cplex.getValue(path.theta) > 0.99999) {
                    totalCost += path.displayInfo();
                }
            }
            System.out.println("Total cost = " + totalCost);
            System.out.println("\n" + "--- Solution <<< ------------------------------");
        } catch (IloException e) {
            System.err.println("Concert exception caught: " + e);
        }
    }
}
