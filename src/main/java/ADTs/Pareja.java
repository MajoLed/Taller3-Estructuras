package ADTs;

public final class Pareja implements Comparable<Pareja> {

    private final int id1;
    private final int id2;
    private final double distancia;

    public Pareja(Persona p1, Persona p2) {
        if (p1.getId() == p2.getId()) {
            throw new IllegalArgumentException("La pareja debe ser de 2 personas distintas");
        }

        //id1 < id2
        this.id1 = Math.min(p1.getId(), p2.getId());
        this.id2 = Math.max(p1.getId(), p2.getId());

        double dLat = p1.getLat() - p2.getLat();
        double dLon = p1.getLon() - p2.getLon();
        this.distancia = dLat * dLat + dLon * dLon;
    }

    public int getId1() {
        return id1;
    }

    public int getId2() {
        return id2;
    }

    public double getDistancia() {
        return distancia;
    }

    // La raíz se calcula solo cuando se necesita (por ejemplo, para reportar r*)
    public double getDistanciaRaiz() {
        return Math.sqrt(distancia);
    }

    @Override
    public int compareTo(Pareja otra) {
        int c = Double.compare(this.distancia, otra.distancia);
        if (c != 0) return c;
        c = Integer.compare(this.id1, otra.id1);
        if (c != 0) return c;
        return Integer.compare(this.id2, otra.id2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pareja)) return false;
        Pareja p = (Pareja) o;
        return id1 == p.id1 && id2 == p.id2;
    }

    @Override
    public int hashCode() {
        return 31 * id1 + id2;
    }

    @Override
    public String toString() {
        return "ADTs.Pareja{" +
                "id1=" + id1 +
                ", id2=" + id2 +
                ", distancia=" + getDistancia() +
                '}';
    }
}