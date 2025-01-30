package net.rockerle.simulation.waiting;

import net.rockerle.simulation.commonSystem.services.Service;

public class WaitingService extends Service{

    public WaitingService(int workStations) {
        super(workStations);
    }

    public void free(int n){
        super.free(n);
    }
}