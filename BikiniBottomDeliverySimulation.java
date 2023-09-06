import java.util.*;
import java.io.*;

public class BikiniBottomDeliverySimulation {
      final int SECONDS_PER_BLOCK = 30;
      final int SECONDS_PER_HOUSE_UP_DOWN = 3;
      final int SECONDS_PER_PACKAGE_DELIVERY = 60;
      final int MAX_UNITS_FOR_ONE_TRIP = 100;
  
      final int HOURS_PER_DAY = 8;
      final int ADDITIONAL_HOURS_RATE = 45;
      final int MAX_HOURS_PER_DAY = 16;
  
      final int MILES_BETWEEN_STREETS = 200;
      final int MILES_BETWEEN_AVENUES = 1000;
      final int TOTAL_STREETS = 500;
      final int TOTAL_AVENUES = 100;
      final int TOTAL_BLOCKS = 1000;
  
      final int TRUCK_PURCHASE_COST = 100000;
      final int TRUCK_RENTAL_COST_PER_DAY = 15000;
      final int FUEL_COST_PER_MILE = 5;
      final int MAINTENANCE_DISTANCE = 100;
      final int MAINTENANCE_COST = 1000;
      final int EMPLOYEE_COST_PER_HOUR = 30;
      
      String cycleNumber;
      int totalPackages;
      ArrayList<Optimization.Address> homeAddresses;
      int spongeBobUnits;
      int patrickUnits;
  
      int totalHomes;
      int totalDeliveryTime;
      int totalTruckCost;
      int totalGasCost;
      int totalEmployeeCost;
      int cycleCost;
  
      public BikiniBottomDeliverySimulation() {
        homeAddresses = new ArrayList<>();
      }

      public ArrayList<Optimization.Address> readFile(String path) {
        Optimization d = new Optimization();
        File f = new File(path);
        try {
          Scanner s = new Scanner(f);
          cycleNumber = s.nextLine();
          totalPackages = Integer.parseInt(s.nextLine());
          
          while(true) {
            String home = s.nextLine();
            //reference: 2s,22a,BB
            if (home.equals("SpongeBob Complex")) {
              break;
            }
            String[] parts = home.split(",");
            int street = Integer.parseInt(parts[0].replaceAll("s", ""));
            int avenue = Integer.parseInt(parts[1].replaceAll("a", ""));
            if (parts[2].length() == 2) {
              street = street + 250;
            }
            Optimization.Address a = d.new Address(street, avenue);
            homeAddresses.add(a);  
          }
          spongeBobUnits = Integer.parseInt(s.nextLine());
          String junk_2 = s.nextLine();
          patrickUnits = Integer.parseInt(s.nextLine());
          
        } catch (FileNotFoundException e) {
          System.out.println("File not found: " + path);
        }
        totalHomes = homeAddresses.size();
        return homeAddresses;
      } 
  
      public static ArrayList<ArrayList<Optimization.Address>> splitRoute(ArrayList<Optimization.Address> route, int numTrucks) {
        ArrayList<ArrayList<Optimization.Address>> truckRoutes = new ArrayList<>();
        Optimization q = new Optimization();
        for (int i = 0; i < 4; i++) {
          truckRoutes.add(new ArrayList<>());
        }
        
        int truckIndex = 0;
        for (int i = 0; i < route.size(); i+=2) {
          Optimization.Address root = route.get(i);
          Optimization.Address end = route.get(i+1);
                
          truckRoutes.get(truckIndex).add(root);
          truckRoutes.get(truckIndex).add(end);
    
          truckIndex = (truckIndex + 1) % 4;
        }
    
        for (int i = 0; i < truckRoutes.size(); i++) {
          Optimization.Graph truck_graph = q.generateGraph(truckRoutes.get(i));
          ArrayList<Optimization.Address> optimizedRoute = q.kruskalAlgorithm(truck_graph);
          truckRoutes.set(i, optimizedRoute);
        }
    
        return truckRoutes;
      }
  
      void processCycle(String path) {
        Optimization o = new Optimization();
        ArrayList<Optimization.Address> a = readFile(path);
        Optimization.Graph g = o.generateGraph(a);
        ArrayList<Optimization.Address> min_span_tree = o.kruskalAlgorithm(g);
        totalHomes = TOTAL_BLOCKS * 20 - 20 - 300 - 500;
        int numTrucks = 4;
        int numEmployees = 4;
        boolean buyTrucks = false;
        ArrayList<ArrayList<Optimization.Address>> truckRoutes = splitRoute(min_span_tree, 4);
        ArrayList<Optimization.Address> truck1 = truckRoutes.get(0);
        ArrayList<Optimization.Address> truck2 = truckRoutes.get(1);
        ArrayList<Optimization.Address> truck3 = truckRoutes.get(2);
        ArrayList<Optimization.Address> truck4 = truckRoutes.get(3);
        int time1 = calculateTotalDeliveryTime(truck1);
        int time2 = calculateTotalDeliveryTime(truck2);
        int time3 = calculateTotalDeliveryTime(truck3);
        int time4 = calculateTotalDeliveryTime(truck4);

        ArrayList<Integer> times = new ArrayList<Integer>(Arrays.asList(time1, time2, time3, time4));
        
        int minTime = Collections.min(times); 
         ArrayList<Optimization.Address> shortestTruck = truckRoutes.get(times.indexOf(minTime));
        Optimization.Address lastAddress = shortestTruck.get(shortestTruck.size() - 1);
        int shortestIndex = times.indexOf(minTime);
        
        int x_dist = Math.abs(lastAddress.x - 149);
        int y_dist = Math.abs(lastAddress.y - 33);
        int distancePatrick= x_dist+y_dist;
        
        int patrickTime=(distancePatrick*30+patrickUnits*30)/3600;

        int complex_x = 149 - 2;
        int complex_y = 33 - 3;
        int distanceSpongeBob = complex_x+complex_y;
        int spongeBobTime = (distanceSpongeBob*30+spongeBobUnits*30)/3600;
        
        times.set(shortestIndex, minTime+patrickTime+spongeBobTime);
        
        totalDeliveryTime = Collections.max(times);
        calculateCycleCost(numEmployees, numTrucks, buyTrucks);
      }
  
      int calculateTotalDeliveryTime(ArrayList<Optimization.Address> route) {
        int totalTime = 0;
        for (int i = 0; i < route.size() - 1; i++) {
          Optimization.Address current = route.get(i);
          Optimization.Address next = route.get(i+1);
          int blocks = (next.getY() - current.getY()) + (next.getX() - current.getX());
          int transport_time = blocks * 30;
          totalTime += transport_time;
          if (!(blocks == 0)) {
            totalTime += 60;
          }
        }
        totalTime+=totalHomes;
        return totalTime/3600; 
      }

      int calculateEmployeeCost(int hours) {
        int hourlyRate = EMPLOYEE_COST_PER_HOUR;
        if (hours > HOURS_PER_DAY) {
            hourlyRate = ADDITIONAL_HOURS_RATE;
            hours = Math.min(hours, MAX_HOURS_PER_DAY);
        }
        return hours * hourlyRate;
      }
      
      void calculateCycleCost(int numEmployees, int numTrucks, boolean buyTrucks) {
        int numDays;
        int hours;
        if (totalDeliveryTime<HOURS_PER_DAY)
        {
          numDays=1;
          hours=totalDeliveryTime;
        } else {
          numDays=(int)(totalDeliveryTime/HOURS_PER_DAY)+1;
          hours=totalDeliveryTime/numDays;
        }
        totalEmployeeCost = calculateEmployeeCost(hours) * numEmployees * numDays;
        totalTruckCost = 15000*numTrucks;//change later!!
        totalGasCost = (totalHomes * 100)/5000 * FUEL_COST_PER_MILE;
        cycleCost = totalEmployeeCost + totalTruckCost + totalGasCost;
      }
      
      void writeCycleResults(String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("Cycle " + cycleNumber + " Results:\n");
            writer.write("Total Packages Delivered: " + totalPackages + "\n");
            writer.write("Total Delivery Time: " + (totalDeliveryTime) + " hours\n");
            writer.write("Total Employee Cost: $" + totalEmployeeCost + "\n");
            writer.write("Total Truck Cost: $" + totalTruckCost + "\n");
            writer.write("Total Gas Cost: $" + totalGasCost + "\n");
            writer.write("Overall Total Cycle Cost: $" + cycleCost + "\n");
        }
    }
      
}