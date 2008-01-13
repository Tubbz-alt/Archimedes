
package br.org.archimedes.infiniteline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;

import br.org.archimedes.Constant;
import br.org.archimedes.Geometrics;
import br.org.archimedes.exceptions.IllegalActionException;
import br.org.archimedes.exceptions.InvalidArgumentException;
import br.org.archimedes.exceptions.InvalidParameterException;
import br.org.archimedes.exceptions.NullArgumentException;
import br.org.archimedes.gui.model.Workspace;
import br.org.archimedes.gui.opengl.OpenGLWrapper;
import br.org.archimedes.model.ComparablePoint;
import br.org.archimedes.model.Element;
import br.org.archimedes.model.Offsetable;
import br.org.archimedes.model.Point;
import br.org.archimedes.model.Rectangle;
import br.org.archimedes.model.ReferencePoint;
import br.org.archimedes.model.Trimmable;
import br.org.archimedes.model.Vector;
import br.org.archimedes.model.references.TrianglePoint;

public class InfiniteLine extends Element implements Offsetable, Trimmable {

    private Point initialPoint;

    private Point endingPoint;


    public InfiniteLine (double x1, double y1, double x2, double y2)
            throws InvalidArgumentException {

        initialPoint = new Point(x1, y1);
        endingPoint = new Point(x2, y2);
        if (initialPoint.equals(endingPoint)) {
            throw new InvalidArgumentException();
        }
    }

    /**
     * Creates an infinite line given two points.
     * 
     * @param initialPoint
     *            The first point
     * @param endingPoint
     *            The second point
     * @throws NullArgumentException
     *             Thrown if any point is null.
     * @throws InvalidArgumentException
     *             Thrown if boths points are equal.
     */
    public InfiniteLine (Point initialPoint, Point endingPoint)
            throws NullArgumentException, InvalidArgumentException {

        if (initialPoint == null || endingPoint == null) {
            throw new NullArgumentException();
        }

        if (initialPoint.equals(endingPoint)) {
            throw new InvalidArgumentException();
        }

        this.initialPoint = initialPoint;
        this.endingPoint = endingPoint;
    }

    /**
     * Creates an infinite line defined by a point and vector.
     * 
     * @param initialPoint
     *            The initial point.
     * @param direction
     *            The vector
     * @throws NullArgumentException
     *             In case any argument is null
     * @throws InvalidArgumentException
     *             Thrown if the vector is the null vector (0).
     */
    public InfiniteLine (Point initialPoint, Vector direction)
            throws NullArgumentException, InvalidArgumentException {

        if (initialPoint == null || direction == null) {
            throw new NullArgumentException();
        }
        if (Math.abs(direction.getNorm()) < Constant.EPSILON) {
            throw new InvalidArgumentException();
        }

        Point endingPoint = initialPoint.clone();
        endingPoint.addVector(direction);

        this.initialPoint = initialPoint;
        this.endingPoint = endingPoint;
    }

    /**
     * @param point
     *            The point to be tested.
     * @return Return true if the line contains the point, false otherwise.<br>
     *         (A line does not contains a null point).
     * @throws NullArgumentException
     *             In case the point is null.
     */
    @Override
    public boolean contains (Point point) throws NullArgumentException {

        boolean contains = false;

        if (point == null) {
            throw new NullArgumentException();
        }
        if ( !point.equals(getInitialPoint())) {
            double angle1 = Geometrics.calculateAngle(getInitialPoint(), point);
            double angle2 = Geometrics.calculateAngle(point, getInitialPoint());

            if (Math.abs(getAngle() - angle1) <= Constant.EPSILON
                    || Math.abs(getAngle() - angle2) <= Constant.EPSILON) {
                contains = true;
            }
        }
        else {
            contains = true;
        }

        return contains;
    }

    /**
     * @return Return the angle between the line and the x-axis.
     */
    public double getAngle () {

        return Geometrics.calculateAngle(initialPoint.getX(), initialPoint
                .getY(), endingPoint.getX(), endingPoint.getY());
    }

    public boolean equals (InfiniteLine line) {

        try {
            double alpha = Math.abs(this.getAngle() - line.getAngle());
            if (alpha >= Constant.EPSILON
                    && Math.PI - alpha >= Constant.EPSILON)
                return false;

            return contains(line.getInitialPoint());
        }
        catch (NullArgumentException e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public Element clone () {

        InfiniteLine clone = null;
        try {
            clone = new InfiniteLine(getInitialPoint(), getEndingPoint());
            clone.setLayer(getLayer());
        }
        catch (Exception e) {
            // Should never happen
            e.printStackTrace();
        }
        return clone;
    }

    @Override
    public boolean isInside (Rectangle rectangle) {

        return false;
    }

    @Override
    public boolean equals (Object object) {

        boolean equals = false;
        try {
            equals = ((InfiniteLine) object).equals(this);
        }
        catch (ClassCastException e) {
            // It's not equal.
        }
        return equals;
    }

    @Override
    public Rectangle getBoundaryRectangle () {

        return null;
    }

    public String toString () {

        return "Infinite Line: angle =" + getAngle(); //$NON-NLS-1$
    }

    /**
     * @return the initialPoint
     */
    public Point getInitialPoint () {

        return initialPoint;
    }

    /**
     * @param initialPoint
     *            the initialPoint to set
     */
    public void setInitialPoint (Point initialPoint) {

        this.initialPoint = initialPoint;
    }

    @Override
    public void draw (OpenGLWrapper wrapper) {

        Rectangle modelRect = Workspace.getInstance().getCurrentViewportArea();
        Point[] pointsToDraw = getPointsCrossing(modelRect);
        if (pointsToDraw != null) {
            wrapper.drawFromModel(pointsToDraw);
        }
    }

    /**
     * @param rectangle
     *            The rectangle that this line should cross in model
     *            coordinates.
     * @return The line crossing the whole rectangle.
     */
    protected Point[] getPointsCrossing (Rectangle rectangle) {
        Point[] points;
               
        double sen = (initialPoint.getX() - endingPoint.getX());
        double tan = (initialPoint.getY() - endingPoint.getY()) / sen;
        if (Math.abs(sen) < Constant.EPSILON) {
        	points = getVerticalLine(rectangle.getLowerLeft().getY(), rectangle.getUpperRight().getY());
        }
        else if (Math.abs(tan) < Constant.EPSILON) {
            points = getHorizontalLine(rectangle.getLowerLeft().getX(), rectangle.getUpperRight().getX());
        }
        else {
            double b = initialPoint.getY() - (tan) * initialPoint.getX();
            Point lowerLeftModel = rectangle.getLowerLeft();
            Point upperRightModel = rectangle.getUpperRight();

            Point lefterPoint = new Point(lowerLeftModel.getX(), (tan
                    * lowerLeftModel.getX() + b));
            Point righterPoint = new Point(upperRightModel.getX(), (tan
                    * upperRightModel.getX() + b));
            Point lowerPoint = new Point((lowerLeftModel.getY() - b) / tan,
                    lowerLeftModel.getY());
            Point upperPoint = new Point((upperRightModel.getY() - b) / tan,
                    upperRightModel.getY());

            points = new Point[2];
            int i = 0;
            if (lefterPoint.getY() <= upperRightModel.getY()
                    && lefterPoint.getY() >= lowerLeftModel.getY() && i < 2) {
                points[i++] = lefterPoint;
            }
            if (righterPoint.getY() <= upperRightModel.getY()
                    && righterPoint.getY() >= lowerLeftModel.getY() && i < 2) {
                points[i++] = righterPoint;
            }
            if (upperPoint.getX() <= upperRightModel.getX()
                    && upperPoint.getX() >= lowerLeftModel.getX() && i < 2) {
                points[i++] = upperPoint;
            }
            if (lowerPoint.getX() <= upperRightModel.getX()
                    && lowerPoint.getX() >= lowerLeftModel.getX() && i < 2) {
                points[i++] = lowerPoint;
            }

            if (i < 2) {
                points = null;
            }
        }

        return points;
    }

    /**
     * @param minX The lowest X coordinate of the containing rectangle.
     * @param maxX The highest X coordinate of the containing rectangle.
     * 
     * @return Point[] An array with the Points that define the horizontal line.
     */
    private Point[] getHorizontalLine (double minX, double maxX) {
        Point[] points = new Point[2];
        points[0] = new Point(minX, this.getInitialPoint().getY());
        points[1] = new Point(maxX, this.getInitialPoint().getY());
        return points;
    }

    /**
     * @param minY The lowest Y coordinate of the containing rectangle.
     * @param maxY The highest Y coordinate of the containing rectangle.
     * 
     * @return Point[] An array with the Points that define the vertical line.
     */
    private Point[] getVerticalLine (double minY, double maxY) {

        Point[] points = new Point[2];
        points[0] = new Point(this.getInitialPoint().getX(), minY);
        points[1] = new Point(this.getInitialPoint().getX(), maxY);
        return points;
    }

    @Override
    public Collection<Point> getIntersection (Element element)
            throws NullArgumentException {

        // TODO implementar quando tiver intersection
        return new ArrayList<Point>();
    }

    @Override
    public Point getNearestExtremePoint (Point point)
            throws NullArgumentException {

        return null;
    }

    @Override
    public List<Point> getPoints () {

        List<Point> points = new ArrayList<Point>();
        points.add(initialPoint);
        points.add(endingPoint);
        return points;
    }

    /**
     * (non-Javadoc).
     * 
     * @see br.org.archimedes.model.Element#getProjectionOf(br.org.archimedes.model.Point)
     */
    @Override
    public Point getProjectionOf (Point point) throws NullArgumentException {

        if (point == null) {
            throw new NullArgumentException();
        }

        double lineA = (endingPoint.getY() - initialPoint.getY())
                / (endingPoint.getX() - initialPoint.getX());

        if (lineA == 0) {
            return new Point(point.getX(), this.initialPoint.getY());
        }
        else if (lineA == Double.POSITIVE_INFINITY
                || lineA == Double.NEGATIVE_INFINITY) {
            return new Point(this.initialPoint.getX(), point.getY());
        }

        double lineB = endingPoint.getY() - (lineA * endingPoint.getX());

        double perpA = -1 / lineA;
        double perpB = point.getY() - (perpA * point.getX());

        double targetX = (perpB - lineB) / (lineA - perpA);
        double targetY = (perpA * targetX) + perpB;

        return new Point(targetX, targetY);
    }

    @Override
    public Collection<? extends ReferencePoint> getReferencePoints (
            Rectangle area) {

        Point[] pointsCrossing = getPointsCrossing(area);
        if (pointsCrossing != null) {
            try {
                Point meanPoint = Geometrics.getMeanPoint(pointsCrossing[0],
                        pointsCrossing[1]);
                TrianglePoint meanReference = new TrianglePoint(meanPoint,
                        initialPoint, endingPoint);
                return Collections.singletonList(meanReference);
            }
            catch (NullArgumentException e) {
                // Should not happen
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public boolean intersects (Rectangle rectangle)
            throws NullArgumentException {

        // TODO fazer quando tivermos intersec��o
        return false;
    }

    @Override
    public boolean isCollinearWith (Element element) {

        // TODO implementar
        return false;
    }

    @Override
    public boolean isParallelTo (Element element) {

        // TODO implementar
        return false;
    }

    /**
     * (non-Javadoc).
     * 
     * @see br.org.archimedes.model.Offsetable#cloneWithDistance(double)
     */
    public Element cloneWithDistance (double distance)
            throws InvalidParameterException {

        Vector direction = new Vector(getInitialPoint(), getEndingPoint());
        direction = Geometrics.normalize(direction);
        direction = direction.getOrthogonalVector();
        direction = direction.multiply(distance);

        InfiniteLine returnLine = (InfiniteLine) this.clone();
        returnLine.move(direction.getX(), direction.getY());
        returnLine.setLayer(parentLayer);

        return returnLine;
    }

    /**
     * (non-Javadoc).
     * 
     * @see br.org.archimedes.model.Offsetable#isPositiveDirection(br.org.archimedes.model.Point)
     */
    public boolean isPositiveDirection (Point point)
            throws NullArgumentException {

        double determinant = Geometrics.calculateDeterminant(getInitialPoint(),
                getEndingPoint(), point);

        return (determinant >= 0);
    }

    public Collection<Element> trim (Collection<Element> references, Point click)
            throws IllegalActionException {

        // TODO Auto-generated method stub
        return null;
    }

    public SortedSet<ComparablePoint> getSortedPointSet (Point referencePoint,
            Collection<Point> intersectionPoints) {

        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @return the endingPoint
     */
    public Point getEndingPoint () {

        return endingPoint;
    }

    /**
     * @param endingPoint
     *            the endingPoint to set
     */
    public void setEndingPoint (Point endingPoint) {

        this.endingPoint = endingPoint;
    }

}