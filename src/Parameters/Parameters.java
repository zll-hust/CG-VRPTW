package Parameters;

/**
 * @author： zll-hust
 * @date： 2021/2/22 17:03
 * @description： TODO
 */

import Algorithm.ColumnGen;
import Algorithm.MasterProblem;
import ilog.concert.IloException;
import ilog.cplex.IloCplex;

public class Parameters {
	public static double capacity = 200;
	public static class ColGen {
		public static boolean abort = false;
		public static double zero_reduced_cost = -0.0001;
		public static double zero_reduced_cost_AbortColGen = -0.005;
		public static double subproblemTiLim = 5000;
		public static double subproblemObjVal = -1000;
		public static int M = 10000;
	}
	public static void configureCplex(MasterProblem masterproblem) {
		try {
			// branch and bound
			masterproblem.cplex.setParam(IloCplex.Param.MIP.Strategy.NodeSelect, 1);
			masterproblem.cplex.setParam(IloCplex.Param.MIP.Strategy.Branch,1);
			//masterproblem.cplex.setParam(IloCplex.Param.Preprocessing.Presolve, true);
			// display options
			masterproblem.cplex.setParam(IloCplex.Param.MIP.Display, 2);
			masterproblem.cplex.setParam(IloCplex.Param.Tune.Display, 1);
			masterproblem.cplex.setParam(IloCplex.Param.Simplex.Display, 0);
		}
		catch (IloException e) {System.err.println("Concert exception caught: " + e);}
	}
}