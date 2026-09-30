package Ordenamiento;

import java.util.Arrays;
import java.util.Random;


public class OrdenamientoTest {

    private static BaseSort[] algoritmos() {
        return new BaseSort[] { new SelectionSort(), new InsertionSort(), new ShellSort() };
    }

    private static <T extends Comparable<T>> boolean ordenado(T[] a) {
        for (int i = 1; i < a.length; i++)
            if (a[i].compareTo(a[i - 1]) < 0) return false;
        return true;
    }

    // Verifica que el resultado sea una permutación ordenada del original
    private static void verificar(BaseSort s, Integer[] original) {
        Integer[] a = original.clone();
        s.sort(a);
        Integer[] esperado = original.clone();
        Arrays.sort(esperado);   // solo se usa aquí, dentro de la prueba
        String nombre = s.getClass().getSimpleName();
        assert ordenado(a) : nombre + " no ordena";
        assert Arrays.equals(a, esperado) : nombre + " no es una permutación del original";
    }

    public static void main(String[] args) {
        // Casos de borde y generales para los tres algoritmos
        for (BaseSort s : algoritmos()) {
            verificar(s, new Integer[] {});                       // vacío
            verificar(s, new Integer[] {7});                      // un elemento
            verificar(s, new Integer[] {1, 2, 3, 4, 5, 6});       // ya ordenado
            verificar(s, new Integer[] {6, 5, 4, 3, 2, 1});       // orden inverso
            verificar(s, new Integer[] {3, 1, 3, 2, 1, 2, 3});    // repetidos

            Integer[] grande = new Integer[200];
            Random r = new Random(42);
            for (int i = 0; i < grande.length; i++) grande[i] = r.nextInt(50);
            verificar(s, grande);                                 // aleatorio con repetidos
        }

        // Contadores calculados a mano
        SelectionSort sel = new SelectionSort();
        sel.sort(new Integer[] {3, 1, 5, 2, 4});
        assert sel.getComparaciones() == 10 : "selection: comparaciones";
        assert sel.getIntercambios() == 5 : "selection: intercambios";

        InsertionSort ins = new InsertionSort();
        ins.sort(new Integer[] {5, 4, 3, 2, 1});
        assert ins.getComparaciones() == 10 : "insertion inverso: comparaciones";
        assert ins.getIntercambios() == 10 : "insertion inverso: intercambios";

        ins.resetCounters();
        ins.sort(new Integer[] {1, 2, 3, 4, 5});
        assert ins.getComparaciones() == 4 : "insertion ordenado: comparaciones";
        assert ins.getIntercambios() == 0 : "insertion ordenado: intercambios";

        ins.resetCounters();
        assert ins.getComparaciones() == 0 && ins.getIntercambios() == 0 : "resetCounters";

        ShellSort sh = new ShellSort();
        sh.sort(new Integer[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
        assert sh.getComparaciones() == 15 && sh.getIntercambios() == 0 : "shell ordenado";

        sh.resetCounters();
        sh.sort(new Integer[] {10, 9, 8, 7, 6, 5, 4, 3, 2, 1});
        assert sh.getComparaciones() == 21 && sh.getIntercambios() == 13 : "shell inverso";

        System.out.println("Todas las pruebas de ordenamiento pasaron.");
    }
}