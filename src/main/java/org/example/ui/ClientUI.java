package org.example.ui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.example.lib.Message;
import org.example.lib.MessageClient;
import org.example.utils.Json;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ClientUI extends Application {
    String firstElem = "Выберите получателя";
    ArrayList<String> Users;
    MessageClient client;
    String username;
    private boolean isAuth;


    private void showChat(Stage primaryStage) throws IOException {
        var grid = new GridPane();
        var label = new TextArea();

        label.setEditable(false);
        var sendButton = new Button(">");
        var textField = new TextField();
        var updateUsersButton = new Button("↻");
        grid.add(label, 0, 0);
        grid.add(textField, 0, 1);
        grid.add(sendButton, 2, 1);


        ComboBox<String> combo = new ComboBox<>();
        combo.getItems().addAll(firstElem);
        combo.getSelectionModel().clearAndSelect(0);
        combo.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> System.out.println(t1));
        grid.add(combo, 0, 2);
        grid.add(updateUsersButton, 1, 2);

        primaryStage.setScene(new Scene(grid, 520, 250));
        primaryStage.show();

        client = new MessageClient(username);

        client.StartReceiving((Message m) -> {
            System.out.println(Json.toJson(m));
            if (m.ServerResp != null) {
                System.out.println(m.ServerResp);
                if (m.ServerResp.equals("auth")){
                    isAuth = true;
                }
                else if (m.Command.equals("get users")) {
                    Users = new ArrayList<>(List.of(m.ServerResp.split(",")));
                    System.out.println(Json.toJson(Users));
                }
                return;
            }

            if(isAuth) {
                System.out.println(m.Text);
                label.setText(label.getText() + m.From + ": " + m.Text + "\n");
            }
            else{
                label.setText("Не авторизованы");
            }
        });

        updateUsersButton.setOnAction((ActionEvent e) -> {
            try {
                client.SendCommand("get users");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            if (Users == null) {
                return;
            }

            combo.getItems().clear();
            combo.getItems().addAll(Users);
            combo.getSelectionModel().clearAndSelect(0);
        });

        sendButton.setOnAction((ActionEvent e) -> {
            var message = textField.getText();
            label.setText(label.getText() + "me: " + message + "\n");
            var to = combo.getSelectionModel().getSelectedItem();
            if (to != null) {
                client.Send(to, message);
            }
        });
    }

    private void showReg(Stage primaryStage) {
        primaryStage.setTitle("Chat");

        var gridUsername = new GridPane();
        var userNameField = new TextField();
        userNameField.promptTextProperty().setValue("username");
        var userNameButton = new Button("Зарегистрироваться в чате");

        gridUsername.add(userNameField, 0, 0);
        gridUsername.add(userNameButton, 0, 1);

        primaryStage.setScene(new Scene(gridUsername, 520, 250));
        primaryStage.show();

        userNameButton.setOnAction((ActionEvent e) -> {
            username = userNameField.getText();
            try {
                showChat(primaryStage);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "cannot connect to server");
                System.exit(1);
                throw new RuntimeException(ex);
            }
        });


    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        showReg(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
