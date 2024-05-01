package org.example;

import java.time.LocalDateTime;

public class Message {
    public String Text;
    public String From;
    public String To;
    public String DateTime;
    public String Command;
    public String ServerResp;

    public Message(String text, String from, String to) {
        this.Text = text;
        this.DateTime = LocalDateTime.now().toString();
        this.From = from;
        this.To = to;
    }

    public Message(String from, String command) {
        this.DateTime = LocalDateTime.now().toString();
        this.From = from;
        this.Command = command;
    }

    public Message(String serverResp) {
        this.DateTime = LocalDateTime.now().toString();
        this.ServerResp = serverResp;
    }
}
