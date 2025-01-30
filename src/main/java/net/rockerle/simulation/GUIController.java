package net.rockerle.simulation;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import net.rockerle.simulation.commonSystem.formulae.ErlangBlock;
import net.rockerle.simulation.commonSystem.stats.AbstractStatistics;
import net.rockerle.simulation.commonSystem.stats.OutputStatistics;
import net.rockerle.simulation.hybrid.HybridSystem;
import net.rockerle.simulation.loss.LossSystem;
import net.rockerle.simulation.waiting.WaitingSystem;

public class GUIController {
    // Input Fields
    @FXML
    private TextField l, n, lambda, my, accuracy;

    @FXML
    private Button lossUpdate,waitUpdate, hybridUpdate;

    //M/M/0
    @FXML
    private Label p_LossC, p_LossS, littlesT, meanMemberLoss, lossError;

    //M/M/c-INF
    @FXML //Label For Calculated Values
    private Label waitStability, n_QueueWaitC, t_QueueWaitC, t_OnlyQueueWaitC,
                    n_BEWaitC, t_BEWaitC, n_WaitC, t_WaitC, waitC, waitS;
    @FXML //Label For Simulated Values
    private Label n_QueueWaitS, t_QueueWaitS, t_OnlyQueueWaitS,
                    n_BEWaitS, t_BEWaitS, nWaitS, tWaitS;

    //M/M/n/n-INF
    @FXML
    private Label hybridN, hybridN_Queue, hybridN_BE, hybrid_Blocking, hybridError,
                    hybrid_T, hybrid_TQueue, hybrid_TOnlyQueue, hybrid_TBE;

    private WaitingSystem waitSys;
    private LossSystem lossSys;
    private HybridSystem hybridSys;
    private Task<Void> simulation;
    private Thread simulationThread;
    private boolean simulationRunning = false;

    @FXML
    protected void lossStart(){
        if(simulationRunning){
            simulation.cancel();
            simulationRunning=false;
            return;
        }
        int n_loss; int acc;
        double lambda_loss;
        double my_loss;
        try {
            n_loss = Integer.parseInt(this.n.getCharacters().toString());
            my_loss = Double.parseDouble(this.my.getCharacters().toString());
            lambda_loss = Double.parseDouble(this.lambda.getCharacters().toString());
            acc = Integer.parseInt(this.accuracy.getCharacters().toString());
        }catch(NumberFormatException e){
            this.lossError.setText("Invalid Input Parameter");
            return;
        }
        this.lossError.setText("");
        double p_loss = ErlangBlock.bFormula(n_loss,lambda_loss, my_loss);
        p_LossC.setText(String.valueOf(p_loss));
        p_LossS.setText("-");
        littlesT.setText(String.valueOf(ErlangBlock.littlesTheorem(lambda_loss/my_loss, p_loss)));
        meanMemberLoss.setText("-");

        AbstractStatistics[] outStats = new AbstractStatistics[2];
        outStats[0] = new OutputStatistics();
        outStats[1] = new OutputStatistics();

        this.lossSys = new LossSystem(n_loss, my_loss, lambda_loss, acc, outStats);
        simulation = new Task<>() {
            @Override
            protected Void call() throws Exception {
                return lossSys.run();
            }
        };
        simulation.setOnRunning((e)->lossUpdate.setText("Simulating currently"));
        simulation.setOnSucceeded((e)->{
            this.p_LossS.setText(String.valueOf(outStats[0].get()));
            this.meanMemberLoss.setText(String.valueOf(outStats[1].get()));
            lossUpdate.setText("Start Simulation");
        });
        simulation.setOnCancelled((e)->{
            lossUpdate.setText("Start Simulation");
            System.out.println("canceled loss simulation");
        });
        this.simulationThread = new Thread(simulation);
        this.simulationThread.start();
    }
    @FXML
    protected void waitStart(){
        if(simulationRunning){
            simulation.cancel(true);
            this.simulationRunning = false;
            return;
        }

        this.waitStability.setText(" ");
        this.n_BEWaitS.setText(" ");
        this.t_BEWaitS.setText(" ");
        this.n_QueueWaitS.setText(" ");
        this.t_QueueWaitS.setText("");
        this.t_OnlyQueueWaitS.setText(" ");
        this.nWaitS.setText(" ");
        this.tWaitS.setText(" ");
        this.waitS.setText(" ");

        int n_wait;
        int acc;
        double lambda_wait;
        double my_wait;
        try {
            n_wait = Integer.parseInt(this.n.getCharacters().toString());
            my_wait = Double.parseDouble(this.my.getCharacters().toString());
            lambda_wait = Double.parseDouble(this.lambda.getCharacters().toString());
            acc = Integer.parseInt(this.accuracy.getCharacters().toString());
        }catch(NumberFormatException e){
            this.waitStability.setText("Invalid Input Parameter");
            return;
        }
        if(lambda_wait>n_wait*my_wait){
            System.out.println("No stability given when arrival rate is bigger than combined service rate");
            this.waitStability.setText("Arrival-Rate>Service-Rate stability criteria not given!");
            return;
        }

        this.waitC.setText(String.valueOf(ErlangBlock.cFormula(n_wait, 1/my_wait, 1/lambda_wait)));
        this.n_QueueWaitC.setText(String.valueOf(ErlangBlock.n_QUEUE(n_wait,lambda_wait, my_wait)));
        this.t_QueueWaitC.setText(String.valueOf(ErlangBlock.t_QUEUE(n_wait,lambda_wait, my_wait)));
        this.t_OnlyQueueWaitC.setText(String.valueOf(ErlangBlock.t_onlyQueue(n_wait, lambda_wait,my_wait)));
        this.n_BEWaitC.setText(String.valueOf(ErlangBlock.n_BE(lambda_wait,my_wait)));
        this.t_BEWaitC.setText(String.valueOf(ErlangBlock.t_BE(my_wait)));
        this.n_WaitC.setText(String.valueOf(ErlangBlock.n_total(n_wait,lambda_wait,my_wait)));
        this.t_WaitC.setText(String.valueOf(ErlangBlock.t_total(n_wait,lambda_wait,my_wait)));
        AbstractStatistics[] outStats = new AbstractStatistics[8];
        for(int i=0;i<8;i++)
            outStats[i] = new OutputStatistics();

        this.waitSys = new WaitingSystem(n_wait, my_wait, lambda_wait, acc, outStats);

//        Start Simulation in Task on different Thread to not block the GUI
        simulation = new Task<>() {
          @Override
          protected Void call() throws Exception {
              return waitSys.run();
          }
        };
        simulation.setOnRunning((e)->waitUpdate.setText("Simulating currently"));
        simulation.setOnSucceeded((e)->{
            this.n_BEWaitS.setText(String.valueOf(outStats[0].get()));
            this.t_BEWaitS.setText(String.valueOf(outStats[1].get()));
            this.n_QueueWaitS.setText(String.valueOf(outStats[2].get()));
            this.t_QueueWaitS.setText(String.valueOf(outStats[3].get()));
            this.t_OnlyQueueWaitS.setText(String.valueOf(outStats[4].get()));
            this.nWaitS.setText(String.valueOf(outStats[5].get()));
            this.tWaitS.setText(String.valueOf(outStats[6].get()));
            this.waitS.setText(String.valueOf(outStats[7].get()));
            waitUpdate.setText("Start Simulation");
        });
        simulation.setOnFailed((e)-> {
            System.out.println("Something went wrong while simulating");
            System.out.println("From "+e.toString());
            waitUpdate.setText("Start Simulation");
        });
        simulation.setOnCancelled((e)->waitUpdate.setText("Start Simulation"));
        new Thread(simulation).start();
    }

    @FXML
    protected void hybStart(){
        int queueSize, wsNumber, acc;
        double mot, mat;
        try {
            queueSize = Integer.parseInt(this.l.getCharacters().toString());
            wsNumber = Integer.parseInt(this.n.getCharacters().toString());
            mot = Double.parseDouble(this.my.getCharacters().toString());
            mat = Double.parseDouble(this.lambda.getCharacters().toString());
            acc = Integer.parseInt(this.accuracy.getCharacters().toString());
        }catch(NumberFormatException e){
            this.hybridError.setText("Invalid Input Parameter");
            return;
        }
        OutputStatistics[] oStats = new OutputStatistics[8];
        for(int i=0;i<8;i++){
            oStats[i] = new OutputStatistics();
        }
        this.hybridSys = new HybridSystem(queueSize, wsNumber, mot, mat, acc, oStats);

        // Put Simulation into its own Task to run on a different Thread
        simulation = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    return hybridSys.run();
                }catch(Exception e){
                    e.printStackTrace();
                }
                throw new Exception("Hybrid-Simulation Failed");
            }
        };

        simulation.setOnRunning((e)->{
            hybridUpdate.setText("Simulating currently");
            hybridError.setText("");
        });
        simulation.setOnSucceeded((e)->{
            this.hybridN.setText(String.valueOf(oStats[0].get()));
            this.hybridN_BE.setText(String.valueOf(oStats[1].get()));
            this.hybridN_Queue.setText(String.valueOf(oStats[2].get()));
            this.hybrid_Blocking.setText(String.valueOf(oStats[3].get()));
            this.hybrid_T.setText(String.valueOf(oStats[4].get()));
            this.hybrid_TBE.setText(String.valueOf(oStats[5].get()));
            this.hybrid_TQueue.setText(String.valueOf(oStats[6].get()));
            this.hybrid_TOnlyQueue.setText(String.valueOf(oStats[7].get()));
            this.hybridUpdate.setText("Start Simulation");
        });
        simulation.setOnFailed((e)-> {
            System.out.println("Something went wrong while simulating");
            System.out.println("From "+e.toString());
            hybridUpdate.setText("Start Simulation");
        });
        simulation.setOnCancelled((e)->waitUpdate.setText("Start Simulation"));
        try {
            new Thread(simulation).start();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}