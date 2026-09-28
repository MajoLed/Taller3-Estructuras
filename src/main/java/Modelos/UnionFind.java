package Modelos;

public interface UnionFind {
    int count();
    int find(int p);
    boolean connected(int p, int q);
    void union(int p, int q);

    long getAccesosFind();
    long getAccesosUnion();
    void resetCounters();
}