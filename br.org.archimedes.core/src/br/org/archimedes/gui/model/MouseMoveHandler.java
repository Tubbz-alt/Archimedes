/**
 * Copyright (c) 2006, 2009 Hugo Corbucci and others.<br>
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html<br>
 * <br>
 * Contributors:<br>
 * Hugo Corbucci - initial API and implementation<br>
 * <br>
 * This file was created on 2006/04/07, 00:03:02, by Hugo Corbucci.<br>
 * It is part of package br.org.archimedes.gui.model on the br.org.archimedes.core project.<br>
 */
package br.org.archimedes.gui.model;

import java.util.Observable;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;

import br.org.archimedes.exceptions.NullArgumentException;
import br.org.archimedes.model.Point;

public class MouseMoveHandler extends Observable {

    private static MouseMoveHandler instance;


    /**
     * Constructor. It's Empty.
     */
    private MouseMoveHandler () {

    }

    /**
     * @return the unique instance of the MouseClickHandler.
     */
    public static MouseMoveHandler getInstance () {

        if (instance == null) {
            instance = new MouseMoveHandler();
        }
        return instance;
    }

    /**
     * This method receives the mouse move event from the current Canvas, and
     * sends to the observers the normalized point.
     * 
     * @param event The mouse event received
     */
    public void receiveMouseMove (MouseEvent event) {

        Point point;

        Canvas canvas = (Canvas) event.getSource();

        Rectangle rect = canvas.getClientArea();

        double x = (double) event.x;
        double y = (double) event.y;

        point = new Point(x - rect.width / 2, (rect.height - y) - rect.height
                / 2);

        Workspace workspace = br.org.archimedes.Utils.getWorkspace();
        try {
			point = workspace.screenToModel(point);
        }
        catch (NullArgumentException e) {
            e.printStackTrace();
        }
        workspace.setMousePosition(point);
        
        setChanged();
        notifyObservers(point);
    }
}
