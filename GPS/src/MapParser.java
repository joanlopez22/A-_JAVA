import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to read and parse cartography files into a distance matrix
 * and a list of city names.
 */
public class MapParser {
    
    /**
     * Reads a cartography file and parses it into a distance matrix and a list of city names.
     * 
     * @param filename The name of the file to read
     * @return An array where the first element is the list of city names and the second is the distance matrix
     * @throws IOException If there is an error reading the file
     */
    public static Object[] parseMapFile(String filename) throws IOException {
        List<String> cityNames = new ArrayList<>();
        List<List<Double>> distanceRows = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            // Read number of cities
            int numCities = Integer.parseInt(reader.readLine().trim());
            
            // Read each city line
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length < 2) {
                    continue; // Skip invalid lines
                }
                
                // First part is the city name
                String cityName = parts[0].trim();
                cityNames.add(cityName);
                
                // Parse distances
                List<Double> distances = new ArrayList<>();
                for (int i = 1; i < parts.length; i++) {
                    String distanceStr = parts[i].trim();
                    
                    // Handle city names with distances in format CityName(Distance)
                    if (distanceStr.contains("(") && distanceStr.endsWith(")")) {
                        int openParenIndex = distanceStr.lastIndexOf("(");
                        int closeParenIndex = distanceStr.length() - 1;
                        String distanceValue = distanceStr.substring(openParenIndex + 1, closeParenIndex);
                        distances.add(Double.parseDouble(distanceValue));
                    } else {
                        // If the format is direct distances
                        distances.add(Double.parseDouble(distanceStr));
                    }
                }
                
                distanceRows.add(distances);
            }
        }
        
        // Convert List<List<Double>> to double[][]
        double[][] distanceMatrix = new double[cityNames.size()][cityNames.size()];
        for (int i = 0; i < distanceRows.size(); i++) {
            List<Double> row = distanceRows.get(i);
            for (int j = 0; j < row.size(); j++) {
                distanceMatrix[i][j] = row.get(j);
            }
        }
        
        return new Object[] { cityNames, distanceMatrix };
    }
    
    /**
     * Prints the distance matrix for debugging purposes.
     * 
     * @param cityNames List of city names
     * @param distanceMatrix Distance matrix between cities
     */
    public static void printDistanceMatrix(List<String> cityNames, double[][] distanceMatrix) {
        System.out.println("Distance Matrix:");
        
        // Print header row with city names
        System.out.print("\t");
        for (String city : cityNames) {
            System.out.print(city + "\t");
        }
        System.out.println();
        
        // Print each row
        for (int i = 0; i < cityNames.size(); i++) {
            System.out.print(cityNames.get(i) + "\t");
            for (int j = 0; j < cityNames.size(); j++) {
                System.out.print(distanceMatrix[i][j] + "\t");
            }
            System.out.println();
        }
    }
} 