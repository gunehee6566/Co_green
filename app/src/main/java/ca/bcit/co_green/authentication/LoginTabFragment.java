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
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import ca.bcit.co_green.MainActivity;
import ca.bcit.co_green.R;

public class LoginTabFragment extends Fragment {

    EditText editEmail;
    EditText editPassword;
    Button btnLogin;
    TextView tvCreateNewAccount;
    FirebaseAuth fAuth;
    float v = 0;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.login_tab_fragment, container, false);

        fAuth = FirebaseAuth.getInstance();

        editEmail = root.findViewById(R.id.editEmail);
        editPassword = root.findViewById(R.id.editPassword);
        btnLogin = root.findViewById(R.id.btnLogin);
        tvCreateNewAccount = root.findViewById(R.id.tvCreateNewAccount);

        editEmail.setTranslationX(800);
        editPassword.setTranslationX(800);
        btnLogin.setTranslationX(800);
        tvCreateNewAccount.setTranslationX(800);

        editEmail.setAlpha(v);
        editPassword.setAlpha(v);
        btnLogin.setAlpha(v);
        tvCreateNewAccount.setAlpha(v);

        editEmail.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(800).start();
        editPassword.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(800).start();
        btnLogin.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(800).start();
        tvCreateNewAccount.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(800).start();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editEmail.getText().toString().trim();
                String password = editPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    editEmail.setError("Email is required");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    editEmail.setError("Password is required");
                    return;
                }

                if (password.length() < 6) {
                    editEmail.setError("Password must be >= 6 characters.");
                    return;
                }

                //authenticate user
                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Login successful!", Toast.LENGTH_LONG).show();
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
}
