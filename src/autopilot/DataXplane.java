/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autopilot;

/**
 *
 * @author my.oñ
 */
public class DataXplane {

    private float lat;
    private float lon;
    private float alt;
    private float roll;
    private float pitch;
    private float head;

    private float aoa;
    private float beta;
    private float vpath;
    private float hpath;

    private float ias;
    private float tas;
    private float gs;
    private float vs;

    private float brake;

    public void setLat(float lat) {
        this.lat = lat;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    public void setAlt(float alt) {
        this.alt = alt;
    }

    public void setRoll(float roll) {
        this.roll = roll;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setHead(float head) {
        this.head = head;
    }

    public void setAoa(float aoa) {
        this.aoa = aoa;
    }

    public void setBeta(float beta) {
        this.beta = beta;
    }

    public void setVpath(float vpath) {
        this.vpath = vpath;
    }

    public void setHpath(float hpath) {
        this.hpath = hpath;
    }

    public void setIas(float ias) {
        this.ias = ias;
    }

    public void setTas(float tas) {
        this.tas = tas;
    }

    public void setGs(float gs) {
        this.gs = gs;
    }

    public void setVs(float vs) {
        this.vs = vs;
    }

    public void setBrake(float brake) {
        this.brake = brake;
    }

    public float getLat() {
        return lat;
    }

    public float getLon() {
        return lon;
    }

    public float getAlt() {
        return alt;
    }

    public float getRoll() {
        return roll;
    }

    public float getPitch() {
        return pitch;
    }

    public float getHead() {
        return head;
    }

    public float getAoa() {
        return aoa;
    }

    public float getBeta() {
        return beta;
    }

    public float getVpath() {
        return vpath;
    }

    public float getHpath() {
        return hpath;
    }

    public float getIas() {
        return ias;
    }

    public float getTas() {
        return tas;
    }

    public float getGs() {
        return gs;
    }

    public float getVs() {
        return vs;
    }

    public float getBrake() {
        return brake;
    }
}
