package br.org.archimedes.trimmers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import br.org.archimedes.Geometrics;
import br.org.archimedes.ellipse.Ellipse;
import br.org.archimedes.exceptions.NullArgumentException;
import br.org.archimedes.model.ComparablePoint;
import br.org.archimedes.model.DoubleKey;
import br.org.archimedes.model.Element;
import br.org.archimedes.model.Point;
import br.org.archimedes.trims.interfaces.Trimmer;

public class EllipseTrimmer implements Trimmer {

	public Collection<Element> trim(Element element,
			Collection<Point> givenCutPoints, Point click)
			throws NullArgumentException {

		if (element == null || givenCutPoints == null || click == null) {
			throw new NullArgumentException();
		}
		Ellipse ellipse = (Ellipse) element;
		Collection<Point> cutPoints = new ArrayList<Point>();
		Collection<Element> trimResult = new ArrayList<Element>();

		for (Point p : givenCutPoints)
			if (ellipse.contains(p))
				cutPoints.add(p);

		if (cutPoints.size() <= 1)
			return Collections.singleton(element);

		// using the point on the end of the major axis
		Point initialPoint = ellipse.getCenter().clone();
		initialPoint.addVector(ellipse.getSemiMajorAxis());

		// Sort the cut points.
		SortedSet<ComparablePoint> sortedPointSet = getSortedPointSet(ellipse,
				initialPoint, cutPoints);

		// Create the base comparison point, using the mouse click point.
		ComparablePoint clickPoint = null;
		try {
			double key = Geometrics.calculateRelativeAngle(ellipse.getCenter(),
					initialPoint, click);
			clickPoint = new ComparablePoint(click, new DoubleKey(key));

		} catch (NullArgumentException e) {
			e.printStackTrace();
		}

		SortedSet<ComparablePoint> negativeIntersections = sortedPointSet
				.headSet(clickPoint);
		SortedSet<ComparablePoint> positiveIntersections = sortedPointSet
				.tailSet(clickPoint);

		Point first = null;
		Point last = null;
		if (positiveIntersections.size() > 0)
			first = positiveIntersections.first().getPoint();
		else
			first = negativeIntersections.first().getPoint();
		if (negativeIntersections.size() > 0)
			last = negativeIntersections.last().getPoint();
		else
			last = positiveIntersections.last().getPoint();

		// TODO: Create the EllipseArc here.

		return trimResult;
	}

	private SortedSet<ComparablePoint> getSortedPointSet(Ellipse ellipse,
			Point referencePoint, Collection<Point> cutPoints) {

		SortedSet<ComparablePoint> sortedSet = new TreeSet<ComparablePoint>();

		for (Point point : cutPoints) {
			try {
				double key = Geometrics.calculateRelativeAngle(
						ellipse.getCenter(), referencePoint, point);
				ComparablePoint orderedPoint = new ComparablePoint(point,
						new DoubleKey(key));
				sortedSet.add(orderedPoint);
			} catch (NullArgumentException e) {
				// Should not catch this exception
				e.printStackTrace();
			}

		}

		return sortedSet;
	}

}