/*
 * Created on 26/08/2006
 */

package br.org.archimedes.controller.commands;

import br.org.archimedes.Constant;
import br.org.archimedes.model.Drawing;
import br.org.archimedes.model.Point;
import br.org.archimedes.model.Rectangle;

/**
 * Belongs to package br.org.archimedes.model.commands.
 * 
 * @author Nitao
 */
public class ZoomExtendCommand extends ZoomCommand {

    private ZoomCommand zoomByArea;


    /*
     * (non-Javadoc)
     * 
     * @see br.org.archimedes.model.commands.TheZoomCommand#getNewViewport()
     */
    @Override
    protected Point getNewViewport (Drawing drawing) {

        if (zoomByArea == null) {
            zoomByArea = calculateArea(drawing);
        }

        return zoomByArea.getNewViewport(drawing);
    }

    /*
     * (non-Javadoc)
     * 
     * @see br.org.archimedes.model.commands.TheZoomCommand#calculateZoom()
     */
    @Override
    protected double calculateZoom (Drawing drawing) {

        if (zoomByArea == null) {
            zoomByArea = calculateArea(drawing);
        }

        return zoomByArea.calculateZoom(drawing);
    }

    /**
     * @param drawing
     *            The drawing in which the area should be calculated.
     * @return The zoom by area that should be performed.
     */
    private ZoomCommand calculateArea (Drawing drawing) {

        Rectangle area = drawing.getBoundary();
        ZoomCommand zoomByArea = null;
        if (area != null) {
            double x, y, width, height;
            x = area.getLowerLeft().getX() - area.getWidth()
                    * (Constant.ZOOM_EXTEND_BORDER / 2.0);
            width = area.getWidth() * (1.0 + Constant.ZOOM_EXTEND_BORDER);
            y = area.getLowerLeft().getY() - area.getHeight()
                    * (Constant.ZOOM_EXTEND_BORDER / 2.0);
            height = area.getHeight() * (1.0 + Constant.ZOOM_EXTEND_BORDER);

            if (width > Constant.EPSILON || height > Constant.EPSILON) {
                Point p1 = new Point(x, y);
                Point p2 = new Point(x + width, y + height);
                try {
                    zoomByArea = new ZoomByAreaCommand(p1, p2);
                }
                catch (Exception e) {
                    // Should never reach this block
                }
            }
        }
        else {
            zoomByArea = new RelativeZoomCommand(1.0);
        }
        return zoomByArea;
    }
}
