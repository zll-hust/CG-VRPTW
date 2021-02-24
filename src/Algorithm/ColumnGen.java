package Algorithm;

import Timer.Timer;
import VRPTW.Graph;
import VRPTW.Instance;
import VRPTW.Path;
import Parameters.Parameters;

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
    public SubProblem_Pulse subproblem;
    public Graph g;

    public ColumnGen(String instance) {
        this.instance = new Instance(instance);
        this.g = this.instance.ReadDataFromFile();
        this.paths = new ArrayList<Path>();
        this.masterproblem = new MasterProblem(g, paths);
        this.watch = new Timer();
    }

    public void runColumnGeneration() {
        int iteration_counter = 0;
        watch.start();
        do {
            iteration_counter++;
//            if(iteration_counter == 3)
//                break;
            masterproblem.solveRelaxation();
            subproblem = new SubProblem_Pulse(masterproblem.lambda, g, instance, g.depot_start.startTw, g.depot_start.endTw, (g.depot_start.endTw - g.depot_start.startTw) / 4);
            List<Integer> path = subproblem.runPulseAlgorithm();
            masterproblem.addNewColumn(new Path(path, paths, g));
            displayIteration(iteration_counter);
        } while (subproblem.objValue < Parameters.ColGen.zero_reduced_cost_AbortColGen);

//        masterproblem.solveMIP();
        masterproblem.solveRelaxation();
        masterproblem.displaySolution();
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
            System.out.print("      SB int");
            System.out.println();
        }
        System.out.format("%9.0f", (double) iter);
        System.out.format("%9.1f", watch.getSecond());
        System.out.format("%9.0f", (double) paths.size());
        System.out.format("%15.2f", masterproblem.lastObjValue);//master lower bound
        System.out.format("%12.4f", subproblem.objValue);
        System.out.println();
    }
}
