package net.rockerle.simulation.commonSystem.stats;

public class OutputStatistics extends AbstractStatistics {

    public OutputStatistics(){
        super();
    }
    @Override
    public void update(double x){
        n++;
        sum += x;
        square += Math.pow(x,2);
    }
    @Override
    public boolean stop(int accuracy){
        double singleVariance = (square/n) - (Math.pow(sum/n,2));
        double totalVariance = (1/n) * singleVariance;
        double standardDeviation = Math.sqrt(totalVariance);
        if(standardDeviation < Math.pow(10,-accuracy))
            return true;
        return false;
    }
}