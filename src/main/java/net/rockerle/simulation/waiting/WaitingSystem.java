package net.rockerle.simulation.waiting;

import net.rockerle.simulation.commonSystem.Clock;
import net.rockerle.simulation.commonSystem.stats.*;
import net.rockerle.simulation.waiting.events.ArrivalWaitEvent;
import net.rockerle.simulation.waiting.events.DurationWaitEvent;
import net.rockerle.simulation.waiting.events.WaitingEventQueue;

import java.util.ArrayList;
import java.util.List;

public class WaitingSystem {

    private InputStatistics arrivalGenerator;
    private InputStatistics serviceGenerator;

    private AbstractStatistics n_BE;
    private AbstractStatistics t_BE;
    private AbstractStatistics n_Queue;
    private AbstractStatistics t_Queue;
    private AbstractStatistics t_OnlyQueue;
    private AbstractStatistics n;
    private AbstractStatistics t;
    private AbstractStatistics waitingProb;

    private List<ArrivalWaitEvent> waitingQueue;
    private WaitingEventQueue eventQueue;
    private WaitingService service;
    private Clock clock;
    private int accuracy;

    public WaitingSystem(int workStations, double meanOperating, double meanArrival, int accuracy, AbstractStatistics[] outStats){

        this.arrivalGenerator = new InputStatistics(meanArrival);
        this.serviceGenerator = new InputStatistics(meanOperating);

        this.accuracy = accuracy;
        this.waitingQueue = new ArrayList<>();
        this.clock = new Clock();

        this.n_BE = outStats[0];
        this.t_BE = outStats[1];
        this.n_Queue = outStats[2];
        this.t_Queue = outStats[3];
        this.t_OnlyQueue = outStats[4];
        this.n = outStats[5];
        this.t = outStats[6];
        this.waitingProb = outStats[7];

        this.eventQueue = new WaitingEventQueue();
        this.service = new WaitingService(workStations);
    }

    public Void run(){
        AbstractStatistics[] durationStats = {this.t_OnlyQueue, this.t_Queue, this.t, this.n, this.n_Queue, this.waitingProb};
        new DurationWaitEvent(0.0, clock,service, this.waitingQueue, this.eventQueue, this.serviceGenerator, this.arrivalGenerator, durationStats);
        this.eventQueue.addEvent(
                new ArrivalWaitEvent(arrivalGenerator.draw(),
                        clock,
                        this.eventQueue,
                        this.waitingQueue,
                        this.arrivalGenerator,
                        this.serviceGenerator,
                        this.n_BE,
                        this.t_BE,
                        this.n_Queue,
                        this.t_Queue,
                        this.t_OnlyQueue,
                        this.n,
                        this.t,
                        this.waitingProb,
                        this.service)
        );
        int rounds;
        boolean stop;
        do{
            for(rounds=0;rounds<10000;rounds++){
                this.eventQueue.nextEvent().action();
            }
            stop = waitingProb.stop(accuracy) && n.stop(accuracy) && n_BE.stop(accuracy) && n_Queue.stop(accuracy)
                && t.stop(accuracy) && t_BE.stop(accuracy) && t_Queue.stop(accuracy) && t_OnlyQueue.stop(accuracy);
        }while(!stop);
        System.out.println("Accuracy was hit");
        return null;
    }
}