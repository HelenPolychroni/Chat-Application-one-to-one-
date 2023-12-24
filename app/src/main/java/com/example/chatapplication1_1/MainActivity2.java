package com.example.chatapplication1_1;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivity2 extends AppCompatActivity {
    TextView textView2, allMessages;
    EditText message, user_email;
    String nickname, email, emailToCheck;
    FirebaseDatabase database;
    FirebaseAuth auth;
    DatabaseReference reference;
    Button send_btn;
    ImageButton imageSendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        textView2 = findViewById(R.id.hello_msg);  // hello user message
        send_btn = findViewById(R.id.send_btn);   // send message button

        imageSendButton = findViewById(R.id.imageSendButton);

        nickname = getIntent().getStringExtra("nickname");
        email = getIntent().getStringExtra("e-mail");

        user_email = findViewById(R.id.userEmail);      // chat participant's email

        textView2.setText("Hello " + nickname);

        allMessages = findViewById(R.id.textView3);    // whole conversation
        allMessages.setText("");                       // initialize with null

        message = findViewById(R.id.editTextText3);   // user chat message

        auth = FirebaseAuth.getInstance();           // user authentication
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("chats");

        message.setVisibility(View.GONE);
        send_btn.setVisibility(View.GONE);          // send message button
        imageSendButton.setVisibility(View.GONE);
    }


    public void findUserByEmail(View view) {       // button to find chat participant  by email

        emailToCheck = user_email.getText().toString().trim();

        if (!emailToCheck.isEmpty() && !emailToCheck.equals(email)) {
            auth.fetchSignInMethodsForEmail(emailToCheck).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    System.out.println("Participant's email: " + emailToCheck);

                    SignInMethodQueryResult result = task.getResult();
                    if (result != null && result.getSignInMethods() != null) {
                       // Handle the case where the email  exists
                        System.out.println("Chat participant has been found!");
                        showMessage("Success", "Chat participant has been found!");

                        startChat();  // start chat conversation
                    } else {  // Handle the case where the result or sign-in methods are null
                        System.out.println("Error");
                    }
                } else {
                    // The email address is not associated with an existing account
                    // Handle the case where the email does not exist
                    System.out.println("Error, participant's email does not exist!");
                    showMessage("Error", "Participant's email does not exist!");
                }
            });
        } else if (emailToCheck.isEmpty())
            showMessage("Error", "Chat participant's email cannot be null!");
        else showMessage("Error", "Mate, you cannot chat with yourself!");
    }

    public void startChat() {

        // show buttons
        send_btn.setVisibility(View.VISIBLE);
        message.setVisibility(View.VISIBLE);
        //imageSendButton.setVisibility(View.VISIBLE);


        /* ---------------------- Initialize Realtime Database: ----------------------*/
        String emails1 = email + ", " + emailToCheck;   // 1st users emails combination
        String emails2 = emailToCheck + ", " + email;   // 2nd users emails combination

        Query query = reference.orderByChild("email").equalTo(emails1);
        Query query2 = reference.orderByChild("email").equalTo(emails2);

        //query.addListenerForSingleValueEvent(new ValueEventListener() {
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String previousMessages = allMessages.getText().toString();

                if (dataSnapshot.exists()) {
                    System.out.println("Case1");
                    System.out.println("Key: " + dataSnapshot.getKey());
                    showMessages(dataSnapshot, previousMessages);

                } else {  // does not exist or exist the 2nd combination
                    query2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String previousMessages = allMessages.getText().toString();

                            if (snapshot.exists()) {
                                System.out.println("Case2");
                                showMessages(snapshot, previousMessages);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        history();  // load previous messages in the chat
    }


    private void showMessages(DataSnapshot dataSnapshot, String previousMessages) {

        System.out.println("Inside updateMessage2()");

        // Reference to the one and only match child node
        DataSnapshot userSnapshot = dataSnapshot.getChildren().iterator().next();
        //System.out.println("UserSnapShot" + userSnapshot);

        String[] messages = Objects.requireNonNull(userSnapshot.child("message").getValue()).toString().trim().split(", ");
        String lastMessage = messages[messages.length - 1];

        System.out.println("Previous messages: " + previousMessages);
        System.out.println("Last message: " + lastMessage);

        allMessages.setText(previousMessages + "\n" + lastMessage);

        System.out.println("Message has been shown successfully!");
    }

    public void history() {   // load chat history

        String emails1 = email + ", " + emailToCheck;
        String emails2 = emailToCheck + ", " + email;

        Query query = reference.orderByChild("email").equalTo(emails1);///equalTo(emails1);
        Query query2 = reference.orderByChild("email").equalTo(emails2);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    loadMessages(dataSnapshot);
                } else {   // does not exist or exist the 2nd combination
                    query2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                loadMessages(snapshot);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private void loadMessages(DataSnapshot dataSnapshot) {

        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {

            System.out.println("Chat exists!");

            String messages = userSnapshot.child("message").getValue(String.class);

            if (messages != null) {
                System.out.println("All messages: " + messages);
                String[] messagesAr = messages.split(", ");

                // Create a StringBuilder to concatenate array elements
                StringBuilder stringBuilder = new StringBuilder();

                for (String msg : messagesAr) {
                    System.out.println(msg);
                    stringBuilder.append(msg).append("\n");
                    //allMessages.setText(pm + "\n" + msg);
                }
                allMessages.setText(stringBuilder);
            }
        }
    }

    public void send_msg(View view) {  // button's method: send a message in the chat

        if (!message.getText().toString().trim().isEmpty()) {
            System.out.println("Inside send_msg()!");

            checkAndCreateChat121(message.getText().toString().trim());

            message.setText("");   // clear message field
        } else {
            showMessage("Error", "Please write a message first!..");
        }
    }

    public void checkAndCreateChat121(String msg) {

        System.out.println("Inside checkAndCreateChat121()!");

        String emails1 = email + ", " + emailToCheck;
        String emails2 = emailToCheck + ", " + email;

        Query query = reference.orderByChild("email").equalTo(emails1);///equalTo(emails1);
        Query query2 = reference.orderByChild("email").equalTo(emails2);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            //query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    System.out.println("Case1 1st email combo");
                    //System.out.println("Key: " + dataSnapshot.getKey());
                    updateMessage(dataSnapshot, msg);

                } else {  // does not exist or exist the 2nd combination
                    query2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                System.out.println("Case2 2nd email combo");
                                updateMessage(snapshot, msg);
                            } else { // Node doesn't exist, create a new node
                                System.out.println("Case3 create new chat node");
                                createNode(snapshot, emails1, msg);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void updateMessage(DataSnapshot dataSnapshot, String newMessage) {

        System.out.println("Inside updateMessage()");

        // Reference to the 'message' attribute in the specific chat node
        DataSnapshot userSnapshot = dataSnapshot.getChildren().iterator().next();
        System.out.println("UserSnapShot" + userSnapshot);

        DatabaseReference messageReference = reference.child(Objects.requireNonNull(userSnapshot.getKey()));


        String prevMessages = Objects.requireNonNull(userSnapshot.child("message").getValue()).toString();

        System.out.println("Previous msgs: " + prevMessages);
        System.out.println("Message to add: " + newMessage);

        String newMsg = prevMessages + ", " + nickname + ":" + newMessage;
        System.out.println("New message: " + newMsg);


        messageReference.child("message").setValue(newMsg);

        System.out.println("Chat has been updated successfully!");
    }

    private void createNode(DataSnapshot snapshot, String emails, String msg) {

        System.out.println("Creating new chat node...");
        DatabaseReference newChatRef = FirebaseDatabase.getInstance().getReference("chats").push();

        String msgToAdd = nickname + ":" + msg;

        newChatRef.setValue(new Chat121(emails, msgToAdd));

        System.out.println("New chat node has been created successfully!");
    }

    void showMessage(String title, String message) {
        // Create an AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the title and message
        builder.setTitle(title);
        builder.setMessage(message);

        // Set the positive button and its click listener
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do something when the "Close" button is clicked
                dialog.dismiss(); // Dismiss the dialog
            }
        });

        // Create and show the dialog
        AlertDialog helpDialog = builder.create();
        helpDialog.show();

        //new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }
}

















