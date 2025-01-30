package net.rockerle.simulation.commonSystem.formulae;

public class ErlangBlock {
    // n = #workstations
    public static double bFormula(int n, double lambda, double my){
        if(n==0)
            return 1.0;

        double a = lambda/my;
        double result;
        double zaehler = Math.pow(a,n)/fact(n);
        double nenner = 0;
        for(int i=0;i<=n;i++){
            double top = Math.pow(a,i);
            double bot = fact(i);
            nenner += top/bot;
        }
        result = zaehler/nenner;
        return result;
    }

    /*
     * breaking Erlang C down to x/(y+x)
     */
    public static double cFormula(int n, double lambda, double my){
        double a = lambda/my;
        double x = (Math.pow(a,n)/fact(n))*(n/(n-a));
        double y = 0.0;
        for(int i=0;i<=n-1;i++){
            double top = Math.pow(a,i);
            double bot = fact(i);
            y += top/bot;
        }
        return x/(y+x);
    }

    public static double fact(int n){
        double res = 1.0;
        for(int i=1;i<=n;i++){
            res *= i;
        }
        return res;
    }

    public static double littlesTheorem(double avgLoad, double blockRate){
        return avgLoad*(1-blockRate);
    }

    public static double n_QUEUE(int n, double lambda, double my){
        double a = lambda/my;
        return cFormula(n, lambda, my) * (a/(n-a));
    }
    public static double t_QUEUE(int n, double lambda, double my){
        return n_QUEUE(n,lambda,my)/lambda;
    }
    public static double t_onlyQueue(int n, double lambda, double my){
        return 1/lambda * ((lambda/my)/(n-(lambda/my)));
    }
    public static double n_total(int n, double lambda, double my){
        return n_QUEUE(n,lambda,my) + n_BE(lambda,my);
    }
    public static double t_total(int n, double lambda, double my){
        return t_QUEUE(n,lambda,my) + t_BE(my);
    }
    public static double n_BE(double lambda, double my){
        return lambda/my;
    }
    public static double t_BE(double my){
        return 1/my;
    }
}