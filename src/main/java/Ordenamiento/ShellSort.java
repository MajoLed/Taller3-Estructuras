package Ordenamiento;

public class ShellSort extends BaseSort {

    @Override
    public <T extends Comparable<T>> void sort(T[] a) {
        int n = a.length;
        int h = 1;
        while (h < n / 3) h = 3 * h + 1;   // Knuth: 1, 4, 13, 40, 121, ...

        while (h >= 1) {
            for (int i = h; i < n; i++) {
                for (int j = i; j >= h && less(a[j], a[j - h]); j -= h) {
                    exch(a, j, j - h);
                }
            }
            h /= 3;
        }
    }
}
