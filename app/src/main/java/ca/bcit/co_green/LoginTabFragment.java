package ca.bcit.co_green;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class LoginTabFragment extends Fragment {
    EditText editEmail;
    EditText editPassword;
    Button btnLogin;
    TextView tvCreateNewAccount;
    float v = 0;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.login_tab_fragment, container, false);
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
        return root;
    }
}
