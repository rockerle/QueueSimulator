package net.rockerle.simulation.commonSystem;

public class Clock {

    private double currentTime;

    public Clock(){this.currentTime=0.0;}

    public double getTime(){
        return currentTime;
    }
    public void setTime(double time){
        this.currentTime = time;
    }
}