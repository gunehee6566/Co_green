package ca.bcit.co_green.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ca.bcit.co_green.MainActivity;
import ca.bcit.co_green.R;
import ca.bcit.co_green.User;

public class SignupTabFragment extends Fragment {

    EditText editName, editEmail, editPassword;
    Button btnRegister;
    TextView tvCreateNewAccount;
    FirebaseAuth fAuth;
    DatabaseReference databaseInput;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.signup_tab_fragment, container, false);

        databaseInput = FirebaseDatabase.getInstance().getReference("user");

        editName = root.findViewById(R.id.editName);
        editEmail = root.findViewById(R.id.editEmail);
        editPassword = root.findViewById(R.id.editPassword);
        btnRegister = root.findViewById(R.id.btnRegister);
        tvCreateNewAccount = root.findViewById(R.id.tvCreateNewAccount);

        fAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editName.getText().toString().trim();
                String email = editEmail.getText().toString().trim();
                String password = editPassword.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    editName.setError("Name is required");
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    editEmail.setError("Email is required");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    editEmail.setError("Password is required");
                    return;
                }

                if (password.length() <6) {
                    editEmail.setError("Password must be >= 6 characters.");
                    return;
                }


                // register user
                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            addUserName(task.getResult().getUser().getUid());
                            Toast.makeText(getContext(), "User Created", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getContext(), MainActivity.class));
                        } else {
                            Toast.makeText(getContext(), "ERROR: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

        });

        tvCreateNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return root;
    }

    private void addUserName(String id){
        String userName = editName.getText().toString().trim();
        if(TextUtils.isEmpty(userName)){
            Toast.makeText(getContext(), "Must enter some value.", Toast.LENGTH_LONG).show();
        }

        User user = new User(id,userName);
        Task setValueTask = databaseInput.child(id).setValue(user);

        setValueTask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(getContext(),"User Name added.",Toast.LENGTH_LONG).show();
                editName.setText("");
            }
        });
    }
}
