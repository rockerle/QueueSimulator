package net.rockerle.simulation.commonSystem.events;

import net.rockerle.simulation.commonSystem.Clock;
import net.rockerle.simulation.commonSystem.services.Service;
import net.rockerle.simulation.commonSystem.stats.InputStatistics;

public abstract class Event{

    double actionTime;
    public static Clock clock;
    public static Service service;
    public static EventQueue queue;
    public static InputStatistics genA, genB;


    public Event(double time, Clock c, Service s){
        this.actionTime=time;
        this.clock=c;
        this.service=s;
    }
    public Event(double time){
        this.actionTime=time;
    }
    public double getActionTime() {
        return actionTime;
    }
    public void action(){
        clock.setTime(actionTime);
    }
    abstract public EventType type();

    @Override
    public String toString(){
        return type() + ": "+actionTime;
    }
}