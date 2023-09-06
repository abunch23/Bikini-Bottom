import java.io.*;
import java.util.*;
import java.util.List;

class Main {
  public static void main(String[] args) throws IOException {
    BikiniBottomDeliverySimulation simulation = new BikiniBottomDeliverySimulation();
    String inputFile = "cycle_" + 10 + ".txt";
    String outputFile = "cycle" + 10 + "_results.txt";
    simulation.processCycle(inputFile);
    simulation.writeCycleResults(outputFile);
    // for (int cycle = 1; cycle <= 10; cycle++) {
    //   String inputFile = "cycle" + cycle + ".txt";
    //   String outputFile = "cycle" + cycle + "_results.txt";
    //   simulation.processCycle(inputFile);
    //   simulation.writeCycleResults(outputFile);
    // }
  }  
}