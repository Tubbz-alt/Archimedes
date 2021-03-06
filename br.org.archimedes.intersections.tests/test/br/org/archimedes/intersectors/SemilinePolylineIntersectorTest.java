/**
 * Copyright (c) 2008, 2009 Hugo Corbucci and others.<br>
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html<br>
 * <br>
 * Contributors:<br>
 * Hugo Corbucci - initial API and implementation<br>
 * <br>
 * This file was created on 2008/09/18, 01:36:41, by Hugo Corbucci.<br>
 * It is part of package br.org.archimedes.intersectors on the br.org.archimedes.intersections.tests project.<br>
 */
package br.org.archimedes.intersectors;

import br.org.archimedes.Tester;
import br.org.archimedes.exceptions.InvalidArgumentException;
import br.org.archimedes.exceptions.NullArgumentException;
import br.org.archimedes.intersections.interfaces.Intersector;
import br.org.archimedes.model.Point;
import br.org.archimedes.polyline.Polyline;
import br.org.archimedes.semiline.Semiline;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SemilinePolylineIntersectorTest extends Tester {

	@Test
	public void testSemilineIntersectsPolylineReturnsOneIntersectionPoint()
			throws InvalidArgumentException, NullArgumentException {
		Semiline semiline = new Semiline(0.0, 1.0, 0.0, -1.0);
		List<Point> list = new ArrayList<Point>();
		list.add(new Point(-1.0, 0.0));
		list.add(new Point(1.0, 0.0));
		Polyline polyline = new Polyline(list);

		Intersector intersector = new SemilinePolylineIntersector();

		Collection<Point> intersections = intersector.getIntersections(semiline,
				polyline);
		Point point = new Point(0.0, 0.0);

		assertCollectionTheSame(Collections.singleton(point), intersections);

	}
	
	@Test
	public void testSemilineIntersectsPolylineReturnsManyIntersectionPoints()
			throws InvalidArgumentException, NullArgumentException {
		
		Semiline semiline = new Semiline(0.0, 1.0, 0.0, -1.0);
		List<Point> list = new ArrayList<Point>();
		list.add(new Point(-1.0, 0.0));
		list.add(new Point(1.0, 0.0));
		list.add(new Point(1.0, 1.0));
		list.add(new Point(-1.0, 1.0));
		
		Polyline polyline = new Polyline(list);

		Intersector intersector = new SemilinePolylineIntersector();

		Collection<Point> intersections = intersector.getIntersections(semiline,
				polyline);
		
		List<Point> intersectionPoints = new ArrayList<Point>();
		intersectionPoints.add(new Point(0.0, 0.0));
		intersectionPoints.add(new Point(0.0, 1.0));

		assertCollectionTheSame(intersectionPoints, intersections);

	}
	
	@Test
	public void testSemilineIntersectsPolylineReturnsNoIntersectionPoints()
			throws InvalidArgumentException, NullArgumentException {
		
		Semiline semiline = new Semiline(0.0, 1.0, 0.0, -1.0);
		List<Point> list = new ArrayList<Point>();
		
		list.add(new Point(1.0, 0.0));
		list.add(new Point(1.0, 1.0));
		
		
		Polyline polyline = new Polyline(list);

		Intersector intersector = new SemilinePolylineIntersector();

		Collection<Point> intersections = intersector.getIntersections(semiline,
				polyline);
		
		assertTrue(intersections.isEmpty());

	}
	
	@Test
	public void polylineIntersectsSemilineReturnsNoIntersectionPoints() throws InvalidArgumentException, NullArgumentException {
		Semiline semiline = new Semiline(-0.5, 0.0,	0.5, 0.0);
		List<Point> list = new ArrayList<Point>();
		list.add(new Point(-1.0, 0.0));
		list.add(new Point(1.0, 0.0));
		Polyline polyline = new Polyline(list);

		Intersector intersector = new SemilinePolylineIntersector();

		Collection<Point> intersections = intersector.getIntersections(semiline,
				polyline);

		assertCollectionTheSame(Collections.emptyList(), intersections);
	}
	
	@Test
	public void testSemilinePolylineIntersectorNullArgument() throws NullArgumentException, InvalidArgumentException{
		
		Semiline semiline = new Semiline(0.0, 1.0, 0.0, -1.0);
		
		List<Point> list = new ArrayList<Point>();
		list.add(new Point(-1.0, 0.0));
		list.add(new Point(1.0, 0.0));
		list.add(new Point(1.0, 1.0));
		list.add(new Point(-1.0, 1.0));
		
		Polyline polyline = new Polyline(list);
		
		Intersector intersector = new SemilinePolylineIntersector();
		
		try{
			intersector.getIntersections(semiline,null);
			fail("Should throw exception because of null polyline argument");			
		} catch (NullArgumentException e){
			// Passed
		}
		
		try{
			intersector.getIntersections(null,polyline);
			fail("Should throw exception because of null line argument");			
		} catch (NullArgumentException e){
			// Passed
		}
		
		try{
			intersector.getIntersections(null,null);
			fail("Should throw exception because of null polyline and line argument");			
		} catch (NullArgumentException e){
			// Passed
		}		
			
	}
}
