package net.rockerle.simulation.hybrid;

import java.util.ArrayList;
import net.rockerle.simulation.commonSystem.Clock;
import net.rockerle.simulation.commonSystem.stats.AbstractStatistics;
import net.rockerle.simulation.hybrid.events.ArrivalHybridEvent;
import net.rockerle.simulation.hybrid.events.DurationHybridEvent;
import net.rockerle.simulation.hybrid.events.HybridEventQueue;
import net.rockerle.simulation.commonSystem.stats.InputStatistics;
import net.rockerle.simulation.waiting.WaitingService;
import net.rockerle.simulation.waiting.events.ArrivalWaitEvent;

import java.util.List;

public class HybridSystem {

    private InputStatistics arrivalGenerator;
    private InputStatistics serviceGenerator;

    private AbstractStatistics n;
    private AbstractStatistics n_BE;
    private AbstractStatistics n_Queue;
    private AbstractStatistics blockingRate;

    private AbstractStatistics t;
    private AbstractStatistics t_BE;
    private AbstractStatistics t_Queue;
    private AbstractStatistics t_OnlyQueue;

    private List<ArrivalWaitEvent> waitingQueue;
    private HybridEventQueue eventQueue;
    private WaitingService service;
    private Clock clock;
    private int accuracy;
    private int queueLength;

    public HybridSystem(int queueSize, int workStations, double meanOperating, double meanArrival, int accuracy, AbstractStatistics[] outStats){

        this.arrivalGenerator = new InputStatistics(meanArrival);
        this.serviceGenerator = new InputStatistics(meanOperating);
        this.service = new WaitingService(workStations);
        this.eventQueue = new HybridEventQueue();
        this.waitingQueue = new ArrayList<>();

        this.clock = new Clock();
        this.accuracy = accuracy;
        this.queueLength = queueSize;

        this.n = outStats[0];
        this.n_BE = outStats[1];
        this.n_Queue = outStats[2];
        this.blockingRate = outStats[3];
        this.t = outStats[4];
        this.t_BE = outStats[5];
        this.t_Queue = outStats[6];
        this.t_OnlyQueue = outStats[7];
    }

    public Void run(){
        AbstractStatistics[] outStats = {this.n, this.n_BE, this.n_Queue, this.blockingRate, this.t, this.t_BE, this.t_Queue, this.t_OnlyQueue};
        new DurationHybridEvent(0.0, clock, service, waitingQueue, eventQueue, this.serviceGenerator, this.arrivalGenerator, outStats);
        this.eventQueue.addEvent(
            new ArrivalHybridEvent(
                this.queueLength, this.arrivalGenerator.draw(), clock, this.service, this.waitingQueue,
                this.eventQueue, arrivalGenerator, serviceGenerator, outStats));
        int rounds;
        boolean stop;
        do{
            for(rounds=0;rounds<10000;rounds++){
                this.eventQueue.nextEvent().action();
            }
            stop = this.n.stop(accuracy) && this.n_BE.stop(accuracy) && this.n_Queue.stop(accuracy) && this.blockingRate.stop(accuracy)
            && this.t.stop(accuracy) && this.t_BE.stop(accuracy) && this.t_Queue.stop(accuracy) && this.t_OnlyQueue.stop(accuracy);
        }while(!stop);
        System.out.println("Accuracy was hit");
        return null;
    }
}