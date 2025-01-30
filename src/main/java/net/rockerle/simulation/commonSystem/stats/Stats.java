package net.rockerle.simulation.commonSystem.stats;

public interface Stats {
    void update(double x);
    boolean stop(int acc);
    public double get();
}
