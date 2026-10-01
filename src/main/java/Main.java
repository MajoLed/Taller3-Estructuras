import ADTs.Persona;
import ADTs.Pareja;
import Modelos.WeightedQuickUnion;
import Ordenamiento.InsertionSort;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {

        String rutaCsv = "src/main/java/personas25.csv";
        int nEtiqueta = Integer.parseInt(args[1]); // para dejar registro, no se recalcula
        int semilla = Integer.parseInt(args[2]);
        String archivoProgreso = args.length >= 4 ? args[3] : null;

        // Cargamos el arreglo de personas con lo generado en csv
        Persona[] personas = cargarPersonas(rutaCsv);
        int n = personas.length;


        // 2 - construir arreglo
        Pareja[] parejas = construirParejas(personas);
        int p = parejas.length;

        // --- Fase de ordenamiento ---

        InsertionSort InSort = new InsertionSort();
        long inicioOrden = System.nanoTime();
        InSort.sort(parejas);
        long finOrden = System.nanoTime();
        double tiempoTotal = (finOrden - inicioOrden) / 1_000_000.0;

        // --- Fase de uniones ---
        WeightedQuickUnion uf = new WeightedQuickUnion(n);
        List<int[]> progreso = new ArrayList<>(); // (k, C(k))
        int k = 0;
        double rEstrella = -1; // distancia (al cuadrado) de la última pareja efectiva
        int ured = 0;

        long inicioUniones = System.nanoTime();
        for (Pareja par : parejas) {
            int antes = uf.count();
            uf.union(par.getId1(), par.getId2());
            int despues = uf.count();
            k++;

            if (despues < antes) {
                rEstrella = par.getDistancia(); // efectiva: guarda su distancia
            } else {
                ured++;
            }

            progreso.add(new int[]{k, despues});

            if (despues == 1) break; // conectividad total alcanzada
        }
        long finUniones = System.nanoTime();
        double tiempoUnionesMs = (finUniones - inicioUniones) / 1_000_000.0;

        double rEstrellaGrados = Math.sqrt(rEstrella);
        double rEstrellaKm = rEstrellaGrados * 111.19;

        System.out.println("***** Informe punto 5 *******");
        System.out.println("N = " + n + ", P = " + p);
        System.out.println("Tiempo ordenamiento (ms): " + tiempoTotal);
        System.out.println("Comparaciones (less): " + InSort.getComparaciones());
        System.out.println("Intercambios (exch): " + InSort.getIntercambios());
        System.out.println("Tiempo fase de uniones (ms): " + tiempoUnionesMs);
        System.out.println("K (parejas examinadas): " + k);
        System.out.println("Uniones efectivas: " + (n - 1) + " | Redundantes: " + ured);
        System.out.println("Accesos find: " + uf.getAccesosFind());
        System.out.println("Accesos union (total, incluye sus find): " + uf.getAccesosUnion());
        System.out.println("r* = " + rEstrellaGrados + " grados (~" + rEstrellaKm + " km)");

        File resultados = new File("resultados.csv");
        boolean nuevo = !resultados.exists();
        try (FileWriter fw = new FileWriter(resultados, true)) {
            if (nuevo) fw.write("N,semilla,P,tOrdenMs,less,exch,tUnionesMs,K,Uef,Ured,accFind,accUnion,rGrados,rKm\n");
            fw.write(linea + "\n");
        }

        FileDescriptor archivoProgreso;
        if (archivoProgreso != null) {
            try (FileWriter fw = new FileWriter(archivoProgreso)) {
                fw.write("k,C(k)\n");
                for (int[] punto : progreso) fw.write(punto[0] + "," + punto[1] + "\n");
            }
        }

        System.out.println("N=" + n + " semilla=" + semilla + " -> " + linea);

    }

    private static Persona[] cargarPersonas(String ruta) throws Exception {
        List<Persona> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea = br.readLine(); // para saltar el encabezado
            while ((linea = br.readLine()) != null) {
                 String[] partes = linea.split(",");
                int id = Integer.parseInt(partes[0].trim());
                double lat = Double.parseDouble(partes[1].trim());
                double lon = Double.parseDouble(partes[2].trim());
                lista.add(new Persona(id, lat, lon));
            }
        }
        return lista.toArray(new Persona[0]);
    }

    private static Pareja[] construirParejas(Persona[] personas) {
        int n = personas.length;
        Pareja[] parejas = new Pareja[n * (n - 1) / 2];
        int idx = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                parejas[idx++] = new Pareja(personas[i], personas[j]);
            }
        }
        return parejas;
    }
}