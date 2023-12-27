package com.example.chatapplication1_1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class MainActivity extends AppCompatActivity {
    EditText email,password,nickname;
    FirebaseAuth auth, auth1;
    FirebaseUser user, user1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = findViewById(R.id.email1);
        password = findViewById(R.id.password1);
        nickname = findViewById(R.id.nickname1);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user!=null){
            Button b = findViewById(R.id.button);
            b.setVisibility(View.GONE);
        }
    }
    public void signup(View view){
        if(!email.getText().toString().isEmpty() &&
                !password.getText().toString().isEmpty() &&
                !nickname.getText().toString().isEmpty()){
            auth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                user = auth.getCurrentUser();
                                updateUser(user,nickname.getText().toString());
                                showMessage("Success","User profile created!");
                            }else {
                                showMessage("Error",task.getException().getLocalizedMessage());
                            }
                        }
                    });
        }else {
            showMessage("Error","Please provide all info!");
        }
    }
    private void updateUser(FirebaseUser user, String nickname){
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(nickname)
                .build();
        user.updateProfile(request);
    }
    public void signin(View view){
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if(!email.getText().toString().isEmpty() &&
                !password.getText().toString().isEmpty()){
            auth.signInWithEmailAndPassword(email.getText().toString(),
                    password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        showMessage("Success","User signed in successfully!");

                        Button b = findViewById(R.id.button);
                        b.setVisibility(View.GONE);

                    }else {
                        showMessage("Error",task.getException().getLocalizedMessage());
                    }
                }
            });
        }
    }
    public void signout(View view){
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        showMessage("Success", "You signed out successfully.");
        auth.signOut();

        Button b = findViewById(R.id.button);
        b.setVisibility(View.VISIBLE);
    }
    public void chat(View view){
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user!=null){
            Intent intent = new Intent(this, MainActivity2.class);
            intent.putExtra("nickname",user.getDisplayName());
            intent.putExtra("e-mail",user.getEmail());
            //intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }else {
            showMessage("Error","Please sign-in or create an account first!");
        }
    }
    void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }
}