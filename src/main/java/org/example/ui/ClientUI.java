package org.example.ui;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.example.Message;
import org.example.MessageClient;


public class ClientUI extends Application {
    String firstElem = "Выберите получателя";
    @Override
    public void start(Stage primaryStage) throws Exception {
        var username = "user1";
        var client = new MessageClient(username);

        var grid = new GridPane();

        var label = new TextArea();
        label.setEditable(false);
        var sendButton = new Button(">");
        var textField = new TextField();
        var updateUsersButton = new Button("↻");
        grid.add(label, 0, 0);
        grid.add(textField, 0, 1);
        grid.add(sendButton, 2, 1);

        primaryStage.setScene(new Scene(grid, 520, 250));
        primaryStage.show();

        ComboBox<String> combo = new ComboBox<>();
        combo.getItems().addAll(firstElem, "123");
        combo.getSelectionModel().clearAndSelect(0);
        combo.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> System.out.println(t1));
        grid.add(combo, 0, 2);
        grid.add(updateUsersButton, 1, 2);

        updateUsersButton.setOnAction((ActionEvent e) -> {
            // Send message to server
            var message = textField.getText();
            System.out.println("Sending message: " + message);
        });

        sendButton.setOnAction((ActionEvent e) -> {
            // Send message to server
            var message = textField.getText();
            label.setText(label.getText() + "me: " + message + "\n");
            client.Send(username, message);
        });

        client.StartReceiving((Message m) -> {
            System.out.println(m.Text);
            label.setText(label.getText() + m.From + ": " + m.Text + "\n");
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
