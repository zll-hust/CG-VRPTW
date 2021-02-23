package VRPTW;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

/**
 * @author： zll-hust
 * @date： 2021/2/22 17:10
 * @description： TODO
 */
public class Instance {
    public String instanceName;
    public Graph graph;
    public double max_capacity = 200;


    public Instance(String instanceName) {
        this.instanceName = instanceName;
        this.graph = new Graph();
    }

    public Graph ReadDataFromFile() {
        try {
            System.out.println(this.instanceName);
            Scanner in = new Scanner(new FileReader(this.instanceName));

            // skip unusefull lines
            in.nextLine(); // skip filename
            in.nextLine(); // skip empty line
            in.nextLine(); // skip vehicle line
            in.nextLine();
            int vehiclesNr = in.nextInt();

            // read Q
            double vehiclesCapacity = in.nextInt();

            // skip unusefull lines
            in.nextLine();
            in.nextLine();
            in.nextLine();
            in.nextLine();
            in.nextLine();

            // read depots data
            int id_external_depot = in.nextInt();
            double xcoord_depot = in.nextDouble();
            double ycoord_depot = in.nextDouble();
            in.nextDouble();
            double startTw_depot = in.nextInt();
            double endTw_depot = in.nextInt();
            in.nextDouble();

            // read customers data
            while (in.hasNextInt()) {
                int id_external = in.nextInt();
                double xcoord = in.nextDouble();
                double ycoord = in.nextDouble();
                double demand = in.nextDouble();
                double startTw = in.nextInt();
                double endTw = in.nextInt();
                double servicet = in.nextDouble();
                // add customer to customers list
                new Customer(graph, id_external, xcoord, ycoord, demand, startTw, endTw, servicet);
            }// end for customers

            graph.depot_start = new Depot(graph, id_external_depot, xcoord_depot, ycoord_depot, startTw_depot, endTw_depot);
            graph.depot_end = new Depot(graph, id_external_depot, xcoord_depot, ycoord_depot, startTw_depot, endTw_depot);

            graph.buildMatrix();

            in.close();

        } catch (FileNotFoundException e) {
            // File not found
            System.out.println("File not found!");
            System.exit(-1);
        }

        return graph;
    }
}