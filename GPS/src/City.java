import java.util.ArrayList;
import java.util.List;

/**
 * Representa una ciudad en el sistema GPS.
 * Esta clase se utiliza como un nodo en el algoritmo A*.
 */
public class City implements Comparable<City> {
    private String name;
    private double g; // Costo desde el nodo inicial hasta este nodo
    private double h; // Costo heurístico desde este nodo hasta la meta
    private double f; // Costo total (g + h)
    private City parent;
    private List<String> path;

    /**
     * Constructor para la clase City
     * @param name El nombre de la ciudad
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
     * Constructor con todos los parámetros
     * @param name El nombre de la ciudad
     * @param g Costo desde el inicio hasta este nodo
     * @param h Costo heurístico desde este nodo hasta la meta
     * @param parent Nodo padre en el camino
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

    // Getters y setters
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
     * Compara ciudades basándose en su valor f (para la cola de prioridad)
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