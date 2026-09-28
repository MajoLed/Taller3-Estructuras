package Modelos;

public abstract class BaseUnionFind implements UnionFind {

    protected final int n;
    protected int count;
    protected long accesosFind;
    protected long accesosUnion;

    protected BaseUnionFind (int n) {
        this.n = n;
        this.count = n;
    }

    public int count() { return count; }
    public long getAccesosFind() { return accesosFind; }
    public long getAccesosUnion() { return accesosUnion; }

    public void resetCounters() {
        accesosFind = 0;
        accesosUnion = 0;
    }

    protected void validate(int p) {
        if (p < 0 || p >= n) {
            throw new IllegalArgumentException("index " + p + " is not between 0 and " + (n - 1));
        }
    }

    public boolean connected(int p, int q) {
        return find(p) == find(q); // sus accesos ya se cuentan dentro de find
    }

    public final void union(int p, int q) {
        long antes = accesosFind;
        int raizP = find(p);
        int raizQ = find(q);
        long costo = accesosFind - antes;   // accesos de los dos find

        if (raizP != raizQ) {
            costo += unir(raizP, raizQ);    // + accesos propios
            count--;
        }
        accesosUnion += costo;              // una unión redundante también cuesta
    }

    // Une las dos raíces y devuelve SOLO los accesos propios de la unión
    protected abstract long unir(int raizP, int raizQ);
}
