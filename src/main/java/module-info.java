module net.rockerle.simulation {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens net.rockerle.simulation to javafx.fxml;
    exports net.rockerle.simulation;
}