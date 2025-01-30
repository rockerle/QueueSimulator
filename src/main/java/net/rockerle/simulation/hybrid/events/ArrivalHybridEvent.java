package net.rockerle.simulation.hybrid.events;

import net.rockerle.simulation.commonSystem.Clock;
import net.rockerle.simulation.commonSystem.events.Event;
import net.rockerle.simulation.commonSystem.events.EventType;
import net.rockerle.simulation.commonSystem.stats.AbstractStatistics;
import net.rockerle.simulation.commonSystem.stats.InputStatistics;
import net.rockerle.simulation.waiting.WaitingService;
import java.util.List;

public class ArrivalHybridEvent extends Event {

    protected static AbstractStatistics p_blocked;
    protected static AbstractStatistics n_BE;
    protected static AbstractStatistics t_BE;
    protected static AbstractStatistics n_Queue;
    protected static AbstractStatistics t_Queue;
    protected static AbstractStatistics t_OnlyQueue;
    protected static AbstractStatistics n;
    protected static AbstractStatistics t;

    private static List<ArrivalHybridEvent> waitingQueue;
    private static WaitingService service;

    private double momentOfQueueing = 0.0;
    private static int queueLimit;

    public ArrivalHybridEvent(int limit, double time, Clock c, WaitingService s, List wQ, HybridEventQueue eQ,
                                InputStatistics arrGen, InputStatistics durGen, AbstractStatistics[] oStats) {
        super(time, c, s);
        this.queueLimit = limit;

        this.genA = arrGen;
        this.genB = durGen;

        this.waitingQueue = wQ;
        this.queue = eQ;
        this.service = s;

        this.n = oStats[0];
        this.n_BE = oStats[1];
        this.n_Queue = oStats[2];
        this.p_blocked = oStats[3];
        this.t = oStats[4];
        this.t_BE = oStats[5];
        this.t_Queue = oStats[6];
        this.t_OnlyQueue = oStats[7];
    }

    public ArrivalHybridEvent(double time) {
        super(time);
    }

    public void action(){
        super.action();

        n.update(this.service.getAmountBusy()+this.waitingQueue.size());
        n_BE.update(this.service.getAmountBusy());
        n_Queue.update(this.waitingQueue.size());

        int numStat = service.reserve();
        double newBorn = genA.draw() + clock.getTime();
        if(numStat==-1){
            if(this.waitingQueue.size()<queueLimit) {
                ArrivalHybridEvent newEvent = new ArrivalHybridEvent(newBorn);
                newEvent.setQueueingMoment(clock.getTime());
                addToWaitingQueue(newEvent);
                this.p_blocked.update(0.0);
            }else{
                this.p_blocked.update(1.0);
            }
         }else{
            this.p_blocked.update(0.0);
            double newFinished = genB.draw() + clock.getTime();
            this.t_BE.update(newFinished-clock.getTime());
            this.t.update(newFinished-this.getActionTime());
            this.t_Queue.update(0.0);
            queue.addEvent(new DurationHybridEvent(newFinished,numStat));
        }
        this.queue.addEvent(new ArrivalHybridEvent(newBorn));
    }

    public void setQueueingMoment(double time){
        this.momentOfQueueing=time;
    }
    public double getMomentOfQueueing(){
        return this.momentOfQueueing;
    }

    @Override
    public EventType type() {
        return EventType.ARRIVAL;
    }

    public void addToWaitingQueue(ArrivalHybridEvent ahe){
        for(int i=0;i<this.waitingQueue.size();i++){
            if(i>=this.queueLimit)
                p_blocked.update(1.0);
            else
                p_blocked.update(0.0);
            if(ahe.getActionTime()>=this.waitingQueue.get(i).getActionTime()){
                this.waitingQueue.add(i,ahe);
                return;
            }
        }
        this.waitingQueue.add(ahe);
    }
}