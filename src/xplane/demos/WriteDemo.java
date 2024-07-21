/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xplane.demos;

import autopilot.ActionXplane;
import autopilot.DataXplane;
import xplane.XplaneInputOutput;

/**
 *
 * @author my.oñ
 */
public class WriteDemo {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
         XplaneInputOutput xplane;
         ActionXplane action;
         
         xplane = new XplaneInputOutput();
         action = new ActionXplane();
         action.setAilerons(0);
         action.setElevators(0.1f);
         action.setRudder(0.25f);
         action.setThrottle(1);
         action.setBrake(0);
         action.setFlaps(1);
         xplane.write(action);
         
         xplane.stop();
         
               
        
    }
    
}
