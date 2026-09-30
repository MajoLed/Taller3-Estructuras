package Ordenamiento;

public class SelectionSort extends BaseSort {

    @Override
    public <T extends Comparable<T>> void sort(T[] a) {
        int n = a.length;
        for (int i = 0; i < n; i++) {
            int min = i;
            for (int j = i + 1; j < n; j++) {
                if (less(a[j], a[min])) min = j;
            }
            exch(a, i, min);   // siempre intercambia: exactamente N intercambios
        }
    }
}
