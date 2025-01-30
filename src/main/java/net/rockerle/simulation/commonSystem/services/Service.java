package net.rockerle.simulation.commonSystem.services;

import net.rockerle.simulation.commonSystem.events.Activity;

public class Service {

    protected Activity[] workStationStates;

    public Service(int workStations) {
        this.workStationStates = new Activity[workStations];
        for(int i = 0; i < workStations; i++)
            this.workStationStates[i] = Activity.IDLE;
    }
    public int reserve(){
        for(int i=0;i<workStationStates.length;i++){
            if(workStationStates[i]== Activity.IDLE){
                workStationStates[i]=Activity.BUSY;
                return i;
            }
        }
        return -1;
    }

    public int getAmountBusy(){
        int res=0;
        for(int i=0;i<workStationStates.length;i++){
            if(workStationStates[i]== Activity.BUSY){
                res++;
            }
        }
        return res;
    }
    public void free(int n){
        this.workStationStates[n]=Activity.IDLE;
    }
}