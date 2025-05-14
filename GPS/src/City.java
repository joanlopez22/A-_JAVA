import java.util.ArrayList;
import java.util.List;

/**
 * Represents a city in the GPS system.
 * This class is used as a node in the A* algorithm.
 */
public class City implements Comparable<City> {
    private String name;
    private double g; // Cost from start node to this node
    private double h; // Heuristic cost from this node to goal
    private double f; // Total cost (g + h)
    private City parent;
    private List<String> path;

    /**
     * Constructor for the City class
     * @param name The name of the city
     */
    public City(String name) {
        this.name = name;
        this.g = 0;
        this.h = 0;
        this.f = 0;
        this.parent = null;
        this.path = new ArrayList<>();
        this.path.add(name);
    }

    /**
     * Constructor with all parameters
     * @param name The name of the city
     * @param g Cost from start to this node
     * @param h Heuristic cost from this node to goal
     * @param parent Parent node in the path
     */
    public City(String name, double g, double h, City parent) {
        this.name = name;
        this.g = g;
        this.h = h;
        this.f = g + h;
        this.parent = parent;
        this.path = new ArrayList<>();
        if (parent != null) {
            this.path.addAll(parent.getPath());
        }
        this.path.add(name);
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public double getG() {
        return g;
    }

    public void setG(double g) {
        this.g = g;
        this.f = this.g + this.h;
    }

    public double getH() {
        return h;
    }

    public void setH(double h) {
        this.h = h;
        this.f = this.g + this.h;
    }

    public double getF() {
        return f;
    }

    public City getParent() {
        return parent;
    }

    public void setParent(City parent) {
        this.parent = parent;
        this.path = new ArrayList<>();
        if (parent != null) {
            this.path.addAll(parent.getPath());
        }
        this.path.add(this.name);
    }

    public List<String> getPath() {
        return path;
    }

    /**
     * Compare cities based on their f value (for priority queue)
     */
    @Override
    public int compareTo(City other) {
        return Double.compare(this.f, other.f);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        City city = (City) obj;
        return name.equals(city.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
} 