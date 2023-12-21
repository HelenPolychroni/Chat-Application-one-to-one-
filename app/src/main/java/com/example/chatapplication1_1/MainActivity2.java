package com.example.chatapplication1_1;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivity2 extends AppCompatActivity {
    TextView textView2,allMessages;
    EditText message, user_email;
    String nickname, email, emailToCheck, key;
    FirebaseDatabase database;
    FirebaseAuth auth;
    DatabaseReference reference;
    DatabaseReference newChatRef;
    DatabaseReference chatRef;
    Button send_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        textView2 = findViewById(R.id.textView2);  // hello message
        send_btn  = findViewById(R.id.send_btn);   // send message button

        nickname = getIntent().getStringExtra("nickname");
        email = getIntent().getStringExtra("e-mail");

        user_email = findViewById(R.id.userEmail);


        textView2.setText("Hello "+ nickname);

        allMessages = findViewById(R.id.textView3);   // whole conversation
        allMessages.setText("");                      // initialize with null

        message = findViewById(R.id.editTextText3);   // user chat message

        auth = FirebaseAuth.getInstance();  // user authentication
        database = FirebaseDatabase.getInstance();
        //reference = database.getReference("chats");
    }

    // first check if other user's mail exists in database
    public void findUserByEmail(View view){

        emailToCheck = user_email.getText().toString().trim();

        if (!emailToCheck.isEmpty() && !emailToCheck.equals(email)){
            auth.fetchSignInMethodsForEmail(emailToCheck).addOnCompleteListener(task -> {
                if (task.isSuccessful()){

                    System.out.println("Participant's email: "+ emailToCheck);

                    SignInMethodQueryResult result = task.getResult();
                    if (result != null && result.getSignInMethods() != null) {
                        //List<String> signInMethods = result.getSignInMethods();
                        // Handle the case where the email exists
                        System.out.println("Chat participant has been found!");
                        showMessage("Success", "Chat participant has been found!");
                        reference = database.getReference("chats");

                        // this was a comment
                        reference.push().setValue(new Chat121(email, emailToCheck,""));

                        startChat();  // start chat conversation

                        //Log.d(TAG, "Email exists. Sign-in methods: " + signInMethods.toString());
                    }
                    else {
                        System.out.println("IDK WTF");
                        showMessage("methods", "null");
                        // Handle the case where the result or sign-in methods are null
                        //Log.w(TAG, "Result or sign-in methods are null");
                    }
                }
                else {
                    // The email address is not associated with an existing account
                    // Handle the case where the email does not exist
                    System.out.println("Error, participant's email does not exists!");
                    showMessage("Error","Participant's email does not exist!");
                    //Log.w(TAG, "Email does not exist", task.getException());
                }
            });
        }
    }

    public void startChat(){

        send_btn.setVisibility(View.VISIBLE);
        message.setVisibility(View.VISIBLE);

       /* reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {    // snapshot e.g helen:hi
*/

               // String previousMessages = allMessages.getText().toString();
                //String previousMessages;
                System.out.println("Inside onDataChange()!");
                //System.out.println("reference: " + reference.child("users"));

                // Add a child event listener to iterate through each child
                reference.addChildEventListener(new ChildEventListener() {

                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {


                        System.out.println("New child!");
                        // Get the key of the current child (chat node)

                        String chatKey = dataSnapshot.getKey();


                        if ((dataSnapshot.child("email1").getValue() == email &&
                                dataSnapshot.child("email2").getValue() == emailToCheck) ||
                                (dataSnapshot.child("email1").getValue() == emailToCheck &&
                                        dataSnapshot.child("email2").getValue() == email)) {

                            String previousMessages = allMessages.getText().toString();
                            // Check if the current child has the "message" field
                            if (dataSnapshot.hasChild("message")) {

                                // Get the value of the "message" field for the current child
                                String message1 = dataSnapshot.child("message").getValue(String.class);

                                if (message1 != null) {
                                    // Handle the message for the current child as needed
                                    Log.d(TAG, "Chat Key: " + chatKey + ", Message: " + message1);

                                    allMessages.setText(previousMessages + "\n" + message1);

                                } else {
                                    Log.d(TAG, "Message is null for Chat Key: " + chatKey);
                                }
                            } else {
                                Log.d(TAG, "No 'message' field for Chat Key: " + chatKey);
                            }
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });


               // System.out.println("Snapshot key elements: "+ snapshot.child("-Nm8Ij7099RkKrTZRTiy").child("message").getValue());
                //System.out.println("key is: "+key);
                /*if (snapshot.getValue()!=null) {
                    System.out.println("Snapshot message: "+ snapshot.getValue());
                    //System.out.println("Message is: " + snapshot.child("chats").child("message").getValue());
                    allMessages.setText(previousMessages + "\n" + snapshot.getValue());
                }*/


         /*   @Override
            public void onCancelled(@NonNull DatabaseError error) {}
       // });*/
    }

    public void send_msg(View view){  // send a message to the chat

        if(!message.getText().toString().trim().isEmpty()){

            // this was uncomment
            //Chat121 chatEmails = new Chat121(email, emailToCheck, nickname+":"+message.getText().toString());

            System.out.println("Create object type Chat121");

            //newChatRef = reference.push();
            //newChatRef.setValue(/*nickname+":"+message.getText().toString(),*/ chatEmails);  // save to firebase

            // this was uncomment
            //reference.push().setValue(chatEmails);

            reference.ch

            message.setText("");   // clear message field

            key = reference.getKey();
            System.out.println("Key is: "+ key);
            System.out.println("reference: " +  reference);
            //System.out.println("NewChatRef : "+ newChatRef);

            //chatRef = newChatRef.child(key);
            //System.out.println("chatref: "+ chatRef);
            //chatRef = database.getReference("chats").child(key);

        }
        else {showMessage("Error","Please write a message first!..");}
    }
    void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }
}