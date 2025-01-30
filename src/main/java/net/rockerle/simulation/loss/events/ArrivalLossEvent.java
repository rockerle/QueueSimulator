package net.rockerle.simulation.loss.events;

import net.rockerle.simulation.commonSystem.Clock;
import net.rockerle.simulation.commonSystem.events.Event;
import net.rockerle.simulation.commonSystem.events.EventQueue;
import net.rockerle.simulation.commonSystem.events.EventType;
import net.rockerle.simulation.commonSystem.services.Service;
import net.rockerle.simulation.commonSystem.stats.AbstractStatistics;
import net.rockerle.simulation.commonSystem.stats.InputStatistics;

public class ArrivalLossEvent extends Event {

    private static AbstractStatistics blockStats;
    private static AbstractStatistics membersStats;

    public ArrivalLossEvent(double time,
                            Clock c,
                            EventQueue queue,
                            InputStatistics a,
                            InputStatistics b,
                            AbstractStatistics out,
                            AbstractStatistics membersStats,
                            Service s){
        super(time,c,s);
        this.queue = queue;
        this.genA = a;
        this.genB = b;
        this.blockStats = out;
        this.membersStats = membersStats;
    }
    public ArrivalLossEvent(double time){
        super(time);
    }

    public void action(){
        super.action();
        this.membersStats.update(this.service.getAmountBusy());
        int numStat = service.reserve();
        if(numStat==-1){
            blockStats.update(1.0);
        }else{
            blockStats.update(0.0);
            double newFinished = genB.draw() + clock.getTime();
            queue.addEvent(new DurationLossEvent(newFinished,numStat));
        }
        double newBorn = genA.draw() + clock.getTime();
        queue.addEvent(new ArrivalLossEvent(newBorn));
    }
    @Override
    public EventType type() {
        return EventType.ARRIVAL;
    }
}