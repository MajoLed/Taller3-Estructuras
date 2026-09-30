package Ordenamiento;

public abstract class BaseSort {

    protected long comparaciones;   // cada llamada a less cuenta 1
    protected long intercambios;    // cada llamada a exch cuenta 1

    public long getComparaciones() { return comparaciones; }
    public long getIntercambios()  { return intercambios; }

    public void resetCounters() {
        comparaciones = 0;
        intercambios = 0;
    }

    public abstract <T extends Comparable<T>> void sort(T[] a);

    protected <T extends Comparable<T>> boolean less(T v, T w) {
        comparaciones++;
        return v.compareTo(w) < 0;
    }

    protected void exch(Object[] a, int i, int j) {
        intercambios++;
        Object t = a[i];
        a[i] = a[j];
        a[j] = t;
    }
}
