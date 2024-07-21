/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autopilot;

import xplane.XplaneInputOutput;
import gui.ControlPanel;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.WayPoint;

/**
 *
 * @author jvila
 */
public class Autopilot extends Thread {

    public static final int ROLLOUT = 1;
    public static final int ROTATE = 2;
    public static final int CLIMB = 3;
    public static final int CRUISE = 4;

    private int i = 0;

    XplaneInputOutput xplane;
    ControlPanel mygui;
    private boolean running, paused;
    private int sampleRate;

    private WayPoint[] wp;

    private DataXplane data;
    private ActionXplane action;

    private PhaseController pController;
    private Rollout rollOut;
    private Rotate rotate;
    private Climb climb;
    private Cruise cruise;

    public Autopilot(ControlPanel mygui, WayPoint[] wp) {

        this.xplane = new XplaneInputOutput();
        this.mygui = mygui;
        this.wp = wp;

        rollOut = new Rollout(); // inicialize phases
        rotate = new Rotate();
        climb = new Climb(wp, i);
        cruise = new Cruise(wp, i);

        //running = true;
        //this.start();
        paused = false;
        sampleRate = 200;
    }

    @Override
    public void run() {

        pController = rollOut; // initial phase
        int count = 1;
        //rollOut;
        while (running) { // control loop

            data = xplane.read();
            mygui.display(data);
            mygui.paint(data);
            if (!paused) {
                action = pController.computeAction(data);
                xplane.write(action);
                i = action.getWP();
            }
            mygui.display(action);
            
            if (running && !paused) {
                mygui.setInfoText("Volando de "
                        + wp[i].getLocationName() + " a "
                        + wp[i + 1].getLocationName());
            }
            
            if (paused){
                mygui.setInfoText("Autopiloto pausado");
            }
            

            switch (action.getPhase()) {
                case ROLLOUT:
                    pController = rollOut;
                    break;
                case ROTATE:
                    pController = rotate;
                    break;
                case CLIMB:
                    pController = climb;
                    break;
                case CRUISE:
                    pController = cruise;
                    break;
            }

            if (count == 10) { //Se guarda un punto cada 2 seg
                count = 1;
                mygui.addPoint(data.getLat(), data.getLon(), data.getAlt()); // Se añade el punto
            }
            
            count++;

            try {
                Thread.sleep(sampleRate);
            } catch (InterruptedException ex) {
                Logger.getLogger(ControlPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        xplane.stop();
    }

    public void stopMe() {
        paused = true;
    }

    public void resumeMe() {
        paused = false;
    }
    
    public void startMe() {
        running = true;
        this.start();
    }

}
