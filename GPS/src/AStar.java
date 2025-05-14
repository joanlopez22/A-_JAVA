import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Implementation of the A* algorithm for finding optimal routes between cities.
 */
public class AStar {
    private List<String> cityNames;
    private double[][] distanceMatrix;
    
    /**
     * Constructor for the AStar class
     * 
     * @param cityNames List of city names
     * @param distanceMatrix Distance matrix between cities
     */
    public AStar(List<String> cityNames, double[][] distanceMatrix) {
        this.cityNames = cityNames;
        this.distanceMatrix = distanceMatrix;
    }
    
    /**
     * Find the index of a city by its name
     * 
     * @param cityName Name of the city to find
     * @return The index of the city, or -1 if not found
     */
    private int findCityIndex(String cityName) {
        for (int i = 0; i < cityNames.size(); i++) {
            if (cityNames.get(i).equalsIgnoreCase(cityName)) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Calculate the direct distance between two cities
     * 
     * @param fromIndex Index of the source city
     * @param toIndex Index of the destination city
     * @return The distance between the cities, or -1 if no direct path exists
     */
    private double getDistance(int fromIndex, int toIndex) {
        return distanceMatrix[fromIndex][toIndex];
    }
    
    /**
     * Find all cities that are directly connected to the given city
     * 
     * @param cityIndex Index of the city
     * @return List of indices of cities connected to the given city
     */
    private List<Integer> getNeighbors(int cityIndex) {
        List<Integer> neighbors = new ArrayList<>();
        
        for (int i = 0; i < cityNames.size(); i++) {
            double distance = distanceMatrix[cityIndex][i];
            if (distance > 0) { // City is connected (not -1 and not 0)
                neighbors.add(i);
            }
        }
        
        return neighbors;
    }
    
    /**
     * Find the optimal route between two cities using the A* algorithm
     * 
     * @param startCity Name of the start city
     * @param goalCity Name of the goal city
     * @return A City object containing the path and cost, or null if no path exists
     */
    public City findRoute(String startCity, String goalCity) {
        return findRoute(startCity, goalCity, null);
    }
    
    /**
     * Find the optimal route between two cities passing through a mandatory city
     * 
     * @param startCity Name of the start city
     * @param goalCity Name of the goal city
     * @param mandatoryCity Name of a city that the route must pass through, or null if no restriction
     * @return A City object containing the path and cost, or null if no path exists
     */
    public City findRoute(String startCity, String goalCity, String mandatoryCity) {
        // Handle case with mandatory city by finding two routes
        if (mandatoryCity != null && !mandatoryCity.isEmpty() && 
            !mandatoryCity.equalsIgnoreCase(startCity) && 
            !mandatoryCity.equalsIgnoreCase(goalCity)) {
            
            // Find route from start to mandatory city
            City firstLeg = findRouteDirect(startCity, mandatoryCity);
            if (firstLeg == null) {
                return null; // No path to mandatory city
            }
            
            // Find route from mandatory city to goal
            City secondLeg = findRouteDirect(mandatoryCity, goalCity);
            if (secondLeg == null) {
                return null; // No path from mandatory city to goal
            }
            
            // Combine the paths
            double totalCost = firstLeg.getG() + secondLeg.getG();
            List<String> combinedPath = new ArrayList<>(firstLeg.getPath());
            // The mandatory city is already included at the end of firstLeg's path,
            // so we skip it from secondLeg's path
            combinedPath.addAll(secondLeg.getPath().subList(1, secondLeg.getPath().size()));
            
            // Create a new City representing the complete route
            City completeRoute = new City(goalCity);
            completeRoute.setG(totalCost);
            completeRoute.setH(0);
            
            // Set the combined path
            City current = completeRoute;
            for (int i = combinedPath.size() - 2; i >= 0; i--) {
                City parent = new City(combinedPath.get(i));
                current.setParent(parent);
                current = parent;
            }
            
            return completeRoute;
        } else {
            // No mandatory city, find direct route
            return findRouteDirect(startCity, goalCity);
        }
    }
    
    /**
     * The core A* algorithm implementation to find a route between two cities
     * 
     * @param startCity Name of the start city
     * @param goalCity Name of the goal city
     * @return A City object containing the path and cost, or null if no path exists
     */
    private City findRouteDirect(String startCity, String goalCity) {
        int startIndex = findCityIndex(startCity);
        int goalIndex = findCityIndex(goalCity);
        
        if (startIndex == -1 || goalIndex == -1) {
            return null; // City not found
        }
        
        // Initialize open and closed sets
        PriorityQueue<City> openSet = new PriorityQueue<>();
        Set<String> closedSet = new HashSet<>();
        
        // Create start node
        City start = new City(startCity);
        start.setG(0);
        start.setH(getHeuristic(startIndex, goalIndex));
        
        // Add start node to open set
        openSet.add(start);
        
        while (!openSet.isEmpty()) {
            // Get the node with the lowest f value
            City current = openSet.poll();
            
            // If we've reached the goal, return the path
            if (current.getName().equalsIgnoreCase(goalCity)) {
                return current;
            }
            
            // Add current node to closed set
            closedSet.add(current.getName().toLowerCase());
            
            // Get current city index
            int currentIndex = findCityIndex(current.getName());
            
            // Explore all neighbors
            for (int neighborIndex : getNeighbors(currentIndex)) {
                String neighborName = cityNames.get(neighborIndex);
                
                // Skip if neighbor is already in closed set
                if (closedSet.contains(neighborName.toLowerCase())) {
                    continue;
                }
                
                // Calculate cost to this neighbor
                double cost = current.getG() + getDistance(currentIndex, neighborIndex);
                
                // Create neighbor node
                double heuristic = getHeuristic(neighborIndex, goalIndex);
                City neighbor = new City(neighborName, cost, heuristic, current);
                
                // Check if this neighbor is already in open set with a better path
                boolean inOpenSet = false;
                for (City openNode : openSet) {
                    if (openNode.getName().equalsIgnoreCase(neighborName)) {
                        inOpenSet = true;
                        
                        // If current path to neighbor is better, update it
                        if (cost < openNode.getG()) {
                            openSet.remove(openNode);
                            openSet.add(neighbor);
                        }
                        break;
                    }
                }
                
                // If neighbor is not in open set, add it
                if (!inOpenSet) {
                    openSet.add(neighbor);
                }
            }
        }
        
        // No path found
        return null;
    }
    
    /**
     * Calculate the heuristic value (estimated cost) from a city to the goal.
     * In this implementation, we'll use zero as a heuristic to make the algorithm behave like Dijkstra's.
     * For a true A* implementation, we could use geographical distances if available.
     * 
     * @param fromIndex Index of the source city
     * @param toIndex Index of the destination city
     * @return The heuristic value
     */
    private double getHeuristic(int fromIndex, int toIndex) {
        // For now, use zero as heuristic (equivalent to Dijkstra's algorithm)
        // In a real-world scenario, this would be replaced with an actual distance estimate
        return 0;
    }
    
    /**
     * Print the route details
     * 
     * @param route The City object containing the route
     */
    public void printRoute(City route) {
        if (route == null) {
            System.out.println("No route found.");
            return;
        }
        
        List<String> path = route.getPath();
        System.out.println("Optimal Route:");
        System.out.println("Path: " + String.join(" -> ", path));
        System.out.println("Total Distance: " + route.getG() + " km");
        
        // Print segment details
        if (path.size() > 1) {
            System.out.println("\nSegment Details:");
            for (int i = 0; i < path.size() - 1; i++) {
                int fromIndex = findCityIndex(path.get(i));
                int toIndex = findCityIndex(path.get(i + 1));
                double distance = getDistance(fromIndex, toIndex);
                System.out.println(path.get(i) + " -> " + path.get(i + 1) + ": " + distance + " km");
            }
        }
    }
} 