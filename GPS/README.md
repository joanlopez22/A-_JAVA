# GPS Route Finder

This application finds optimal routes between cities using the A* algorithm. It reads cartography files containing distances between cities and calculates the shortest path between a source and destination city.

## Features

- Load cartography from custom map files
- Find optimal routes between cities
- Find routes with mandatory intermediate cities
- Create sample cartography files with real distances

## File Format

The cartography files should follow this format:

```
N (number of cities)
City1;Distance1;Distance2;...;DistanceN
City2;Distance1;Distance2;...;DistanceN
...
CityN;Distance1;Distance2;...;DistanceN
```

Where:
- Distances are in kilometers
- A distance of 0 means the city to itself
- A distance of -1 means no direct connection
- A positive distance means a direct route exists

## How to Run

1. Compile the Java files:
```
javac -d bin src/*.java
```

2. Run the application:
```
java -cp bin GPS
```

3. Follow the menu options to:
   - Load a cartography file
   - Find optimal routes
   - Create sample cartography files

## A* Algorithm Implementation

The A* algorithm is implemented in the `AStar.java` file. The algorithm uses:

- A priority queue for the open set (to be evaluated)
- A set for the closed set (already evaluated)
- The f(n) = g(n) + h(n) formula where:
  - g(n) is the cost from start to the current node
  - h(n) is the heuristic (estimated cost from current to goal)

In this implementation, the heuristic is set to 0, making the algorithm behave like Dijkstra's algorithm. This ensures that the shortest path is always found.

## Project Structure

- `GPS.java`: Main class with user interface
- `City.java`: Represents a city (node) in the A* algorithm
- `AStar.java`: Implementation of the A* algorithm
- `MapParser.java`: Utility to read and parse cartography files

## Sample Cartography

The application can generate a sample cartography file for Spain with real distances between major cities.

## Author

Created for a university assignment. 