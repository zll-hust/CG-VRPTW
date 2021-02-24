package Main;

/**
 * @author： zll-hust
 * @date： 2021/2/22 17:00
 * @description： TODO
 */

import Algorithm.ColumnGen;
import Timer.Timer;
import VRPTW.Instance;
import ilog.concert.*;
import ilog.cplex.*;

public class Main {
    public static void main(String[] args) {
        ColumnGen col_gen = new ColumnGen("input\\Solomon\\50_customer\\c101.txt"); //"input\\Solomon\\25_customer\\c101.txt"
        col_gen.runColumnGeneration();
    }
}
