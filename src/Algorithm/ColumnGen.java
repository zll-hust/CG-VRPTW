package Algorithm;

import Timer.Timer;
import VRPTW.Graph;
import VRPTW.Instance;
import VRPTW.Path;

import java.util.ArrayList;
import java.util.List;

/**
 * @author： zll-hust
 * @date： 2021/2/22 17:11
 * @description： TODO
 */
public class ColumnGen {
    public Timer watch;
    public Instance instance;
    public List<Path> paths;
    public MasterProblem masterproblem;
    public SubProblem subproblem;
    public Graph g;

    public ColumnGen(String instance) {
        this.instance = new Instance(instance);
        this.g = this.instance.ReadDataFromFile();
        this.masterproblem = new MasterProblem(g, paths);
        this.subproblem = new SubProblem();
        this.watch = new Timer();
        this.paths = new ArrayList<Path>();
    }

    public void runColumnGeneration() {
        int iteration_counter = 0;
        watch.start();
//        do {
            iteration_counter++;
            masterproblem.solveRelaxation();
//            subproblem.updateReducedCost();
//            subproblem.solve();
            displayIteration(iteration_counter);
//        } while (subproblem.lastObjValue < Parameters.ColGen.zero_reduced_cost_AbortColGen && iteration_counter != 2);
//
//        masterproblem.solveMIP();
        watch.stop();
        System.out.println(watch);
    }

    private void displayIteration(int iter) {
        if ((iter) % 20 == 0 || iter == 1) {
            System.out.println();
            System.out.print("Iteration");
            System.out.print("     SbTime");
            System.out.print("   nPaths");
            System.out.print("       MP lb");
            System.out.print("       SB lb");
            System.out.print("      SB int");
            System.out.println();
        }
        System.out.format("%9.0f", (double) iter);
        System.out.format("%9.1f", watch.getSecond());
        System.out.format("%9.0f", (double) paths.size());
        System.out.format("%12.4f", masterproblem.lastObjValue);//master lower bound
//        System.out.format("%12.4f", subproblem.lastObjValueRelaxed);//sb lower bound
//        System.out.format("%12.4f", subproblem.lastObjValue);//sb lower bound
        System.out.println();
    }
}
