/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class DialogTestController {

    Benchmark bench;

    @FXML
    private TextField sourceLink;

    @FXML
    private TextField averageDegree;

    @FXML
    private TextField maxDegree;

    @FXML
    private TextArea description;

    private Stage dialogStage;
    private boolean okClicked = false;

    @FXML
    private void initialize() {
        //Benchmark bench = new Benchmark();

    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleOk() {
        bench.setAverageDegree(averageDegree.getText());
        bench.setDescription(description.getText());
        bench.setMaxDegree(maxDegree.getText());
        bench.setSourceLink(sourceLink.getText());

        okClicked = true;
        dialogStage.close();

    }

    /**
     * Called when the user clicks cancel.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @FXML
    void submit(ActionEvent event) {

    }

    public Benchmark getBench() {
        return bench;
    }

    public void setBench(Benchmark bench) {
        this.bench = bench;
    }

}
