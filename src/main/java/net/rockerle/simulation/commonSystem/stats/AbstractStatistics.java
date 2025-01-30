package net.rockerle.simulation.commonSystem.stats;

public abstract class AbstractStatistics implements Stats {

    protected double n,sum,square;

    public AbstractStatistics(){
        n=0;
        sum=0;
        square=0;
    }

    public abstract void update(double x);
    public abstract boolean stop(int acc);
    public double get(){
        if(n==0)
            return 0.0;
        else
            return sum/n;
    }
}