package geofence;


public class Polygon {

    private Point[] points;

    public Polygon(Point[] points) {
        this.points = points;
    }

    public Point[] getPoints() {
        return points;
    }

}
