package net.rockerle.simulation.waiting.events;

import net.rockerle.simulation.commonSystem.events.Event;
import net.rockerle.simulation.commonSystem.events.EventQueue;

public class WaitingEventQueue extends EventQueue {

    public WaitingEventQueue() {
        super();
    }

    public Event nextEvent(){
        Event next = super.nextEvent();
        return next;
    }

    public void addEvent(Event e){
        super.addEvent(e);
    }
}