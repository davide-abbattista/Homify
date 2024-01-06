package com.example.homify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://homify-is07-default-rtdb.europe-west1.firebasedatabase.app/");
    DatabaseReference myRef = database.getReference();

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private TextView name;
    private TextView surname;
    private TextView email;
    private TextView password;
    private TextView repeatPassword;
    private Button btnRegister2;
    private Utente user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = findViewById(R.id.txtNameRegister);
        surname = findViewById(R.id.txtSurnameRegister);
        email = findViewById(R.id.txtEmailRegister);
        password = findViewById(R.id.txtPasswordRegister);
        repeatPassword = findViewById(R.id.txtRepeatPassowrdRegister);
        btnRegister2 = findViewById(R.id.btnRegister2);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        btnRegister2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!name.getText().toString().equals("") && !surname.getText().toString().equals("") &&!email.getText().toString().equals("")) {
                    if (repeatPassword.getText().toString().equals(password.getText().toString())) {
                        try {
                            createAccount(email.getText().toString(), password.getText().toString());
                        } catch (java.lang.IllegalArgumentException exception) {
                            Toast.makeText(RegisterActivity.this, "Complilare entrambe le celle", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Reinserire la stessa password [deve essere di almeno 6 caratteri]", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Compilare tutti i campi", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createAccount(String email, String password) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("EmailPasswordLogin", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            insertOnDatabase();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("EmailPasswordLogin", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "L'indirizzo email è già utilizzato da un altro account o la password fornita è invalida",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        // [END create_user_with_email]
    }

    private void updateUI(FirebaseUser user) {
        if(user!=null){
            Intent intent = new Intent(getApplicationContext(),UserHomeActivity.class);
            startActivity(intent);
        }
    }

    private void insertOnDatabase() {
        user = new Utente(name.getText().toString(), surname.getText().toString(), email.getText().toString());
        myRef.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user);
    }
}