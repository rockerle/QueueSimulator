package net.rockerle.simulation.loss.events;

import net.rockerle.simulation.commonSystem.Clock;
import net.rockerle.simulation.commonSystem.events.Event;
import net.rockerle.simulation.commonSystem.events.EventType;
import net.rockerle.simulation.commonSystem.services.Service;

public class DurationLossEvent extends Event {

    private int position;

    public DurationLossEvent(double time, Clock c, Service s, int pos){
        super(time,c,s);
        this.position = pos;
    }
    public DurationLossEvent(double time, int pos){
        super(time);
        this.position = pos;
    }

    public void action(){
        super.action();
        service.free(this.position);
    }

    @Override
    public EventType type() {
        return EventType.DURATION;
    }
}