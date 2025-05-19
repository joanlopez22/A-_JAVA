# Buscador de Rutas GPS

Esta aplicación encuentra rutas óptimas entre ciudades utilizando el algoritmo A*. Lee archivos de cartografía con distancias entre ciudades y calcula el camino más corto entre una ciudad de origen y una de destino.

## Características

- Cargar cartografía desde archivos de mapas personalizados
- Encontrar rutas óptimas entre ciudades
- Encontrar rutas con ciudades intermedias obligatorias
- Crear archivos de cartografía de ejemplo con distancias reales

## Formato de Archivo

Los archivos de cartografía deben seguir este formato:

```
N (número de ciudades)
Ciudad1;Distancia1;Distancia2;...;DistanciaN
Ciudad2;Distancia1;Distancia2;...;DistanciaN
...
CiudadN;Distancia1;Distancia2;...;DistanciaN
```

Donde:
- Las distancias están en kilómetros
- Una distancia de 0 significa la ciudad a sí misma
- Una distancia de -1 significa que no hay conexión directa
- Una distancia positiva significa que existe una ruta directa

## Cómo Ejecutar

1. Compilar los archivos Java:
```
javac -d bin src/*.java
```

2. Ejecutar la aplicación:
```
java -cp bin GPS
```

3. Seguir las opciones del menú para:
   - Cargar un archivo de cartografía
   - Encontrar rutas óptimas
   - Crear archivos de cartografía de ejemplo

## Implementación del Algoritmo A*

El algoritmo A* está implementado en el archivo `AStar.java`. El algoritmo utiliza:

- Una cola de prioridad para el conjunto abierto (nodos por evaluar)
- Un conjunto para el conjunto cerrado (nodos ya evaluados)
- La fórmula f(n) = g(n) + h(n) donde:
  - g(n) es el costo desde el inicio hasta el nodo actual
  - h(n) es la heurística (costo estimado desde el nodo actual hasta la meta)

En esta implementación, la heurística se establece en 0, haciendo que el algoritmo se comporte como el algoritmo de Dijkstra. Esto garantiza que siempre se encuentre el camino más corto.

## Estructura del Proyecto

- `GPS.java`: Clase principal con interfaz de usuario
- `City.java`: Representa una ciudad (nodo) en el algoritmo A*
- `AStar.java`: Implementación del algoritmo A*
- `MapParser.java`: Utilidad para leer y analizar archivos de cartografía

## Cartografía de Ejemplo

La aplicación puede generar un archivo de cartografía de ejemplo para España con distancias reales entre las principales ciudades.

## Autor

Creado para un trabajo universitario. 