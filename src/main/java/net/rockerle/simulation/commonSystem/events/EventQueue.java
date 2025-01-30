package net.rockerle.simulation.commonSystem.events;

import java.util.LinkedList;
import java.util.List;
import net.rockerle.simulation.loss.events.ArrivalLossEvent;
import net.rockerle.simulation.loss.events.DurationLossEvent;

public class EventQueue {
    protected List<Event> queue;

    public EventQueue(){
        this.queue = new LinkedList<>();
    }
    public Event nextEvent(){
        return this.queue.remove(this.queue.size()-1);
    }
    public void addEvent(Event e){
        for(int i=0;i<this.queue.size();i++){
            if(e.getActionTime()>=queue.get(i).getActionTime()){
                this.queue.add(i,e);
                return;
            }
        }
        this.queue.add(e);
    }

    public int getArrivalEvents(){
        int result = 0;
        for(int i=0;i<this.queue.size();i++){
            if(this.queue.get(i) instanceof ArrivalLossEvent)
                result += 1;
        }
        return result;
    }
    public int getDurationEvents(){
        int result = 0;
        for(int i=0;i<this.queue.size();i++){
            if(this.queue.get(i) instanceof DurationLossEvent)
                result += 1;
        }
        return result;
    }
    public int getSize(){
        return this.queue.size();
    }
    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        for(Event e: this.queue){
            res.append(e.toString()+ java.lang.System.lineSeparator());
        }
        return res.toString();
    }
}