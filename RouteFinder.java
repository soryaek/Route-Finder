/* 
Author: Sorya Ek
*/

import java.io.*;
import java.util.*;

public class RouteFinder {
 
  // Size of the array which stores all cities and all roads
  private ArrayList<City> cities;
  private ArrayList<Road> roads;
  
 
 /*
  * The MapProgram method opens city file and road file
  * @param cityFile 
  * @param roadFile
  */
  public RouteFinder(String cityFile, String roadFile) {
	
	// Parse city from City.dat file and Parse roads from Road.dat line by line
    cities = new ArrayList<City>();
    roads = new ArrayList<Road>();
   
    //Check for wrong input
    try {
      BufferedReader br = new BufferedReader(new FileReader(cityFile));
      for(String line; (line = br.readLine()) != null; ) {
        addCity(line);
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    //Check for wrong input
    try {
      BufferedReader br = new BufferedReader(new FileReader(roadFile));
      for(String line; (line = br.readLine()) != null; ) {
        addRoad(line);
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }
 
  /*
  * The cityCodeToNumber method converts city code to city number
  * @param code 
  */
  private int cityCodeToNumber(String code) {
    for (City c: cities) {
      if (c.code.equals(code)) {
        return c.number;
      }
    }
    return -1;
  }
  
 
  /*
   *  The cityNumberToCode method convert city number to city code
   *  @param number
   */
  private String cityNumberToCode(int number) {
    for (City c: cities) {
      if (c.number == number) {
        return c.code;
      }
    }
    return "";
  }
  
 
  /*
  * The cityNumberToName method converts city number to city name
  * @param number
  */
  private String cityNumberToName(int number) {
    for (City c: cities) {
      if (c.number == number) {
        return c.name;
      }
    }
    return "";
  }
 
 
  /*
  * The addCity method allows to add city
  * @param cityInfo
  */
  private void addCity(String cityInfo) {
    String[] params = cityInfo.trim().split("\\s+");
    
    int number,population,elevation;
    String code, name;
    
    if (params.length >= 5) {
      number = Integer.parseInt(params[0]);
      code = params[1];
      name = params[2];
      for (int i = 3; i < params.length-2; i++) {
        name += " "+params[i];
      }
      population = Integer.parseInt(params[params.length-2]);
      elevation = Integer.parseInt(params[params.length-1]);
      City c = new City(number, code, name, population, elevation);
      cities.add(c);
    }
  }
  
  /*
   * The addRoad allows to add road 
   * @param roadInfo
   */
  private void addRoad(String roadInfo) {
    String[] params = roadInfo.trim().split("\\s+");
    int from, to,distance;
    if (params.length == 3) {
      from = Integer.parseInt(params[0]);
      to = Integer.parseInt(params[1]);
      distance = Integer.parseInt(params[2]);
      Road r = new Road(from, to, distance);
      roads.add(r);
    }
  }
  
 
  /*
  * The printCityInfo method prints city info
  * @param code
  */
  private void printCityInfo(String code) {
    for (City c: cities) {
      if (c.code.equals(code)) {
        System.out.println(c.toString());
        return;
      }
    }
  }
 
 
 /*
  * The findConnection method find connection between two cities
  * @param citiesInfo
  */
  private void findConnection(String citiesInfo){
    String[] params = citiesInfo.trim().split("\\s+");
    int from,to;
    if (params.length == 2)
    {
      from = cityCodeToNumber(params[0]);
      if (from == -1) {
        System.out.println("City code "+params[0]+" doesn't exist.");
        return;
      }
     
      to = cityCodeToNumber(params[1]);
      if (to == -1) {
        System.out.println("City code "+params[1]+" doesn't exist.");
        return;
      }
      
    
      ArrayList<String> Q = new ArrayList<String>();	// Create vertex set
      
      // Dictionary of distances
      HashMap<Integer,Integer> dist = new HashMap<Integer,Integer>();    // Create dictionary of distances
      
      //Dictionary of previous nodes
      HashMap<Integer,Integer> prev = new HashMap<Integer,Integer>();
 
      for (City c: cities) {
        Q.add(c.code);
        dist.put(c.number,Integer.MAX_VALUE);
      }
  
      dist.put(from,0);		 // Starting point 
      // Continue running until all nodes are visited
      while(Q.size() > 0) {
        
        int u = -1; // The shortest distance
        int minDist = Integer.MAX_VALUE;
        for (String code : Q) {
          int number = cityCodeToNumber(code);
          if (dist.get(number) < minDist) {
            u = number;
            minDist = dist.get(number);
          }
        }
       
        Q.remove(cityNumberToCode(u));
        // For each neighbor v of u, where v is still in Q:
        for (Road r: roads) {
          if (r.from == u && Q.contains(cityNumberToCode(r.to))) {
            int v = r.to;
            // Identify path length
            int alt = dist.get(u) + r.distance;
            // If the new path is shorter than the previous path,
            // update the distance and node
            if (alt < dist.get(v)) {
              dist.put(v,alt);
              prev.put(v,u);
            }
          }
        }
      }
     
      ArrayList<String> route = new ArrayList<String>();
      int current = to;
      while (current != from) {
        route.add(0,cityNumberToCode(current));
        current = prev.get(current);
      }
      route.add(0,cityNumberToCode(from));
      
      //Print minimum distance
      String[] routeArray = route.toArray(new String[0]);
      String routeString = "";
      for (int i = 0; i < routeArray.length-1; i++) {
        routeString += routeArray[i] + ", ";
      }
      routeString += routeArray[routeArray.length-1] + ".";
      System.out.println("The minimum distance between "+cityNumberToName(from)+" and "+
    		  cityNumberToName(to)+" is "+dist.get(to)+" through the route: "+routeString);
      return;
    }
    System.out.println("Invalid input.");
  }
  
  
  /*
   * The insertRoad allows to insert road
   * @param roadInfo
   */
  private void insertRoad(String roadInfo) {
    String[] params = roadInfo.trim().split("\\s+");
    if (params.length == 3) {
      int from = cityCodeToNumber(params[0]);
      if (from == -1) { //Dijkstra rule, cannot be negative value
        System.out.println("City code "+params[0]+" doesn't exist.");
        return;
      }
     
      int to = cityCodeToNumber(params[1]);
      if (to == -1) { //Dijkstra rule, cannot be negative value
        System.out.println("City code "+params[1]+" doesn't exist.");
        return;
      }
      int distance = Integer.parseInt(params[2]);
      for (Road r: roads) {
        if (r.from == from && r.to == to) {
          System.out.println("The road between "+cityNumberToName(from)+" and "+cityNumberToName(to)+" already exists.");
          return;
        }
      }
      roads.add(new Road(from, to, distance));
      System.out.println("You have inserted a road from "+cityNumberToName(from)+" to "
    		  +cityNumberToName(to)+" with a distance of "+distance+".");
      return;
    }
    System.out.println("Invalid input.");
  }
  
  
  /*
   * The removeRoad allows to remove a rode
   * @param roadInfo
   */
  private void removeRoad(String roadInfo) {
    String[] params = roadInfo.trim().split("\\s+");
    if (params.length == 2) {
      int from = cityCodeToNumber(params[0]);
      if (from == -1) {
        System.out.println("City code "+params[0]+" doesn't exist.");
        return;
      }
      int to = cityCodeToNumber(params[1]);
      if (to == -1) {
        System.out.println("City code "+params[1]+" doesn't exist.");
        return;
      }
      for (Road r: roads) {
        if (r.to == to && r.from == from) {
          roads.remove(r);
          return;
        }
      }
      System.out.println("The road between "+cityNumberToName(from)+" and "		
    		  	+cityNumberToName(to)+" doesn't exist.");
      return;
    }
    System.out.println("Invalid input.");
  }
  
 /*
  * Main method for execution
  */
  public static void main(String[] args) {
    // Construct MapProgram from specified City.dat and Road.dat files
    Ek_Sorya_Map_Program mp = new Ek_Sorya_Map_Program("City.dat","Road.dat");
    Scanner keyboardInput = new Scanner(System.in);
    String input;
    
    //Get commands from user
    while (true) {
      System.out.print("Command? ");
      input = keyboardInput.nextLine();
      // Print city information
      if ((input.charAt(0) == 'Q') || (input.charAt(0) == 'q')){
        System.out.print("City code: ");
        input = keyboardInput.nextLine();
        mp.printCityInfo(input);
      // Find connection
      } else if ((input.charAt(0) == 'D') || (input.charAt(0) == 'd')){
        System.out.print("City codes: ");
        input = keyboardInput.nextLine();
        mp.findConnection(input);
      // Insert road
      } else if ((input.charAt(0) == 'I') || (input.charAt(0) == 'i')){
        System.out.print("City codes: ");
        input = keyboardInput.nextLine();
        mp.insertRoad(input);
      // Remove road
      } else if ((input.charAt(0) == 'R') || (input.charAt(0) == 'r')){
        System.out.print("City codes: ");
        input = keyboardInput.nextLine();
        mp.removeRoad(input);
      // Help 
      } else if ((input.charAt(0) == 'H') || (input.charAt(0) == 'h')){
        System.out.println("\tQ\tQuery the city information by entering the city code.");
        System.out.println("\tD\tFind the minimum distance between two cities.");
        System.out.println("\tI\tInsert a road by entering two city codes and distance.");
        System.out.println("\tR\tRemove an existing road by entering two city codes.");
        System.out.println("\tH\tDisplay this message.");
        System.out.println("\tE\tExit.");
      // Exit the program
      } else if ((input.charAt(0) == 'E') || (input.charAt(0) == 'e')){
        break;
      }
    }
  }
}


//Road Class
class Road {
  
  // Variables for Road
  int from;
  int to;
  int distance;
  
  Road(int from, int to, int distance) {	 // Road Constructor
    this.from = from;
    this.to = to;
    this.distance = distance;
  }
  
}
//City class
class City {
  
  //Variables for City
  int number;
  String code;
  String name;
  int population;
  int elevation;
 
  City(int number, String code, String name, int population, int elevation) {
    this.number = number;
    this.code = code;
    this.name = name;
    this.population = population;
    this.elevation = elevation;
  }
 
  public String toString() {
    return number+" "+code+" "+name+" "+population+" "+elevation;
  }
  
}



  
