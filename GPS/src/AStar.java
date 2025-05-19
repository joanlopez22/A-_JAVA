import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Implementación del algoritmo A* para encontrar rutas óptimas entre ciudades.
 */
public class AStar {
    private List<String> cityNames;
    private double[][] distanceMatrix;
    private boolean debugMode = true; // Activar modo de depuración por defecto
    
    /**
     * Constructor para la clase AStar
     * 
     * @param cityNames Lista de nombres de ciudades
     * @param distanceMatrix Matriz de distancias entre ciudades
     */
    public AStar(List<String> cityNames, double[][] distanceMatrix) {
        this.cityNames = cityNames;
        this.distanceMatrix = distanceMatrix;
    }
    
    /**
     * Buscar el índice de una ciudad por su nombre
     * 
     * @param cityName Nombre de la ciudad a buscar
     * @return El índice de la ciudad, o -1 si no se encuentra
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
     * Calcular la distancia directa entre dos ciudades
     * 
     * @param fromIndex Índice de la ciudad de origen
     * @param toIndex Índice de la ciudad de destino
     * @return La distancia entre las ciudades, o -1 si no existe un camino directo
     */
    private double getDistance(int fromIndex, int toIndex) {
        return distanceMatrix[fromIndex][toIndex];
    }
    
    /**
     * Encontrar todas las ciudades que están directamente conectadas a la ciudad dada
     * 
     * @param cityIndex Índice de la ciudad
     * @return Lista de índices de ciudades conectadas a la ciudad dada
     */
    private List<Integer> getNeighbors(int cityIndex) {
        List<Integer> neighbors = new ArrayList<>();
        
        for (int i = 0; i < cityNames.size(); i++) {
            double distance = distanceMatrix[cityIndex][i];
            if (distance > 0) { // Ciudad está conectada (no -1 y no 0)
                neighbors.add(i);
            }
        }
        
        return neighbors;
    }
    
    /**
     * Encontrar la ruta óptima entre dos ciudades usando el algoritmo A*
     * 
     * @param startCity Nombre de la ciudad de inicio
     * @param goalCity Nombre de la ciudad de destino
     * @return Un objeto City que contiene la ruta y el costo, o null si no existe una ruta
     */
    public City findRoute(String startCity, String goalCity) {
        return findRoute(startCity, goalCity, null);
    }
    
    /**
     * Encontrar la ruta óptima entre dos ciudades pasando por una ciudad obligatoria
     * 
     * @param startCity Nombre de la ciudad de inicio
     * @param goalCity Nombre de la ciudad de destino
     * @param mandatoryCity Nombre de una ciudad por la que la ruta debe pasar, o null si no hay restricción
     * @return Un objeto City que contiene la ruta y el costo, o null si no existe una ruta
     */
    public City findRoute(String startCity, String goalCity, String mandatoryCity) {
        // Manejar caso con ciudad obligatoria encontrando dos rutas
        if (mandatoryCity != null && !mandatoryCity.isEmpty() && 
            !mandatoryCity.equalsIgnoreCase(startCity) && 
            !mandatoryCity.equalsIgnoreCase(goalCity)) {
            
            if (debugMode) {
                System.out.println("\n=== EXPLICACIÓN DEL ALGORITMO A* ===");
                System.out.println("Buscando ruta con ciudad intermedia obligatoria: " + mandatoryCity);
                System.out.println("El algoritmo A* dividirá la búsqueda en dos partes:");
                System.out.println("1. De " + startCity + " a " + mandatoryCity);
                System.out.println("2. De " + mandatoryCity + " a " + goalCity);
            }
            
            // Encontrar ruta desde inicio hasta ciudad obligatoria
            City firstLeg = findRouteDirect(startCity, mandatoryCity);
            if (firstLeg == null) {
                if (debugMode) System.out.println("No se encontró una ruta desde " + startCity + " hasta " + mandatoryCity);
                return null; // No hay camino hacia la ciudad obligatoria
            }
            
            // Encontrar ruta desde ciudad obligatoria hasta destino
            City secondLeg = findRouteDirect(mandatoryCity, goalCity);
            if (secondLeg == null) {
                if (debugMode) System.out.println("No se encontró una ruta desde " + mandatoryCity + " hasta " + goalCity);
                return null; // No hay camino desde la ciudad obligatoria hasta el destino
            }
            
            // Combinar las rutas
            double totalCost = firstLeg.getG() + secondLeg.getG();
            List<String> combinedPath = new ArrayList<>(firstLeg.getPath());
            // La ciudad obligatoria ya está incluida al final de la ruta de firstLeg,
            // así que la omitimos de la ruta de secondLeg
            combinedPath.addAll(secondLeg.getPath().subList(1, secondLeg.getPath().size()));
            
            // Crear un nuevo objeto City que represente la ruta completa
            City completeRoute = new City(goalCity);
            completeRoute.setG(totalCost);
            completeRoute.setH(0);
            
            // Establecer la ruta combinada
            City current = completeRoute;
            for (int i = combinedPath.size() - 2; i >= 0; i--) {
                City parent = new City(combinedPath.get(i));
                current.setParent(parent);
                current = parent;
            }
            
            if (debugMode) {
                System.out.println("Ruta combinada encontrada con costo total: " + totalCost + " km");
            }
            
            return completeRoute;
        } else {
            // No hay ciudad obligatoria, encontrar ruta directa
            if (debugMode) {
                System.out.println("\n=== EXPLICACIÓN DEL ALGORITMO A* ===");
                System.out.println("Buscando ruta directa desde " + startCity + " hasta " + goalCity);
            }
            return findRouteDirect(startCity, goalCity);
        }
    }
    
    /**
     * La implementación central del algoritmo A* para encontrar una ruta entre dos ciudades
     * 
     * @param startCity Nombre de la ciudad de inicio
     * @param goalCity Nombre de la ciudad de destino
     * @return Un objeto City que contiene la ruta y el costo, o null si no existe una ruta
     */
    private City findRouteDirect(String startCity, String goalCity) {
        int startIndex = findCityIndex(startCity);
        int goalIndex = findCityIndex(goalCity);
        
        if (startIndex == -1 || goalIndex == -1) {
            if (debugMode) System.out.println("Una o ambas ciudades no se encuentran en la cartografía");
            return null; // Ciudad no encontrada
        }
        
        // Inicializar conjuntos abierto y cerrado
        PriorityQueue<City> openSet = new PriorityQueue<>();
        Set<String> closedSet = new HashSet<>();
        
        // Crear nodo de inicio
        City start = new City(startCity);
        start.setG(0);
        start.setH(getHeuristic(startIndex, goalIndex));
        
        if (debugMode) {
            System.out.println("\nIniciando búsqueda A* desde " + startCity + " a " + goalCity);
            System.out.println("Algoritmo A* utiliza la fórmula f(n) = g(n) + h(n) donde:");
            System.out.println("- g(n): costo real acumulado desde el origen hasta el nodo actual");
            System.out.println("- h(n): heurística (estimación del costo desde el nodo actual hasta el destino)");
            System.out.println("- f(n): costo total estimado del camino que pasa por este nodo");
            System.out.println("\nNodo inicial: " + start.getName());
            System.out.println("g(" + start.getName() + ") = " + start.getG() + " (costo desde inicio)");
            System.out.println("h(" + start.getName() + ") = " + start.getH() + " (heurística hasta destino)");
            System.out.println("f(" + start.getName() + ") = " + start.getF() + " (costo total estimado)");
        }
        
        // Añadir nodo inicial al conjunto abierto
        openSet.add(start);
        
        if (debugMode) System.out.println("\n=== INICIO DE LA BÚSQUEDA ===");
        
        int iteration = 0;
        while (!openSet.isEmpty()) {
            iteration++;
            // Obtener el nodo con el valor f más bajo
            City current = openSet.poll();
            
            if (debugMode) {
                System.out.println("\nIteración " + iteration + ":");
                System.out.println("Nodo actual: " + current.getName());
                System.out.println("g(" + current.getName() + ") = " + current.getG() + ", h(" + current.getName() + ") = " + current.getH() + ", f(" + current.getName() + ") = " + current.getF());
            }
            
            // Si hemos llegado al objetivo, devolver la ruta
            if (current.getName().equalsIgnoreCase(goalCity)) {
                if (debugMode) {
                    System.out.println("\n¡DESTINO ALCANZADO!");
                    System.out.println("Se ha encontrado la ruta óptima desde " + startCity + " hasta " + goalCity);
                    System.out.println("El costo total de la ruta es: " + current.getG() + " km");
                    System.out.println("\nEl algoritmo A* garantiza que esta es la ruta óptima porque:");
                    System.out.println("1. Explora primero los nodos con menor costo estimado total (f)");
                    System.out.println("2. Lleva un registro de los nodos ya explorados para evitar ciclos");
                    System.out.println("3. Utiliza una heurística admisible que nunca sobreestima el costo real");
                }
                return current;
            }
            
            // Añadir nodo actual al conjunto cerrado
            closedSet.add(current.getName().toLowerCase());
            
            // Obtener índice de la ciudad actual
            int currentIndex = findCityIndex(current.getName());
            
            // Explorar todos los vecinos
            List<Integer> neighbors = getNeighbors(currentIndex);
            if (debugMode) {
                System.out.println("Vecinos de " + current.getName() + ": ");
                for (int neighborIndex : neighbors) {
                    System.out.println("- " + cityNames.get(neighborIndex) + " (distancia: " + getDistance(currentIndex, neighborIndex) + " km)");
                }
            }
            
            for (int neighborIndex : neighbors) {
                String neighborName = cityNames.get(neighborIndex);
                
                // Omitir si el vecino ya está en el conjunto cerrado
                if (closedSet.contains(neighborName.toLowerCase())) {
                    if (debugMode) System.out.println("  " + neighborName + " ya fue explorado, omitiendo");
                    continue;
                }
                
                // Calcular costo hasta este vecino
                double cost = current.getG() + getDistance(currentIndex, neighborIndex);
                
                // Crear nodo vecino
                double heuristic = getHeuristic(neighborIndex, goalIndex);
                City neighbor = new City(neighborName, cost, heuristic, current);
                
                if (debugMode) {
                    System.out.println("  Evaluando vecino: " + neighborName);
                    System.out.println("    g(" + neighborName + ") = " + cost + " (costo desde inicio)");
                    System.out.println("    h(" + neighborName + ") = " + heuristic + " (heurística hasta destino)");
                    System.out.println("    f(" + neighborName + ") = " + neighbor.getF() + " (costo total estimado)");
                }
                
                // Comprobar si este vecino ya está en el conjunto abierto con un mejor camino
                boolean inOpenSet = false;
                for (City openNode : openSet) {
                    if (openNode.getName().equalsIgnoreCase(neighborName)) {
                        inOpenSet = true;
                        
                        // Si el camino actual al vecino es mejor, actualizarlo
                        if (cost < openNode.getG()) {
                            if (debugMode) System.out.println("    ¡Encontrado camino mejor a " + neighborName + "! Actualizando.");
                            openSet.remove(openNode);
                            openSet.add(neighbor);
                        } else {
                            if (debugMode) System.out.println("    Ya existe un camino mejor a " + neighborName + ", manteniendo el existente.");
                        }
                        break;
                    }
                }
                
                // Si el vecino no está en el conjunto abierto, añadirlo
                if (!inOpenSet) {
                    if (debugMode) System.out.println("    Añadiendo " + neighborName + " al conjunto abierto.");
                    openSet.add(neighbor);
                }
            }
            
            if (debugMode) {
                System.out.println("\nEstado del conjunto abierto después de la iteración " + iteration + ":");
                if (openSet.isEmpty()) {
                    System.out.println("  Conjunto abierto vacío. No hay más nodos para explorar.");
                } else {
                    for (City node : openSet) {
                        System.out.println("  " + node.getName() + ": f = " + node.getF() + " (g = " + node.getG() + ", h = " + node.getH() + ")");
                    }
                    System.out.println("  Próximo nodo a explorar: " + openSet.peek().getName());
                }
            }
        }
        
        // No se encontró ninguna ruta
        if (debugMode) System.out.println("\nNo se encontró ninguna ruta desde " + startCity + " hasta " + goalCity);
        return null;
    }
    
    /**
     * Calcular el valor heurístico (costo estimado) desde una ciudad hasta el objetivo.
     * En esta implementación, usamos cero como heurística para hacer que el algoritmo
     * se comporte como Dijkstra. Para una implementación real de A*, podríamos usar
     * distancias geográficas si están disponibles.
     * 
     * @param fromIndex Índice de la ciudad de origen
     * @param toIndex Índice de la ciudad de destino
     * @return El valor heurístico
     */
    private double getHeuristic(int fromIndex, int toIndex) {
        // Por ahora, usamos cero como heurística (equivalente al algoritmo de Dijkstra)
        // En un escenario del mundo real, esto sería reemplazado con una estimación de distancia real
        return 0;
    }
    
    /**
     * Imprimir los detalles de la ruta
     * 
     * @param route El objeto City que contiene la ruta
     */
    public void printRoute(City route) {
        if (route == null) {
            System.out.println("No se encontró ninguna ruta.");
            return;
        }
        
        List<String> path = route.getPath();
        System.out.println("Ruta Óptima:");
        System.out.println("Camino: " + String.join(" -> ", path));
        System.out.println("Distancia Total: " + route.getG() + " km");
        
        // Imprimir detalles de segmentos
        if (path.size() > 1) {
            System.out.println("\nDetalles de Segmentos:");
            for (int i = 0; i < path.size() - 1; i++) {
                int fromIndex = findCityIndex(path.get(i));
                int toIndex = findCityIndex(path.get(i + 1));
                double distance = getDistance(fromIndex, toIndex);
                System.out.println(path.get(i) + " -> " + path.get(i + 1) + ": " + distance + " km");
            }
        }
    }
} 