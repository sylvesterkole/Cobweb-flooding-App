package geofence;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;



public class GeofencingTest {

	/**
	 * @param args
	 */
	boolean checkInside(Polygon polygon, double x, double y) {
		List<Line> lines = calculateLines(polygon);
		List<Line> intersectionLines = filterIntersectingLines(lines, y);
		List<Point> intersectionPoints = calculateIntersectionPoints(
				intersectionLines, y);
		sortPointsByX(intersectionPoints);
		return calculateInside(intersectionPoints, x);
	}


	List<Line> calculateLines(Polygon polygon) {
		List<Line> results = new LinkedList<Line>();

		// get the polygon points
		Point[] points = polygon.getPoints();

		// form lines by connecting the points
		Point lastPoint = null;
		for (Point point : points) {
			if (lastPoint != null) {
				results.add(new Line(lastPoint, point));
			}
			lastPoint = point;
		}

		// close the polygon by connecting the last point 
		// to the first point
		results.add(new Line(lastPoint, points[0]));

		return results;
	}





	boolean isLineIntersectingAtY(Line line, double y) {
		double minY = Math.min(
				line.getFrom().getY(), line.getTo().getY()
				);
		double maxY = Math.max(
				line.getFrom().getY(), line.getTo().getY()
				);
		return y > minY && y <= maxY;
	}



	List<Line> filterIntersectingLines(List<Line> lines, double y) {
		List<Line> results = new LinkedList<Line>();
		for (Line line : lines) {
			if (isLineIntersectingAtY(line, y)) {
				results.add(line);
			}
		}
		return results;
	}




	List<Point> calculateIntersectionPoints(List<Line> lines, double y) {
		List<Point> results = new LinkedList<Point>();
		for (Line line : lines) {
			double x = calculateLineXAtY(line, y);
			results.add(new Point(x, y));
		}
		return results;
	}

	double calculateLineXAtY(Line line, double y) {
		Point from = line.getFrom();
		double slope = calculateSlope(line);
		return from.getX() + (y - from.getY()) / slope;
	}

	double calculateSlope(Line line) {
		Point from = line.getFrom();
		Point to = line.getTo();
		return (to.getY() - from.getY()) / (to.getX() - from.getX());
	}
	void sortPointsByX(List<Point> points) {
		Collections.sort(points, new Comparator<Point>() {
			public int compare(Point p1, Point p2) {
				return Double.compare(p1.getX(), p2.getX());
			}
		});
	}
	boolean calculateInside(List<Point> sortedPoints, double x) {
		boolean inside = false;
		for (Point point : sortedPoints) {
			if (x < point.getX()) {
				break;
			}
			inside = !inside;
		}
		return inside;
	}




	public static void main(String[] args) {
		Point pt1=new Point(53.308441, -6.223841);
		Point pt2=new Point(53.308672, -6.225353);
		Point pt3=new Point(53.309005, -6.225182);
		Point pt4=new Point(53.308826, -6.223551);
		
		Point[] polygondata= new Point[4];
		
		polygondata[0]=pt1;
		polygondata[1]=pt2;
		polygondata[2]=pt3;
		polygondata[3]=pt4;
		
		
		Polygon polygon=new Polygon(polygondata);
		
		GeofencingTest geo= new GeofencingTest();
		System.out.println(geo.checkInside(polygon,53.308883,-6.22515));
		
		
	}

}
