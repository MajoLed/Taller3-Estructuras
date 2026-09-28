package Modelos;

public class WeightedQuickUnion extends BaseUnionFind {

    private final int[] parent;
    private final int[] size;

    public WeightedQuickUnion(int n) {
        super(n);
        parent = new int[n];
        size = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }

    public int find(int p) {
        validate(p);
        int raiz = p;
        int padre = parent[raiz];
        accesosFind++;
        while (padre != raiz) {
            raiz = padre;
            padre = parent[raiz];
            accesosFind++;
        }
        return raiz;
    }

    @Override
    protected long unir(int raizP, int raizQ) {
        int tamP = size[raizP];         // 1 lectura
        int tamQ = size[raizQ];         // 1 lectura
        if (tamP < tamQ) {
            parent[raizP] = raizQ;      // 1 escritura
            size[raizQ] = tamP + tamQ;  // 1 escritura
        } else {
            parent[raizQ] = raizP;
            size[raizP] = tamP + tamQ;
        }
        return 4;                       // 2 lecturas + 2 escrituras
    }

    @Override
    public String toString() {
        String s = "-".repeat(15) + "Weighted Quick Union" + "-".repeat(15) + "\n";
        s += "parent[] = " + java.util.Arrays.toString(parent) + "\n";
        s += "size[]   = " + java.util.Arrays.toString(size) + "\n";
        s += "-".repeat(15);
        return s;
    }
}