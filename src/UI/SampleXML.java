/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author< ado_k
 */
public class SampleXML extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
        Parent root = (Parent) loader.load();

        FXMLDocumentController controller = (FXMLDocumentController) loader.getController();
        controller.setStageAndSetupListeners(stage); // or what you want to do

        Scene scene = new Scene(root);
        //scene.getStylesheets().add("C:\\Users\\ado_k\\Desktop\\11.css");

        stage.setScene(scene);
        stage.setTitle("EPredictor");
        stage.setMinHeight(650);
        stage.setMinWidth(800);
        stage.show();

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
