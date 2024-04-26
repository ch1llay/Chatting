package org.example;

import java.time.LocalDateTime;

public class Message {
    public String Text;
    public String From;
    public String To;
    public String DateTime;

    Message(String text, String from, String to) {
        this.Text = text;
        this.DateTime = LocalDateTime.now().toString();
        this.From = from;
        this.To = to;
    }
}
