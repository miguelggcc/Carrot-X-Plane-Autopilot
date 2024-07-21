/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xplane.demos;

import autopilot.DataXplane;
import xplane.XplaneInputOutput;

/**
 *
 * @author my.oñ
 */
public class ReadDemo {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
         XplaneInputOutput xplane;
         DataXplane data;
         
         xplane = new XplaneInputOutput();
         data = xplane.read();
         System.out.println("Latitude "+data.getLat()+" Longitude "+data.getLon()+" Altitude "+data.getAlt());
         
         xplane.stop();
        
       
    }
    
}
