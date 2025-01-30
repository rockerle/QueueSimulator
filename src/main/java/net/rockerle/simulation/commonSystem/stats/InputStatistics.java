package net.rockerle.simulation.commonSystem.stats;

public class InputStatistics {

    private double lambda;

    public InputStatistics(double l){
        //Setting the mean operating time
        this.lambda=l;
    }
    private double reverseDistribution(double y){
        return - (Math.log(1-y)/lambda);
    }

    public double draw(){
        return reverseDistribution(Math.random());
    }
}