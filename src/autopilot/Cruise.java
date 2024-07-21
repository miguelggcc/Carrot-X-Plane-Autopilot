/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autopilot;

import utils.Coordinate;
import utils.WayPoint;
import xplane.XplaneInputOutput;

/**
 *
 * @author angel
 */
public class Cruise implements PhaseController {

    private ActionXplane actions;
    private ControlP lateral_control;
    private ControlP vertical_guidance;
    private ControlP vertical_control;
    
    private WayPoint[] wp;
    private int i;


    public Cruise(WayPoint[] wp, int i) {

        actions = new ActionXplane();
        lateral_control = new ControlP(0.005f, -1, 1);
        vertical_guidance = new ControlP(0.6f, -15, 15);
        vertical_control = new ControlP(0.02f, -1, 1);
        this.wp = wp;
        this.i = i;
    }

    private double saturate(double value, double limit) {
        if (value > limit) {
            value = limit;
        } else if (value < -limit) {
            value = -limit;
        }

        return value;
    }

    @Override
    public ActionXplane computeAction(DataXplane data) {
        
        double DTKseg = wp[i].bearing(wp[i + 1]);
        System.out.println("Track: " + DTKseg);
        double xi, lambda, d1, d2, L1val = 900, L1 = L1val;
        
        float DALT = 2000; // references
        float lat, lon, tas, gs, roll, track, altitude, pitch; // inputs
        float ailerons, elevators; // outputs
        float DROLL, DPITCH, roll_err, pitch_err, alt_err; // errors

        // READ DATA----------------------------------------------------------------
        lat = data.getLat();
        lon = data.getLon();
        roll = data.getRoll();
        tas = data.getTas();
        gs = data.getGs();
        altitude = data.getAlt();
        track = data.getHpath();
        pitch = data.getPitch();
        // LATERAL GUIDANCE---------------------------------------------------------
        // GUIDANCE LOOP

        Coordinate x = new Coordinate("pos x", lat, lon); //coord. aeronave

        xi = wp[i].bearing(x);
        lambda = Math.abs(Math.toRadians(DTKseg)) - Math.abs(Math.toRadians(xi));
        d1 = x.getRhumbLineDistance(wp[i]); // dist aeroanve a wp1
        d2 = x.getRhumbLineDistance(wp[i + 1]); // dist aeroanve a wp2

        if (Math.abs(lambda) < Math.PI / 2) {

            if (L1 < Math.abs(d1 * Math.sin(lambda))) { //Mirar que L_1 siempre sea mayor que XTE
                L1 = 3 * Math.abs(d1 * Math.sin(lambda));

            } else {
                L1 = L1val;
            }
        } else if (Math.abs(lambda) >= Math.PI / 2) { // Caso en el que la aeronave está por detrás del punto WP1 del tramo
            L1 = Math.max(L1, d1);
        }

        System.out.println("d1: " + d1 + " d2: " + d2 + " lambda: " + Math.toDegrees(lambda) + " xi: " + xi);

        double WP1Pdist = d1 * Math.cos(lambda)
                + Math.sqrt(L1 * L1 - Math.pow(d1 * Math.sin(lambda), 2));//dist WP1 a punto P

        double PE = wp[i].getXFlat() + WP1Pdist * Math.sin(Math.toRadians(DTKseg)); // Coord N punto P.
        double PN = wp[i].getYFlat() + WP1Pdist * Math.cos(Math.toRadians(DTKseg)); // Coord E punto P.

        double XE = x.getXFlat();
        double XN = x.getYFlat();

        double omega = Math.toRadians(x.bearing(PE, PN));

        if (track > 180) { //Debe estar entre -180 y 180º
            track = track - 360;
        }

        double eta = omega - (double) Math.toRadians(track);

        System.out.println("eta " + Math.toDegrees(eta) + " omega " + Math.toDegrees(omega) + " Wdist " + WP1Pdist);

        while (eta > Math.PI) { //Debe estar entre -180 y 180º
            eta = eta - 2 * Math.PI;
        }

        while (eta < -Math.PI) {
            eta = eta + 2 * Math.PI;
        }

        eta = saturate(eta, Math.PI / 2); //Se satura hasta 90º

        if (d2 < L1 || d1 * Math.cos(lambda) > wp[i].getRhumbLineDistance(wp[i + 1])) { // Ha llegado al final del segmento

            if (i < wp.length - 2) {
                i++;
                actions.setWP(i);
                System.out.println(wp[i].bearing(x) + " " + i);
                System.out.println("-------------------------------------------------------");
                System.out.println("Target: " + wp[i+1].getLocationName());
                System.out.println("-------------------------------------------------------");

            } else {
                System.out.println("END");
            }
        }

        // CONTROL LOOP
        double droll = (2*gs*0.5144*gs*0.5144*Math.sin(eta)/(L1*9.81)); //Desired roll antes de saturación
        System.out.println("droll: " +Math.toDegrees(droll));
        droll = saturate(Math.toDegrees(droll), 20); //Saturado hasta 20º
        DROLL = (float) droll;
        roll_err = DROLL-roll;
        ailerons = lateral_control.control(roll_err);

        // VERTICAL GUIDANCE -------------------------------------------------------
       alt_err = DALT-altitude;
       DPITCH = vertical_guidance.control(alt_err);
       pitch_err = DPITCH - pitch;
        elevators = vertical_control.control(pitch_err);

        // SET ACTIONS--------------------------------------------------------------
        actions.setAilerons(ailerons); 
        actions.setElevators(elevators);
        actions.setRudder(0);
        actions.setThrottle(.8f);
        actions.setBrake(0);
        actions.setFlaps(0.0f);

        // SET NEXT FLIGHT PHASE----------------------------------------------------

            actions.setPhase(Autopilot.CRUISE);
            actions.setWP(i);

  
        return actions;
    }

}
