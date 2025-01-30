package net.rockerle.simulation.loss;

import net.rockerle.simulation.commonSystem.Clock;
import net.rockerle.simulation.commonSystem.events.EventQueue;
import net.rockerle.simulation.commonSystem.stats.AbstractStatistics;
import net.rockerle.simulation.commonSystem.stats.InputStatistics;
import net.rockerle.simulation.loss.events.ArrivalLossEvent;
import net.rockerle.simulation.loss.events.DurationLossEvent;
import net.rockerle.simulation.commonSystem.services.Service;

public class LossSystem {

    private Service service;
    private InputStatistics inArrival;
    private InputStatistics inOperating;
    private AbstractStatistics out;
    private AbstractStatistics meanMembersOut;
    private EventQueue eventQueue;
    private Clock clock;
    private int accuracy;

    public LossSystem(int workStations, double meanOperatingTime, double meanArrivalTime, int accuracy, AbstractStatistics[] outStats){
        this.inOperating = new InputStatistics(meanOperatingTime);
        this.inArrival = new InputStatistics(meanArrivalTime);
        this.out = outStats[0];
        this.meanMembersOut = outStats[1];
        this.clock = new Clock();
        this.eventQueue = new EventQueue();
        this.accuracy = accuracy;
        this.service = new Service(workStations);
    }

    public Void run(){
        new DurationLossEvent(0.0, clock, service, 0);
        this.eventQueue.addEvent(new ArrivalLossEvent(inArrival.draw(), clock, this.eventQueue, inArrival, inOperating, out, this.meanMembersOut, this.service));
        int rounds;
        boolean stop;
        do{
            for(rounds=0;rounds<10000;rounds++){
                this.eventQueue.nextEvent().action();
            }
            stop = out.stop(this.accuracy) && meanMembersOut.stop(accuracy);
        }while(!stop);
        System.out.println("Accuracy was hit");
        return null;
    }
}