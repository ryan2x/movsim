/**
 * Copyright (C) 2010, 2011 by Arne Kesting, Martin Treiber, Ralph Germ, Martin Budden
 *                             <movsim.org@gmail.com>
 * ---------------------------------------------------------------------------------------------------------------------
 * 
 *  This file is part of 
 *  
 *  MovSim - the multi-model open-source vehicular-traffic simulator 
 *
 *  MovSim is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 *  version.
 *
 *  MovSim is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with MovSim.
 *  If not, see <http://www.gnu.org/licenses/> or <http://www.movsim.org>.
 *  
 * ---------------------------------------------------------------------------------------------------------------------
 */
package org.movsim.input.model.output;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FloatingCarInput {

    /** The Constant logger. */
    final static Logger logger = LoggerFactory.getLogger(FloatingCarInput.class);

    /** The n dt. */
    private int nDt;

    /** The dn. */
    private int dn;

    /** The floating cars. */
    private Collection<Integer> floatingCars;

    /** The is with fc. */
    private final boolean isWithFC;

    /**
     * Instantiates a new floating car input impl.
     * 
     * @param elem
     *            the elem
     */
    public FloatingCarInput(Element elem) {

        if (elem == null) {
            isWithFC = false; // not initialized
            return;
        }

        this.nDt = Integer.parseInt(elem.getAttributeValue("n_dt"));

        this.dn = Integer.parseInt(elem.getAttributeValue("dn"));
        if (dn != 0) {
            logger.error("dn = {} not yet implemented. exit.", dn);
            System.exit(-1);
        }

        floatingCars = new HashSet<Integer>();
        @SuppressWarnings("unchecked")
        final List<Element> fcElems = elem.getChildren("FC");
        if (fcElems != null) {
            for (final Element fcElem : fcElems) {
                final int iFC = Integer.parseInt(fcElem.getAttributeValue("number"));
                floatingCars.add(Integer.valueOf(iFC));
            }
        }

        isWithFC = (dn != 0 || !floatingCars.isEmpty());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.movsim.input.model.output.impl.FloatingCarInput#getDn()
     */
    public int getDn() {
        return dn;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.movsim.input.model.output.impl.FloatingCarInput#getDt()
     */
    public int getNDt() {
        return nDt;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.movsim.input.model.output.impl.FloatingCarInput#isWithFCD()
     */
    public boolean isWithFCD() {
        return isWithFC;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.movsim.input.model.output.FloatingCarInput#getFloatingCars()
     */
    public Collection<Integer> getFloatingCars() {
        return floatingCars;
    }
}
