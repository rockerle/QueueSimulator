package net.rockerle.simulation.waiting.events;

import net.rockerle.simulation.commonSystem.Clock;
import net.rockerle.simulation.commonSystem.events.Event;
import net.rockerle.simulation.commonSystem.events.EventQueue;
import net.rockerle.simulation.commonSystem.events.EventType;
import net.rockerle.simulation.commonSystem.stats.AbstractStatistics;
import net.rockerle.simulation.waiting.WaitingService;
import net.rockerle.simulation.commonSystem.stats.InputStatistics;

import java.util.List;

public class ArrivalWaitEvent extends Event {

    protected static AbstractStatistics n_BE;
    protected static AbstractStatistics t_BE;
    protected static AbstractStatistics n_Queue;
    protected static AbstractStatistics t_Queue;
    protected static AbstractStatistics t_OnlyQueue;
    protected static AbstractStatistics n;
    protected static AbstractStatistics t;
    protected static AbstractStatistics waitingProb;

    private static List<ArrivalWaitEvent> waitingQueue;
    private static WaitingService service;

    private double momentOfQueueing = 0.0;

    public ArrivalWaitEvent(double time,
                            Clock c,
                            EventQueue queue,
                            List<ArrivalWaitEvent> waitingQueue,
                            InputStatistics a,
                            InputStatistics b,
                            AbstractStatistics n_BE,
                            AbstractStatistics t_BE,
                            AbstractStatistics n_Queue,
                            AbstractStatistics t_Queue,
                            AbstractStatistics t_OnlyQueue,
                            AbstractStatistics n,
                            AbstractStatistics t,
                            AbstractStatistics waitingProb,
                            WaitingService s){
        super(time,c,s);
        this.queue = queue;
        this.genA = a;
        this.genB = b;
        this.waitingQueue = waitingQueue;
        this.n_BE = n_BE;                   // average number of busy services
        this.t_BE = t_BE;                   // average service time
        this.n_Queue = n_Queue;             // average number of requests in queue
        this.t_Queue = t_Queue;             // average duration in waiting queue (all requests)
        this.t_OnlyQueue = t_OnlyQueue;     // average time in waiting queue (only requests that end up in queue)
        this.n = n;                         // average number of requests in system
        this.t = t;                         // average duration in system
        this.waitingProb = waitingProb;     // probability of getting into the waiting queue
        this.service = s;
    }

    public ArrivalWaitEvent(double time) {
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
            ArrivalWaitEvent newEvent = new ArrivalWaitEvent(newBorn);
            newEvent.setQueueingMoment(clock.getTime());
            addToWaitingQueue(newEvent);
            this.waitingProb.update(1.0);
        }else{
            this.waitingProb.update(0.0);
            double newFinished = genB.draw();
            this.t_BE.update(newFinished);
            this.t.update(newFinished);
            this.t_Queue.update(0.0);
            queue.addEvent(new DurationWaitEvent(newFinished + clock.getTime(), numStat));
        }
        ArrivalWaitEvent newEvent = new ArrivalWaitEvent(clock.getTime()+ genA.draw());
        queue.addEvent(newEvent);
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

    public void addToWaitingQueue(ArrivalWaitEvent awe){
        for(int i=0;i<this.waitingQueue.size();i++){
            if(awe.getActionTime()>=this.waitingQueue.get(i).getActionTime()){
                this.waitingQueue.add(i,awe);
                return;
            }
        }
        this.waitingQueue.add(awe);

    }
}