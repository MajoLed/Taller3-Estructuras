import ADTs.Persona;
import ADTs.Pareja;
import Modelos.WeightedQuickUnion;
import Ordenamiento.InsertionSort;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        String rutaCsv = args[0];
        int nEtiqueta = Integer.parseInt(args[1]);
        int semilla = Integer.parseInt(args[2]);
        String archivoProgreso = args.length >= 4 ? args[3] : null;
        List<int[]> progreso = new ArrayList<>();

        Persona[] personas = cargarPersonas(rutaCsv);
        int n = personas.length;
        Pareja[] parejas = construirParejas(personas);
        int p = parejas.length;

        InsertionSort sort = new InsertionSort();
        long t0 = System.nanoTime();
        sort.sort(parejas);
        long t1 = System.nanoTime();
        double tOrdenMs = (t1 - t0) / 1_000_000.0;

        WeightedQuickUnion uf = new WeightedQuickUnion(n);
        int k = 0, ured = 0;
        double rEstrella = -1;

        long t2 = System.nanoTime();
        for (Pareja par : parejas) {
            int antes = uf.count();
            uf.union(par.getId1(), par.getId2());
            int despues = uf.count();
            k++;
            if (despues < antes) rEstrella = par.getDistancia();
            else ured++;
            progreso.add(new int[]{k, despues});
            if (despues == 1) break;
        }
        long t3 = System.nanoTime();
        double tUnionesMs = (t3 - t2) / 1_000_000.0;

        double rGrados = Math.sqrt(rEstrella);
        double rKm = rGrados * 111.19;

        String linea = String.join(",",
                String.valueOf(n), String.valueOf(semilla), String.valueOf(p),
                String.valueOf(tOrdenMs), String.valueOf(sort.getComparaciones()),
                String.valueOf(sort.getIntercambios()), String.valueOf(tUnionesMs),
                String.valueOf(k), String.valueOf(n - 1), String.valueOf(ured),
                String.valueOf(uf.getAccesosFind()), String.valueOf(uf.getAccesosUnion()),
                String.valueOf(rGrados), String.valueOf(rKm));

        File f = new File("resultados.csv");
        boolean nuevo = !f.exists();
        try (FileWriter fw = new FileWriter(f, true)) {
            if (nuevo) fw.write("N,semilla,P,tOrdenMs,less,exch,tUnionesMs,K,Uef,Ured,accFind,accUnion,rGrados,rKm\n");
            fw.write(linea + "\n");
        }

        if (archivoProgreso != null) {
            try (FileWriter fw = new FileWriter(archivoProgreso)) {
                fw.write("k,C\n");
                for (int[] punto : progreso) fw.write(punto[0] + "," + punto[1] + "\n");
            }
        }

        System.out.println(linea);
    }

    private static Persona[] cargarPersonas(String ruta) throws Exception {
        List<Persona> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea = br.readLine();
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",");
                lista.add(new Persona(Integer.parseInt(partes[0].trim()),
                        Double.parseDouble(partes[1].trim()), Double.parseDouble(partes[2].trim())));
            }
        }
        return lista.toArray(new Persona[0]);
    }

    private static Pareja[] construirParejas(Persona[] personas) {
        int n = personas.length;
        Pareja[] parejas = new Pareja[n * (n - 1) / 2];
        int idx = 0;
        for (int i = 0; i < n; i++)
            for (int j = i + 1; j < n; j++)
                parejas[idx++] = new Pareja(personas[i], personas[j]);
        return parejas;
    }
}