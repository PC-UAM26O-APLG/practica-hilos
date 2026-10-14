import java.util.Arrays;

public class NReinasC extends Thread {
    private int id;
    private int hilos;
    private int N;
    private static int[] resultados;

    // Constructor para la versión concurrente
    public NReinasC(int id, int hilos, int N, int[] resultados) {
        this.id = id;
        this.hilos = hilos;
        this.N = N;
        NReinasC.resultados = resultados;
    }

    // Método para verificar si una reina se ataca
    public static boolean esComida(int[][] tablero, int r, int c) {
        int auxR;
        int auxC;

        // Misma columna
        auxR = r - 1;
        auxC = c;
        while (auxR >= 0) {
            if (tablero[auxR][auxC] == 1) {
                return true;
            }
            auxR--;
        }

        // Diagonal superior derecha
        auxR = r - 1;
        auxC = c + 1;
        while (auxR >= 0 && auxC < tablero.length) {
            if (tablero[auxR][auxC] == 1) {
                return true;
            }
            auxR--;
            auxC++;
        }

        // Diagonal superior izquierda
        auxR = r - 1;
        auxC = c - 1;
        while (auxR >= 0 && auxC >= 0) {
            if (tablero[auxR][auxC] == 1) {
                return true;
            }
            auxR--;
            auxC--;
        }

        return false;
    }

    // Algoritmo de Backtracking recursivo
    public static long backtrackReinas(int[][] tablero, int r) {
        if (r == tablero.length) {
            return 1;
        }
        long soluciones = 0;

        for (int c = 0; c < tablero.length; c++) {
            if (!esComida(tablero, r, c)) {
                tablero[r][c] = 1;
                soluciones += backtrackReinas(tablero, r + 1);
                tablero[r][c] = 0;
            }
        }
        return soluciones;
    }

    // Método run() ejecutado por cada hilo 
    @Override
    public void run() {
        int tam = N / hilos;
        int resto = N % hilos;
        
        // Cálculo del intervalo de columnas asignadas a este hilo
	//Distribución de las columnas del renglón cero entre los trabajadores
        int inicio = id * tam + Math.min(id, resto);
        int cantidad = tam + (id < resto ? 1 : 0);
        int fin = inicio + cantidad;

        int solucionesLocales = 0;

        // Cada hilo recorre únicamente sus columnas asignadas en el renglón 0
        for (int c = inicio; c < fin; c++) {
            int[][] tablero = new int[N][N]; // Tablero propio y aislado por hilo
            tablero[0][c] = 1;
            solucionesLocales += backtrackReinas(tablero, 1);
        }

        resultados[id] = solucionesLocales;
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Uso: java NReinasC <N> <Hilos>");
            System.exit(0);
        }

        int n = Integer.parseInt(args[0]);
        int hilos = Integer.parseInt(args[1]);

        Thread[] trabajadores = new Thread[hilos];
        int[] resultados = new int[hilos];

        long startTime = System.currentTimeMillis();

        // Crear e iniciar los hilos trabajadores
        for (int i = 0; i < hilos; i++) {
            trabajadores[i] = new NReinasC(i, hilos, n, resultados);
            trabajadores[i].start();
        }

        int total = 0;

        // Esperar a que todos terminen (join) y sumar los resultados parciales
        for (int i = 0; i < hilos; i++) {
            try {
                trabajadores[i].join();
                total += resultados[i];
            } catch (InterruptedException e) {
                System.out.println("Error: en la espera del hilo");
            }
        }

        long endTime = System.currentTimeMillis();

        System.out.println("N = " + n);
        System.out.println("Total de soluciones: " + total);
        System.out.println("Tiempo de ejecución: " + (endTime - startTime) + " ms");
    }
}
