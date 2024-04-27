package org.example.ui;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;


public class ClientUI extends Application {
    String firstElem = "Выберите получателя";
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Create a grid pane to hold the components
        var grid = new GridPane();

        // Add a button and text field to the grid pane
        var label = new TextArea();
        label.setEditable(false);
        var sendButton = new Button(">");
        var textField = new TextField();
        grid.add(label, 0, 0);
        grid.add(textField, 0, 1);
        grid.add(sendButton, 2, 1);

        // Set the stage and scene
        primaryStage.setScene(new Scene(grid, 520, 250));
        primaryStage.show();

        ComboBox<String> combo = new ComboBox<>();
        combo.getItems().addAll(firstElem, "123");
        combo.getSelectionModel().clearAndSelect(0);
        combo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                System.out.println(t1);
            };
        });
        grid.add(combo, 0, 2);

        // Add event handler for button press
        sendButton.setOnAction((ActionEvent e) -> {
            // Send message to server
            var message = textField.getText();
            System.out.println("Sending message: " + message);

            // ... code to send the message to the server ...
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
