package net.rockerle.simulation.waiting.events;

import net.rockerle.simulation.commonSystem.Clock;
import net.rockerle.simulation.commonSystem.events.Event;
import net.rockerle.simulation.commonSystem.events.EventQueue;
import net.rockerle.simulation.commonSystem.events.EventType;
import net.rockerle.simulation.commonSystem.services.Service;
import net.rockerle.simulation.commonSystem.stats.AbstractStatistics;
import net.rockerle.simulation.commonSystem.stats.InputStatistics;

import java.util.List;

public class DurationWaitEvent extends Event {

    private int position;
    private static InputStatistics genDuration;
    private static InputStatistics genArr;
    private static EventQueue eventQueue;
    private static List<ArrivalWaitEvent> waitingQueue;
    private static AbstractStatistics t_queueOnly;
    private static AbstractStatistics t_queue;
    private static AbstractStatistics t_total;
    private static AbstractStatistics n;
    private static AbstractStatistics n_Queue;
    private static AbstractStatistics waitingProb;

    public DurationWaitEvent(double time, Clock c, Service s, List wQ, EventQueue eQ, InputStatistics genDuration, InputStatistics genArr, AbstractStatistics[] outStats) {
        super(time, c, s);
        this.genDuration = genDuration;
        this.genArr = genArr;
        this.eventQueue = eQ;
        this.waitingQueue = wQ;
        this.t_queueOnly = outStats[0];
        this.t_queue = outStats[1];
        this.t_total = outStats[2];
        this.n = outStats[3];
        this.n_Queue = outStats[4];
        this.waitingProb = outStats[5];
    }

    public DurationWaitEvent(double time, int pos) {
        super(time);
        this.position = pos;
    }

    public void action(){
        super.action();
        this.service.free(this.position);
        if(waitingQueue.size() > 0){
            ArrivalWaitEvent event = waitingQueue.remove(0);
            t_queueOnly.update(clock.getTime()-event.getMomentOfQueueing());
            int pos = this.service.reserve();
            double dur = genDuration.draw();
            t_queue.update(clock.getTime()-event.getMomentOfQueueing());
            t_total.update(clock.getTime()-event.getMomentOfQueueing()+dur);
            this.eventQueue.addEvent(new DurationWaitEvent(this.clock.getTime()+dur, pos));
        }

        this.n.update(this.service.getAmountBusy()+this.waitingQueue.size());
        this.n_Queue.update(this.waitingQueue.size());
    }

    @Override
    public EventType type() {
        return EventType.DURATION;
    }
}