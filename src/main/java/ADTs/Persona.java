package ADTs;// Final para que sea inmutable


public final class Persona {

    private final int id; //Final usado para que la clase sea inmutable
    private final double lat;
    private final double lon;

    public Persona(int id, double lat, double lon) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
    }

    public int getId() {
        return id;
    }

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }

    @Override
    public String toString() {
        return "ADTs.Persona{" +
                "id=" + id +
                ", lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}
