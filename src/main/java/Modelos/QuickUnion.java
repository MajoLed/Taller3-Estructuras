package Modelos;

public class QuickUnion extends BaseUnionFind {

    private final int[] parent;

    public QuickUnion(int n) {
        super(n);
        parent = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;
    }

    public int find(int p) {
        validate(p);
        int raiz = p;
        int padre = parent[raiz];
        accesosFind++;                  // una lectura por cada nodo visitado
        while (padre != raiz) {
            raiz = padre;
            padre = parent[raiz];
            accesosFind++;
        }
        return raiz;
    }

    @Override
    protected long unir(int raizP, int raizQ) {
        parent[raizP] = raizQ;
        return 1;                       // 1 escritura
    }

    @Override
    public String toString() {
        String s = "-".repeat(15) + "Quick Union" + "-".repeat(15) + "\n";
        s += "parent[] = " + java.util.Arrays.toString(parent) + "\n";
        s += "-".repeat(15);
        return s;
    }
}