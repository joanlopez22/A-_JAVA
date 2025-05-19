import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase para leer y analizar archivos de cartografía en una matriz de distancias
 * y una lista de nombres de ciudades.
 */
public class MapParser {
    
    /**
     * Lee un archivo de cartografía y lo analiza en una matriz de distancias y una lista de nombres de ciudades.
     * 
     * @param filename El nombre del archivo a leer
     * @return Un array donde el primer elemento es la lista de nombres de ciudades y el segundo es la matriz de distancias
     * @throws IOException Si hay un error al leer el archivo
     */
    public static Object[] parseMapFile(String filename) throws IOException {
        List<String> cityNames = new ArrayList<>();
        List<List<Double>> distanceRows = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            // Leer número de ciudades
            int numCities = Integer.parseInt(reader.readLine().trim());
            
            // Leer cada línea de ciudad
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length < 2) {
                    continue; // Omitir líneas inválidas
                }
                
                // La primera parte es el nombre de la ciudad
                String cityName = parts[0].trim();
                cityNames.add(cityName);
                
                // Analizar distancias
                List<Double> distances = new ArrayList<>();
                for (int i = 1; i < parts.length; i++) {
                    String distanceStr = parts[i].trim();
                    
                    // Manejar nombres de ciudades con distancias en formato CiudadNombre(Distancia)
                    if (distanceStr.contains("(") && distanceStr.endsWith(")")) {
                        int openParenIndex = distanceStr.lastIndexOf("(");
                        int closeParenIndex = distanceStr.length() - 1;
                        String distanceValue = distanceStr.substring(openParenIndex + 1, closeParenIndex);
                        distances.add(Double.parseDouble(distanceValue));
                    } else {
                        // Si el formato es de distancias directas
                        distances.add(Double.parseDouble(distanceStr));
                    }
                }
                
                distanceRows.add(distances);
            }
        }
        
        // Convertir List<List<Double>> a double[][]
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
     * Imprime la matriz de distancias para propósitos de depuración.
     * 
     * @param cityNames Lista de nombres de ciudades
     * @param distanceMatrix Matriz de distancias entre ciudades
     */
    public static void printDistanceMatrix(List<String> cityNames, double[][] distanceMatrix) {
        System.out.println("Matriz de Distancias:");
        
        // Imprimir fila de encabezado con nombres de ciudades
        System.out.print("\t");
        for (String city : cityNames) {
            System.out.print(city + "\t");
        }
        System.out.println();
        
        // Imprimir cada fila
        for (int i = 0; i < cityNames.size(); i++) {
            System.out.print(cityNames.get(i) + "\t");
            for (int j = 0; j < cityNames.size(); j++) {
                System.out.print(distanceMatrix[i][j] + "\t");
            }
            System.out.println();
        }
    }
} 