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
public class Route {

    private Airport departure;
    private Airport destination;
    private int numWaypoints;
    private WayPoint[] wp;

    public Route(Airport departure, Airport destination, WayPoint[] wp) {
        this.departure = departure;
        this.destination = destination;
        this.numWaypoints = wp.length;
        this.wp = wp;
    }

    public double greatCircleLenght() {
        int i;
        double distance;

        distance = departure.getGreatCircleDistance(wp[0]);
        for (i = 0; i < numWaypoints - 1; i++) {
            distance += wp[i].getGreatCircleDistance(wp[i + 1]);
        }
        distance = distance + wp[i].getGreatCircleDistance(destination);
        return distance;
    }

    public double rhumbLenght() {
        int i;
        double distance;

        distance = departure.getRhumbLineDistance(wp[0]);
        for (i = 0; i < numWaypoints - 1; i++) {
            distance += wp[i].getRhumbLineDistance(wp[i + 1]);
        }
        distance = distance + wp[i].getRhumbLineDistance(destination);
        return distance;

    }

    public int getNumWaypoints() {
        return numWaypoints;
    }

    public WayPoint[] getWp() {
        return wp;
    }

    public Airport getDeparture() {
        return departure;
    }

    public Airport getDestination() {
        return destination;
    }

}
