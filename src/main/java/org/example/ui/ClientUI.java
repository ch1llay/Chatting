package org.example.ui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
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
    TextArea chatLabel = new TextArea();



    private void showChat(Stage primaryStage) throws IOException {
        var grid = new GridPane();
        grid.setAlignment(Pos.CENTER);

        chatLabel.setEditable(false);
        var sendButton = new Button(">");
        var textField = new TextField();
        var updateUsersButton = new Button("↻");
        grid.add(chatLabel, 0, 0);
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

            if(message.isEmpty()){
                return;
            }

            chatLabel.setText(chatLabel.getText() + "me: " + message + "\n");
            var to = combo.getSelectionModel().getSelectedItem();
            if (to != null) {
                client.Send(to, message);
            }

            textField.clear();
        });
    }

    private void showReg(Stage primaryStage) {
        primaryStage.setTitle("Chat");

        var gridUsername = new GridPane();
        gridUsername.setAlignment(Pos.CENTER);
        var label = new Label("username");

        var userNameField = new TextField();
        userNameField.promptTextProperty().setValue("username");
        var userNameButton = new Button("Зарегистрироваться в чате");
        userNameButton.setFocusTraversable(true);

        gridUsername.add(label, 0, 0);
        gridUsername.add(userNameField, 0, 1);
        gridUsername.add(userNameButton, 0, 2);

        primaryStage.setScene(new Scene(gridUsername, 520, 250));
        primaryStage.show();

        userNameButton.setOnAction((ActionEvent e) -> {
            username = userNameField.getText();
            try {

                client = new MessageClient(username);

                client.StartReceiving((Message m) -> {
                    System.out.println(Json.toJson(m));
                    if (m.ServerResp != null) {
                        System.out.println(m.ServerResp);
                        if (m.ServerResp.equals("auth")){
                            isAuth = true;
                        }
                        else if (m.Command != null && m.Command.equals("get users")) {
                            Users = new ArrayList<>(List.of(m.ServerResp.split(",")));
                            System.out.println(Json.toJson(Users));
                        }
                    }

                    if(isAuth) {
                        System.out.println(m.Text);
                        if (m.From != null) {
                            chatLabel.setText(chatLabel.getText() + m.From + ": " + m.Text + "\n");
                        }
                    }
                    else{
                        JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Such name has been used", "Error", JOptionPane.ERROR_MESSAGE);
                        System.exit(0);
                    }
                });
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
        primaryStage.setOnCloseRequest(windowEvent -> {
            System.exit(0);
        });
        showReg(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
