# Práctica de Programación Concurrente: Hilos

Este repositorio contiene las soluciones y ejercicios desarrollados para la práctica de **Programación Concurrente: Hilos** de la Universidad Autónoma Metropolitana, Unidad Iztapalapa. La práctica aborda los conceptos fundamentales de concurrencia en Java, explorando diferentes mecanismos para crear y administrar hilos, así como un ejercicio integrador para resolver el problema de las **N-Reinas**.

---

## Estructura del Repositorio

El repositorio está organizado de la siguiente manera:

* **`SumaArreglo.java`**: Contiene las implementaciones para sumar los elementos de un arreglo de forma secuencial y concurrente mediante tres enfoques:
  1. **Versión Secuencial**: Recorrido lineal utilizando únicamente el hilo principal de Java.
  2. **Versión con `Thread`**: Creación de hilos mediante herencia directa de la clase `Thread`, sobrescribiendo `run()` y sincronizando con `join()`.
  3. **Versión con `Runnable`**: Separación de conceptos donde el objeto representa una tarea independiente ejecutada por un `Thread`.
  4. **Versión con `Callable`**: Uso de la interfaz genérica `Callable<V>` junto con `ExecutorService` y `Future<V>` para retornar resultados directamente desde las tareas.
* **`NReinas.java`** : Contiene la solución basada en *backtracking* para encontrar todas las configuraciones posibles del problema de las N-Reinas:
  1. **Versión Secuencial**: Explora todo el espacio de búsqueda de forma recursiva acumulando las soluciones válidas.
  2. **Versión Concurrente**: Distribuye las columnas iniciales del renglón 0 entre múltiples hilos para paralelizar el cálculo de las sub-búsquedas.
* **`.gitignore`**: Archivo de configuración para omitir archivos binarios y directorios de compilación (`out/`, `build/`, `*.class`, configuraciones de IDEs).

---

## Compilación y Ejecución

A continuación se detallan las instrucciones para compilar y ejecutar cada uno de los programas incluidos en la práctica.

### 1. Suma de un Arreglo

#### Versión Secuencial
* **Compilación:**
  ```bash
  javac SumaArreglo.java
  ```

* **Ejecución:**
  ```bash
  java SumaArreglo 20
  ```

#### Versiones Concurrentes (Thread, Runnable y Callable)
Para estas versiones se requiere indicar el tamaño del arreglo ($N$) y el número de hilos ($H$) a utilizar:   

* **Compilación:**
  ```bash
  javac SumaArreglo.java
  ```

* **Ejecución:**
  ```bash
  java SumaArreglo 20 4
  ```

### 2. Ejercicio Integrador: N-Reinas
#### Versión secuencial
* **Compilación:**
  ```bash
  javac NReinas.java
  ```

* **Ejecución:**
  ```bash
  java NReinas 8
  ```

#### Versión Concurrente
* **Compilación:**
  ```bash
  javac NReinas.java
  ```

* **Ejecución:**
  ```bash
  java NReinas [N] [Hilos]
  ```

## Distribución del Trabajo en N-Reinas (Versión Concurrente)

Para resolver el problema de las N-Reinas de forma concurrente, la estrategia implementada **no divide el tablero por renglones**, sino que **distribuye las columnas iniciales del renglón 0** entre los hilos disponibles[cite: 17]. 

La distribución y ejecución se realiza mediante los siguientes pasos técnicos:

1. **Partición de Columnas por Bloques:** 
   Utilizando el identificador único (`id`) de cada hilo y el total de hilos (`hilos`), se calcula de forma equitativa el intervalo de columnas del renglón 0 que le corresponde procesar a cada trabajador[cite: 17]. El residuo de la división ($N \pmod H$) se reparte uno a uno entre los primeros hilos para asegurar una carga balanceada:
   ```java
   int tam = N / hilos;
   int resto = N % hilos;
   int inicio = id * tam + Math.min(id, resto);
   int cantidad = tam + (id < resto ? 1 : 0);
   int fin = inicio + cantidad;
   ```
	
2. **Aislamiento de Recursos (Tableros Independientes):** 
  Para evitar condiciones de carrera e interferencias de memoria, cada hilo instancia su propio arreglo bidimensional exclusivo (`int[][] tablero = new int[N][N]`)[cite: 18]. En este tablero privado, el hilo coloca su reina asignada en la columna correspondiente del renglón 0 (`tablero[0][c] = 1`) y ejecuta el algoritmo recursivo de *backtracking* a partir del renglón 1 (`backtrackReinas(tablero, 1)`)[cite: 18].

3. **Sincronización y Acumulación de Resultados:**
  * El hilo principal (`main`) instancia y arranca a los hilos trabajadores utilizando el método `.start()`[cite: 18].
  * Utiliza un ciclo con `.join()` para esperar a que cada hilo termine su sub-búsqueda de forma síncrona[cite: 18].
  * Finalmente, recolecta y suma los resultados parciales almacenados en el arreglo compartido de resultados para imprimir el total definitivo de soluciones válidas[cite: 18], el cual coincide exactamente con la versión secuencial[cite: 19].
