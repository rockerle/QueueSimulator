package net.rockerle.simulation.hybrid.events;

import java.util.List;
import net.rockerle.simulation.commonSystem.Clock;
import net.rockerle.simulation.commonSystem.events.Event;
import net.rockerle.simulation.commonSystem.events.EventQueue;
import net.rockerle.simulation.commonSystem.events.EventType;
import net.rockerle.simulation.commonSystem.services.Service;
import net.rockerle.simulation.commonSystem.stats.AbstractStatistics;
import net.rockerle.simulation.commonSystem.stats.InputStatistics;

public class DurationHybridEvent extends Event {

  protected static InputStatistics arrivalGen;
  protected static InputStatistics serviceGen;

  protected static AbstractStatistics n_BE;
  protected static AbstractStatistics t_BE;
  protected static AbstractStatistics n_Queue;
  protected static AbstractStatistics t_Queue;
  protected static AbstractStatistics t_OnlyQueue;
  protected static AbstractStatistics n;
  protected static AbstractStatistics t;
  protected static AbstractStatistics p_blocked;

  private static EventQueue eventQueue;
  private static List<ArrivalHybridEvent> waitingQueue;
  private static Service service;

  private double momentOfQueueing = 0.0;
  private int position;

  public DurationHybridEvent(double time, Clock c, Service s, List wQ, EventQueue eQ, InputStatistics genDuration, InputStatistics genArr, AbstractStatistics[] oStats) {
    super(time, c, s);
    this.eventQueue = eQ;
    this.waitingQueue = wQ;
    this.service = s;
    this.arrivalGen = genArr;
    this.serviceGen = genDuration;

    this.n = oStats[0];
    this.n_BE = oStats[1];
    this.n_Queue = oStats[2];
    this.p_blocked = oStats[3];
    this.t = oStats[4];
    this.t_BE = oStats[5];
    this.t_Queue = oStats[6];
    this.t_OnlyQueue = oStats[7];
  }

  public DurationHybridEvent(double time, int pos) {
    super(time);
    this.position = pos;
  }

  public void action(){
    super.action();
    this.service.free(this.position);
    if(waitingQueue.size() > 0){
      ArrivalHybridEvent event = waitingQueue.remove(0);
      t_OnlyQueue.update(clock.getTime()-event.getMomentOfQueueing());
      int pos = this.service.reserve();
      double dur = serviceGen.draw();
      t_Queue.update(clock.getTime()-event.getMomentOfQueueing());
      t.update(clock.getTime()-event.getMomentOfQueueing()+dur);
      this.eventQueue.addEvent(new DurationHybridEvent(this.clock.getTime()+dur, pos));
    }
    this.n.update(this.service.getAmountBusy()+this.waitingQueue.size());
    this.n_Queue.update(this.waitingQueue.size());
  }

  @Override
  public EventType type() {
    return EventType.DURATION;
  }
}