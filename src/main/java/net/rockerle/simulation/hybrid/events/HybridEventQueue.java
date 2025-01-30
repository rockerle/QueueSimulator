package net.rockerle.simulation.hybrid.events;

import net.rockerle.simulation.commonSystem.events.Event;
import net.rockerle.simulation.commonSystem.events.EventQueue;

public class HybridEventQueue extends EventQueue {
  public HybridEventQueue(){super();}

  public Event nextEvent(){
    Event next = super.nextEvent();
    return next;
  }
  public void addEvent(Event e){super.addEvent(e);}
}