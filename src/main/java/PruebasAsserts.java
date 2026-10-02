import ADTs.Persona;
import ADTs.Pareja;
import Modelos.QuickFind;
import Modelos.QuickUnion;
import Modelos.WeightedQuickUnion;
import Modelos.UnionFind;
public class PruebasAsserts {

    static int ok = 0, fallos = 0;

    public static void main(String[] args) {
        pruebasPersonaPareja();
        pruebasUnionFind();
        System.out.println("\nResumen: " + ok + " OK, " + fallos + " fallos");
    }

    // ---------------- Punto 1: Persona y Pareja ----------------
    static void pruebasPersonaPareja() {
        System.out.println("== Persona / Pareja ==");

        // Distancia cero: misma persona duplicada en dos objetos
        Persona a = new Persona(1, 6.25, -75.57);
        Persona aCopia = new Persona(2, 6.25, -75.57);
        Pareja pDistanciaCero = new Pareja(a, aCopia);
        verificar("distancia cero", pDistanciaCero.getDistancia() == 0.0);

        // Simetria: d(p,q) == d(q,p)
        Persona b = new Persona(3, 6.30, -75.50);
        Pareja pab = new Pareja(a, b);
        Pareja pba = new Pareja(b, a);
        verificar("simetria d(p,q)=d(q,p)", pab.getDistancia() == pba.getDistancia());
        verificar("orden canonico id1<id2 (ambos constructores)",
                pab.getId1() == pba.getId1() && pab.getId2() == pba.getId2());

        // Orden por distancia: una pareja mas cercana debe compararse antes
        Persona c = new Persona(4, 8.00, -74.00); // lejos de a
        Pareja cercana = new Pareja(a, b);
        Pareja lejana = new Pareja(a, c);
        verificar("orden por distancia", cercana.compareTo(lejana) < 0);

        // Desempate: distancias iguales, se decide por ids
        Persona d1 = new Persona(10, 6.25, -75.57);
        Persona d2 = new Persona(20, 6.26, -75.57);
        Persona d3 = new Persona(30, 6.26, -75.57); // misma distancia a d1 que d2
        Pareja par1 = new Pareja(d1, d2); // ids (10,20)
        Pareja par2 = new Pareja(d1, d3); // ids (10,30), misma distancia
        verificar("desempate por distancia igual -> por ids",
                par1.compareTo(par2) < 0 && par1.getDistancia() == par2.getDistancia());

        // Pareja con personas iguales debe fallar
        boolean lanzoExcepcion = false;
        try {
            new Pareja(a, new Persona(1, 1, 1));
        } catch (IllegalArgumentException e) {
            lanzoExcepcion = true;
        }
        verificar("rechaza pareja con mismo id", lanzoExcepcion);
    }

    // ---------------- Punto 2: Union-Find ----------------
    static void pruebasUnionFind() {
        System.out.println("\n== Union-Find ==");

        pruebasComportamiento(new QuickFind(10), "QuickFind");
        pruebasComportamiento(new QuickUnion(10), "QuickUnion");
        pruebasComportamiento(new WeightedQuickUnion(10), "WeightedQuickUnion");

        pruebaMismaParticion();
        pruebaManual();
    }

    static void pruebasComportamiento(UnionFind uf, String nombre) {
        // count() baja exactamente 1 en union efectiva
        int antes = uf.count();
        uf.union(0, 1);
        verificar(nombre + ": count baja 1 en union efectiva", uf.count() == antes - 1);

        // count() no cambia en union redundante
        int antesRed = uf.count();
        uf.union(0, 1); // ya estan unidos
        verificar(nombre + ": count no cambia en redundante", uf.count() == antesRed);

        // connected reflexiva
        verificar(nombre + ": connected reflexiva", uf.connected(5, 5));

        // connected simetrica
        uf.union(2, 3);
        verificar(nombre + ": connected simetrica",
                uf.connected(2, 3) == uf.connected(3, 2));

        // connected transitiva
        uf.union(3, 4);
        verificar(nombre + ": connected transitiva", uf.connected(2, 4));
    }

    static void pruebaMismaParticion() {
        int[][] uniones = {{0,1},{2,3},{1,2},{4,5},{3,4}};
        UnionFind qf = new QuickFind(6);
        UnionFind qu = new QuickUnion(6);
        UnionFind wqu = new WeightedQuickUnion(6);

        for (int[] u : uniones) {
            qf.union(u[0], u[1]);
            qu.union(u[0], u[1]);
            wqu.union(u[0], u[1]);
        }

        boolean mismaParticion = true;
        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 6; j++)
                if (qf.connected(i, j) != qu.connected(i, j) || qu.connected(i, j) != wqu.connected(i, j))
                    mismaParticion = false;

        verificar("las tres dan la misma particion", mismaParticion);
        verificar("las tres dan el mismo count()", qf.count() == qu.count() && qu.count() == wqu.count());
    }

    static void pruebaManual() {
        // Caso calculado a mano: WeightedQuickUnion con n=4, union(0,1) una sola vez
        WeightedQuickUnion uf = new WeightedQuickUnion(4);
        uf.resetCounters();
        uf.union(0, 1);
        // find(0): 1 acceso, find(1): 1 acceso -> accesosFind = 2
        // union propia: 2 lecturas de size + 2 escrituras = 4
        // accesosUnion = 2 + 4 = 6
        verificar("verificacion manual accesosFind", uf.getAccesosFind() == 2);
        verificar("verificacion manual accesosUnion", uf.getAccesosUnion() == 6);
    }

    static void verificar(String nombre, boolean condicion) {
        if (condicion) {
            System.out.println("  OK   - " + nombre);
            ok++;
        } else {
            System.out.println("  FALLO - " + nombre);
            fallos++;
        }
    }
}