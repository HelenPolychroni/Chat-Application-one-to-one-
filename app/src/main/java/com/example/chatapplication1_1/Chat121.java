package com.example.chatapplication1_1;

public class Chat121 {
    String message, emails_combo;

    public Chat121(String emails_combo, String message) {
        setEmails_combo(emails_combo);
        setMessage(message);
    }

    public String getMessage() {return message;}

    public void setMessage(String message) {this.message = message;}

    public String getEmails_combo() {return emails_combo;}

    public void setEmails_combo(String emails_combo) {this.emails_combo = emails_combo;}
}
