package UI;

import java.util.Collection;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;

public class ListViewWithStringAndCheckBox extends Application {

    @Override
    public void start(Stage primaryStage) {
        ListView<String> listView = new ListView<>();
        String[] toppings = {"Cheese", "Pepperoni", "Black Olives"};
        listView.getItems().addAll(toppings);
        listView.setCellFactory(CheckBoxListCell.forListView(new Callback<String, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(String item) {
                BooleanProperty observable = new SimpleBooleanProperty();
                observable.addListener((obs, wasSelected, isNowSelected)
                        -> System.out.println("Check box for " + item + " changed from " + wasSelected + " to " + isNowSelected)
                );
                return observable;
            }
        }));

        BorderPane root = new BorderPane(listView);
        Scene scene = new Scene(root, 250, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
