package Modelos;

import Modelos.BaseUnionFind;

public class QuickFind extends BaseUnionFind {

    private final int[] id;

    public QuickFind(int n) {
        super(n);
        id = new int[n];
        for (int i = 0; i < n; i++) id[i] = i;
    }

    public int find(int p) {
        validate(p);
        accesosFind++;          // 1 lectura
        return id[p];
    }

    @Override
    protected long unir(int pID, int qID) {
        long propios = 0;
        for (int i = 0; i < n; i++) {
            propios++;          // lectura de id[i]
            if (id[i] == pID) {
                id[i] = qID;
                propios++;      // escritura
            }
        }
        return propios;
    }

    // conserva tu toString (sin llamar a printState dentro)
    public String toString(){

        String estdoArbol = "-".repeat(15) + "Quick Find" + "-".repeat(15);

        estdoArbol += "Id [ ] = " + java.util.Arrays.toString(id);

        estdoArbol += "-".repeat(15);

        return estdoArbol;

    }
}