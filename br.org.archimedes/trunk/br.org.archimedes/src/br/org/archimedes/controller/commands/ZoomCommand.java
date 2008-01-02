/*
 * Created on 26/08/2006
 */

package br.org.archimedes.controller.commands;

import br.org.archimedes.Constant;
import br.org.archimedes.exceptions.IllegalActionException;
import br.org.archimedes.exceptions.NullArgumentException;
import br.org.archimedes.interfaces.UndoableCommand;
import br.org.archimedes.model.Drawing;
import br.org.archimedes.model.Point;

/**
 * Belongs to package br.org.archimedes.model.commands.
 * 
 * @author Nitao
 */
public abstract class ZoomCommand implements UndoableCommand {

    private double previousZoom;

    private Point previousViewport;

    private double newZoom;

    private Point newViewport;


    public ZoomCommand () {

        previousZoom = -1;
        previousViewport = null;
    }

    public void doIt (Drawing drawing) throws IllegalActionException,
            NullArgumentException {

        if (drawing == null) {
            throw new NullArgumentException();
        }

        if (previousZoom < 0) {
            previousZoom = drawing.getZoom();
            previousViewport = drawing.getViewportPosition();
            newZoom = calculateZoom(drawing);
            newViewport = getNewViewport(drawing);
        }

        if (newZoom > Constant.ZOOM_SUPERIOR_LIMIT) {
            newZoom = previousZoom;
            newViewport = previousViewport;
            throw new IllegalActionException(Messages.Zoom_reachedMax);
        }

        if (newViewport != null) {
            drawing.setViewportPosition(newViewport);
        }
        drawing.setZoom(newZoom);
    }

    public void undoIt (Drawing drawing) throws IllegalActionException,
            NullArgumentException {

        if (drawing == null) {
            throw new NullArgumentException();
        }

        if (previousZoom <= 0) {
            throw new IllegalActionException();
        }
        else {
            drawing.setZoom(previousZoom);
            if (previousViewport != null) {
                drawing.setViewportPosition(previousViewport);
            }
        }
    }

    /**
     * @param drawing
     *            The drawing of which the viewport must be changed.
     * @return The new viewport position or null if it shouldn't be changed.
     */
    protected abstract Point getNewViewport (Drawing drawing);

    /**
     * @param drawing
     *            The drawing on which the zoom must be performed.
     * @return The new zoom factor
     */
    protected abstract double calculateZoom (Drawing drawing);

    /**
     * @return Returns the previousZoom.
     */
    protected double getPreviousZoom () {

        return previousZoom;
    }

    /**
     * @return The previous viewport position
     */
    protected Point getPreviousViewport () {

        return this.previousViewport;
    }
}
