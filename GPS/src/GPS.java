import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

/**
 * Main class for the GPS route finding application.
 * This application allows users to find optimal routes between cities using the A* algorithm.
 */
public class GPS {
    private static List<String> cityNames;
    private static double[][] distanceMatrix;
    private static AStar aStar;
    private static Scanner scanner;
    
    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        
        System.out.println("Welcome to the GPS Route Finder");
        System.out.println("==============================");
        
        boolean running = true;
        while (running) {
            System.out.println("\nMenu:");
            System.out.println("1. Load cartography from file");
            System.out.println("2. Find optimal route");
            System.out.println("3. Find route with intermediate city");
            System.out.println("4. Create sample cartography file");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1:
                    loadCartography();
                    break;
                case 2:
                    findRoute(false);
                    break;
                case 3:
                    findRoute(true);
                    break;
                case 4:
                    createSampleCartography();
                    break;
                case 5:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        
        System.out.println("Thank you for using the GPS Route Finder. Goodbye!");
        scanner.close();
    }
    
    /**
     * Load a cartography file
     */
    private static void loadCartography() {
        System.out.print("\nEnter cartography file name (e.g., Espanya.txt): ");
        String filename = scanner.nextLine();
        
        try {
            Object[] parseResult = MapParser.parseMapFile(filename);
            cityNames = (List<String>) parseResult[0];
            distanceMatrix = (double[][]) parseResult[1];
            aStar = new AStar(cityNames, distanceMatrix);
            
            System.out.println("Cartography loaded successfully!");
            System.out.println("Cities found: " + cityNames.size());
            System.out.println("Cities: " + String.join(", ", cityNames));
            
            // Print distance matrix for debugging
            MapParser.printDistanceMatrix(cityNames, distanceMatrix);
            
        } catch (IOException e) {
            System.out.println("Error loading cartography: " + e.getMessage());
        }
    }
    
    /**
     * Find an optimal route
     * 
     * @param withIntermediate Whether to include an intermediate city
     */
    private static void findRoute(boolean withIntermediate) {
        if (aStar == null) {
            System.out.println("Please load a cartography file first.");
            return;
        }
        
        System.out.print("\nEnter origin city: ");
        String originCity = scanner.nextLine();
        
        System.out.print("Enter destination city: ");
        String destCity = scanner.nextLine();
        
        String intermediateCity = null;
        if (withIntermediate) {
            System.out.print("Enter intermediate city (mandatory stop): ");
            intermediateCity = scanner.nextLine();
        }
        
        // Validate cities
        boolean validInput = true;
        if (!cityNames.contains(originCity)) {
            System.out.println("Origin city '" + originCity + "' not found in cartography.");
            validInput = false;
        }
        
        if (!cityNames.contains(destCity)) {
            System.out.println("Destination city '" + destCity + "' not found in cartography.");
            validInput = false;
        }
        
        if (withIntermediate && !cityNames.contains(intermediateCity)) {
            System.out.println("Intermediate city '" + intermediateCity + "' not found in cartography.");
            validInput = false;
        }
        
        if (!validInput) {
            return;
        }
        
        // Find and display route
        City route = aStar.findRoute(originCity, destCity, intermediateCity);
        aStar.printRoute(route);
    }
    
    /**
     * Create a sample cartography file for Spain with real distances between cities
     */
    private static void createSampleCartography() {
        System.out.print("\nEnter file name to create (e.g., Espanya.txt): ");
        String filename = scanner.nextLine();
        
        try (PrintWriter writer = new PrintWriter(new File(filename))) {
            // Spanish cities with real distances (in km)
            String[] cities = {
                "Barcelona",
                "Madrid",
                "Valencia",
                "Sevilla",
                "Zaragoza",
                "Malaga",
                "Murcia",
                "Bilbao",
                "Alicante",
                "Cordoba"
            };
            
            // Number of cities
            writer.println(cities.length);
            
            // Distance matrix (approximated real distances)
            // Value of -1 means no direct connection
            // Distances in km (approximate)
            double[][] distances = {
                // Barcelona
                {0, 621, 350, 995, 312, 868, 589, 615, 515, 859},
                // Madrid
                {621, 0, 356, 530, 325, 529, 401, 395, 422, 394},
                // Valencia
                {350, 356, 0, 654, 311, 619, 241, 610, 166, 520},
                // Sevilla
                {995, 530, 654, 0, 849, 206, 493, 923, 619, 140},
                // Zaragoza
                {312, 325, 311, 849, 0, 780, 521, 324, 477, 713},
                // Malaga
                {868, 529, 619, 206, 780, 0, 412, 922, 520, 158},
                // Murcia
                {589, 401, 241, 493, 521, 412, 0, 794, 75, 352},
                // Bilbao
                {615, 395, 610, 923, 324, 922, 794, 0, 762, 764},
                // Alicante
                {515, 422, 166, 619, 477, 520, 75, 762, 0, 432},
                // Cordoba
                {859, 394, 520, 140, 713, 158, 352, 764, 432, 0}
            };
            
            // Write each city and its distances
            for (int i = 0; i < cities.length; i++) {
                writer.print(cities[i]);
                for (int j = 0; j < cities.length; j++) {
                    writer.print(";" + distances[i][j]);
                }
                writer.println();
            }
            
            System.out.println("Sample cartography file '" + filename + "' created successfully!");
            
        } catch (IOException e) {
            System.out.println("Error creating sample cartography: " + e.getMessage());
        }
    }
    
    /**
     * Get integer input from user with validation
     * 
     * @return The validated integer input
     */
    private static int getIntInput() {
        int input = 0;
        boolean validInput = false;
        
        while (!validInput) {
            try {
                input = Integer.parseInt(scanner.nextLine());
                validInput = true;
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
        
        return input;
    }
} 