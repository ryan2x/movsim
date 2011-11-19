/**
 * Copyright (C) 2010, 2011 by Arne Kesting, Martin Treiber, Ralph Germ, Martin Budden <info@movsim.org>
 * ---------------------------------------------------------------------- This file is part of MovSim - the multi-model
 * open-source vehicular-traffic simulator MovSim is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version. MovSim is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with MovSim. If not, see
 * <http://www.gnu.org/licenses/> or <http://www.movsim.org>.
 * ----------------------------------------------------------------------
 */
package org.movsim.simulator.vehicles.longmodel.accelerationmodels;

import org.movsim.input.model.vehicle.longModel.AccelerationModelInputDataIDM;
import org.movsim.simulator.roadsegment.LaneSegment;
import org.movsim.simulator.vehicles.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class IDM.
 * <p>
 * Implementation of the 'intelligent driver model'(IDM). <a
 * href="http://en.wikipedia.org/wiki/Intelligent_Driver_Model">Wikipedia
 * article IDM.</a>
 * </p>
 * <p>
 * Treiber/Kesting: Verkehrsdynamik und -simulation, 2010, chapter 11.3
 * </p>
 * <p>
 * see <a href="http://xxx.uni-augsburg.de/abs/cond-mat/0002177"> M. Treiber, A.
 * Hennecke, and D. Helbing, Congested Traffic States in Empirical Observations
 * and Microscopic Simulations, Phys. Rev. E 62, 1805 (2000)].</a>
 * </p>
 */
public class IDM extends AccelerationModelAbstract implements AccelerationModel {

    /** The Constant logger. */
    final static Logger logger = LoggerFactory.getLogger(IDM.class);

    /** desired velocity (m/s). */
    private double v0;

    /** safe time headway (s). */
    private double T;

    /** bumper-to-bumper vehicle distance in jams or queues; minimun gap. */
    private double s0;

    /** gap parameter (m). */
    private double s1;

    /** acceleration (m/s^2). */
    private double a;

    /** comfortable (desired) deceleration (braking), (m/s^2). */
    private double b;

    /** acceleration exponent. */
    private double delta;

    /**
     * Instantiates a new IDM.
     * 
     * @param modelName
     *            the model name
     * @param parameters
     *            the parameters: v0, T, s0, s1, a, b, delta
     */
    public IDM(AccelerationModelInputDataIDM parameters) {
        super(ModelName.IDM, parameters);
        initParameters();
    }

    /**
     * Constructor.
     * 
     * @param v0
     *            desired velocity, m/s
     * @param a
     *            maximum acceleration, m/s^2
     * @param b
     *            desired deceleration (comfortable braking), m/s^2
     * @param T
     *            safe time headway, seconds
     * @param s0
     *            bumper to bumper vehicle distance in stationary traffic, meters
     * @param s1
     *            gap parameter, meters
     */
    public IDM(double v0, double a, double b, double T, double s0, double s1) {
        super(ModelName.IDM, null);
        //super(Type.CONTINUOUS);
        this.v0 = v0;
        this.a = a;
        this.b = b;
        this.T = T;
        this.s0 = s0;
        this.s1 = s1;
        //twoSqrtAb = 2.0 * Math.sqrt(a * b);
        this.delta = 4.0;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.movsim.simulator.vehicles.longmodel.accelerationmodels.impl.
     * LongitudinalModel#initParameters()
     */
    @Override
    protected void initParameters() {
        logger.debug("init model parameters");
        this.v0 = ((AccelerationModelInputDataIDM) parameters).getV0();
        this.T = ((AccelerationModelInputDataIDM) parameters).getT();
        this.s0 = ((AccelerationModelInputDataIDM) parameters).getS0();
        this.s1 = ((AccelerationModelInputDataIDM) parameters).getS1();
        this.a = ((AccelerationModelInputDataIDM) parameters).getA();
        this.b = ((AccelerationModelInputDataIDM) parameters).getB();
        this.delta = ((AccelerationModelInputDataIDM) parameters).getDelta();
    }

    /**
     * Gets the v0.
     * 
     * @return the v0
     */
    public double getV0() {
        return v0;
    }

    /**
     * Gets the t.
     * 
     * @return the t
     */
    public double getT() {
        return T;
    }

    /**
     * Gets the s0.
     * 
     * @return the s0
     */
    public double getS0() {
        return s0;
    }

    /**
     * Gets the s1.
     * 
     * @return the s1
     */
    public double getS1() {
        return s1;
    }

    /**
     * Gets the delta.
     * 
     * @return the delta
     */
    public double getDelta() {
        return delta;
    }

    /**
     * Gets the a.
     * 
     * @return the a
     */
    public double getA() {
        return a;
    }

    /**
     * Gets the b.
     * 
     * @return the b
     */
    public double getB() {
        return b;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.movsim.simulator.vehicles.longmodel.accelerationmodels.AccelerationModel
     * #acc(org.movsim.simulator.vehicles.Vehicle,
     * org.movsim.simulator.vehicles.VehicleContainer, double, double, double)
     */
    @Override
    public double calcAcc(Vehicle me, LaneSegment vehContainer, double alphaT, double alphaV0, double alphaA) {

        // Local dynamical variables
        final Vehicle vehFront = vehContainer.frontVehicle(me);
        final double s = me.getNetDistance(vehFront);
        final double v = me.getSpeed();
        final double dv = me.getRelSpeed(vehFront);

        // space dependencies modeled by speedlimits, alpha's

        final double localT = alphaT * T;
        // consider external speedlimit
        final double localV0 = Math.min(alphaV0 * v0, me.getSpeedlimit());
        final double localA = alphaA * a;

        return acc(s, v, dv, localT, localV0, localA);

    }
    
    
    
    @Override
    public double calcAcc(final Vehicle me, final Vehicle vehFront){
        // Local dynamical variables
        final double s = me.getNetDistance(vehFront);
        final double v = me.getSpeed();
        final double dv = me.getRelSpeed(vehFront);
        
        final double localT = T;
        final double localV0 = Math.min(v0, me.getSpeedlimit());
        final double localA = a;

        return acc(s, v, dv, localT, localV0, localA);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.movsim.simulator.vehicles.longmodel.accelerationmodels.AccelerationModel
     * #accSimple(double, double, double)
     */
    @Override
    public double calcAccSimple(double s, double v, double dv) {
        return acc(s, v, dv, T, v0, a);
    }

    /**
     * Acc.
     * 
     * @param s
     *            the s
     * @param v
     *            the v
     * @param dv
     *            the dv
     * @param TLocal
     *            the t local
     * @param v0Local
     *            the v0 local
     * @param aLocal
     *            the a local
     * @return the double
     */
    private double acc(double s, double v, double dv, double TLocal, double v0Local, double aLocal) {
        // treat special case of v0=0 (standing obstacle)
        if (v0Local == 0) {
            return 0;
        }

        double sstar = s0 + TLocal * v + s1 * Math.sqrt((v + 0.0001) / v0Local) + (0.5 * v * dv)
                / Math.sqrt(aLocal * b);

        if (sstar < s0) {
            sstar = s0;
        }

        final double aWanted = aLocal * (1. - Math.pow((v / v0Local), delta) - (sstar / s) * (sstar / s));

        logger.debug("aWanted = {}", aWanted);
        return aWanted; // limit to -bMax in Vehicle
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.movsim.simulator.vehicles.longmodel.accelerationmodels.impl.
     * LongitudinalModel#parameterV0()
     */
    @Override
    public double getDesiredSpeedParameterV0() {
        return v0;
    }

   

    /* (non-Javadoc)
     * @see org.movsim.simulator.vehicles.longmodel.accelerationmodels.impl.AccelerationModelAbstract#setDesiredSpeedV0(double)
     */
    @Override
    protected void setDesiredSpeedV0(double v0) {
        this.v0 = v0;
    }
}
