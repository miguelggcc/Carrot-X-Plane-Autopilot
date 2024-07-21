/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autopilot;

import xplane.XplaneInputOutput;

/**
 *
 * @author angel
 */
public class Rollout implements PhaseController {

    private ActionXplane actions;
    private ControlP rudder_control;

    public Rollout() {
        actions = new ActionXplane();
        rudder_control = new ControlP(0.03f, -1, 1);

    }

    @Override
    public ActionXplane computeAction(DataXplane data) {

        final float DTK = 119; // references
        final float TAS_MAX = 70; // termination conditions
        float tas, track; // inputs
        float rudder; // outputs
        float track_error; // errors

        // READ DATA----------------------------------------------------------------
        tas = data.getTas();
        track = data.getHpath();

        // LATERAL GUIDANCE---------------------------------------------------------
        // CONTROL LOOP
        track_error = DTK - track;
        rudder = rudder_control.control(track_error);

        // SET ACTIONS--------------------------------------------------------------   
        actions.setAilerons(0); 
        actions.setElevators(0);
        actions.setRudder(rudder);
        actions.setThrottle(1);
        actions.setBrake(0);
        actions.setFlaps(0.0f);

        // SET NEXT FLIGHT PHASE----------------------------------------------------
        if (tas > TAS_MAX) {
            actions.setPhase(Autopilot.ROTATE);
        } else {
            actions.setPhase(Autopilot.ROLLOUT);
        }

        return actions;
    }

}
