/*
 * Created on 06/09/2006
 */

package br.org.archimedes.factories;

import java.util.Set;

import br.org.archimedes.controller.Controller;
import br.org.archimedes.exceptions.InvalidParameterException;
import br.org.archimedes.exceptions.NoActiveDrawingException;
import br.org.archimedes.interfaces.Parser;
import br.org.archimedes.model.Element;
import br.org.archimedes.parser.SimpleSelectionParser;

/**
 * Belongs to package com.tarantulus.archimedes.factories.
 * 
 * NOTA: Factories que vao lidar com doubleclicks devem extender
 * de SelectorFactory. Afinal, faz sentido que o primeiro passo
 * de uma factory que lida com duplo clique seja uma seleção...
 * senão no quê você estaria dando um duplo clique??
 * 
 * @author night
 */
public abstract class SelectorFactory implements CommandFactory {

    private Parser parser;

    private boolean done;

    public SelectorFactory () {

        parser = null;
        done = true;
    }

    public String begin () {

        String returnValue = null;
        done = false;
        try {
            Set<Element> selection = Controller.getInstance().getCurrentSelectedElements();

            if (selection == null || selection.isEmpty()) {
                parser = new SimpleSelectionParser();
                returnValue = Messages.SelectorFactory_Select;
            }
            else {
                returnValue = next(selection);
            }
        }
        catch (NoActiveDrawingException e) {
            returnValue = cancel();
        }
        catch (InvalidParameterException e) {
            // Should not happen
            e.printStackTrace();
        }

        return returnValue;
    }

    @SuppressWarnings("unchecked") //$NON-NLS-1$
    public String next (Object parameter) throws InvalidParameterException {

        String message;
        if ( !isDone() && parameter != null) {
            Set<Element> selection;
            try {
                selection = (Set<Element>) parameter;
                message = finishFactory(selection);
                parser = null;
                done = true;
            }
            catch (Exception e) {
                selection = null;
                throw new InvalidParameterException();
            }
        }
        else {
            throw new InvalidParameterException();
        }
        return message;
    }

    /**
     * @param selection
     *            The selection used by the factory.
     * @return A nice message to the user.
     */
    protected abstract String finishFactory (Set<Element> selection)
            throws InvalidParameterException;

    public boolean isDone () {

        return done;
    }

    public String cancel () {

        parser = null;
        done = true;
        return getCancelMessage();
    }

    /**
     * @return The cancel message.
     */
    protected abstract String getCancelMessage ();

    public void drawVisualHelper () {

        // No visual helper
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.tarantulus.archimedes.commands.Command#getNextParser()
     */
    public Parser getNextParser () {

        return parser;
    }
}
