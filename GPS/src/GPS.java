import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

/**
 * Clase principal para la aplicación de búsqueda de rutas GPS.
 * Esta aplicación permite a los usuarios encontrar rutas óptimas entre ciudades usando el algoritmo A*.
 */
public class GPS {
    private static List<String> cityNames;
    private static double[][] distanceMatrix;
    private static AStar aStar;
    private static Scanner scanner;
    
    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        
        System.out.println("Bienvenido al Buscador de Rutas GPS");
        System.out.println("==================================");
        
        boolean running = true;
        while (running) {
            System.out.println("\nMenú:");
            System.out.println("1. Cargar cartografía desde archivo");
            System.out.println("2. Encontrar ruta óptima");
            System.out.println("3. Encontrar ruta con ciudad intermedia");
            System.out.println("4. Crear archivo de cartografía de ejemplo");
            System.out.println("5. Salir");
            System.out.print("Ingrese su opción: ");
            
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
                    System.out.println("Opción inválida. Por favor intente de nuevo.");
            }
        }
        
        System.out.println("Gracias por usar el Buscador de Rutas GPS. ¡Adiós!");
        scanner.close();
    }
    
    /**
     * Cargar un archivo de cartografía
     */
    private static void loadCartography() {
        System.out.print("\nIngrese el nombre del archivo de cartografía (ej., Espanya.txt): ");
        String filename = scanner.nextLine();
        
        try {
            Object[] parseResult = MapParser.parseMapFile(filename);
            cityNames = (List<String>) parseResult[0];
            distanceMatrix = (double[][]) parseResult[1];
            aStar = new AStar(cityNames, distanceMatrix);
            
            System.out.println("¡Cartografía cargada con éxito!");
            System.out.println("Ciudades encontradas: " + cityNames.size());
            System.out.println("Ciudades: " + String.join(", ", cityNames));
            
            // Imprimir matriz de distancias para depuración
            MapParser.printDistanceMatrix(cityNames, distanceMatrix);
            
        } catch (IOException e) {
            System.out.println("Error al cargar la cartografía: " + e.getMessage());
        }
    }
    
    /**
     * Encontrar una ruta óptima
     * 
     * @param withIntermediate Si se debe incluir una ciudad intermedia
     */
    private static void findRoute(boolean withIntermediate) {
        if (aStar == null) {
            System.out.println("Por favor, cargue un archivo de cartografía primero.");
            return;
        }
        
        System.out.print("\nIngrese la ciudad de origen: ");
        String originCity = scanner.nextLine();
        
        System.out.print("Ingrese la ciudad de destino: ");
        String destCity = scanner.nextLine();
        
        String intermediateCity = null;
        if (withIntermediate) {
            System.out.print("Ingrese la ciudad intermedia (parada obligatoria): ");
            intermediateCity = scanner.nextLine();
        }
        
        // Validar ciudades
        boolean validInput = true;
        if (!cityNames.contains(originCity)) {
            System.out.println("La ciudad de origen '" + originCity + "' no se encuentra en la cartografía.");
            validInput = false;
        }
        
        if (!cityNames.contains(destCity)) {
            System.out.println("La ciudad de destino '" + destCity + "' no se encuentra en la cartografía.");
            validInput = false;
        }
        
        if (withIntermediate && !cityNames.contains(intermediateCity)) {
            System.out.println("La ciudad intermedia '" + intermediateCity + "' no se encuentra en la cartografía.");
            validInput = false;
        }
        
        if (!validInput) {
            return;
        }
        
        // Encontrar y mostrar ruta
        City route = aStar.findRoute(originCity, destCity, intermediateCity);
        aStar.printRoute(route);
    }
    
    /**
     * Crear un archivo de cartografía de ejemplo para España con distancias reales entre ciudades
     */
    private static void createSampleCartography() {
        System.out.print("\nIngrese el nombre del archivo a crear (ej., Espanya.txt): ");
        String filename = scanner.nextLine();
        
        try (PrintWriter writer = new PrintWriter(new File(filename))) {
            // Ciudades españolas con distancias reales (en km)
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
            
            // Número de ciudades
            writer.println(cities.length);
            
            // Matriz de distancias (distancias reales aproximadas)
            // El valor -1 significa que no hay conexión directa
            // Distancias en km (aproximadas)
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
            
            // Escribir cada ciudad y sus distancias
            for (int i = 0; i < cities.length; i++) {
                writer.print(cities[i]);
                for (int j = 0; j < cities.length; j++) {
                    writer.print(";" + distances[i][j]);
                }
                writer.println();
            }
            
            System.out.println("¡Archivo de cartografía de ejemplo '" + filename + "' creado con éxito!");
            
        } catch (IOException e) {
            System.out.println("Error al crear la cartografía de ejemplo: " + e.getMessage());
        }
    }
    
    /**
     * Obtener entrada de entero del usuario con validación
     * 
     * @return La entrada de entero validada
     */
    private static int getIntInput() {
        int input = 0;
        boolean validInput = false;
        
        while (!validInput) {
            try {
                input = Integer.parseInt(scanner.nextLine());
                validInput = true;
            } catch (NumberFormatException e) {
                System.out.print("Entrada inválida. Por favor ingrese un número: ");
            }
        }
        
        return input;
    }
} 