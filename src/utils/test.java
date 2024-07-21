/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

/**
 *
 * @author fms
 */
public class test {

    /**
     * @param args the command line arguments
     */
 
    public static void main(String[] args) {
        Airport a1, a2;
 
        a1 = new Airport("Valencia", "LEVC", 39.4894, -0.4780);
        a2 = new Airport("Arica", "SCAR", -18.35, -70.3167);

      
        // compute distance and course 
        // compute route lenght 
        double geoDistance, geoBearing, rhumbDistance, rhumbBearing;
        String geoDistanceStr, geoBearingStr, rhumbDistanceStr, rhumbBearingStr;

        geoDistance = a1.getGreatCircleDistance(a2) / 1852;
        geoDistanceStr = String.format("%.2f", geoDistance);
        geoBearing = a1.getGreatCircleInitialBearing(a2);
        geoBearingStr = String.format("%.2f", geoBearing);

        System.out.println("GreatCircle distance from " + a1.getLocationName() + " to " + a2.getLocationName() + " is " + geoDistanceStr + " NM");
        System.out.println("Initial Bearing: " + geoBearingStr);
        rhumbDistance = a1.getRhumbLineDistance(a2) / 1852;
        rhumbDistanceStr = String.format("%.2f", rhumbDistance);
        rhumbBearing = a1.getRhumbLineBearing(a2);
        rhumbBearingStr = String.format("%.2f", rhumbBearing);
        rhumbBearingStr = String.format("%.2f", rhumbBearing);
        System.out.println("Rhumb line distance from " + a1.getLocationName() + " to " + a2.getLocationName() + " is " + rhumbDistanceStr + " NM");
        System.out.println("Bearing: " + rhumbBearingStr);

       
        System.out.println("END");

    }

}
