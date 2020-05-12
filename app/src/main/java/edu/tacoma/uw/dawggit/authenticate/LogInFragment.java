package edu.tacoma.uw.dawggit.authenticate;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import edu.tacoma.uw.dawggit.R;

/**
 * This fragment is responsible for displaying and retrieving login information.
 * A simple {@link Fragment} subclass.
 * Use the {@link LogInFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogInFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    /**Allows information to be passed from this fragment back into SignInActivity*/
    private LoginFragmentListenter mLoginFragmentListener;

    /**
     /**Allows information to be passed from this fragment back into SignInActivity
     */
    public interface LoginFragmentListenter {
        void login(String email, String pwd);
    }

    public LogInFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LogInFragment.
     */
    public static LogInFragment newInstance(String param1, String param2) {
        LogInFragment fragment = new LogInFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    /**
     * Sets the fragment view.
     * Validates user login information.
     * Retrieves user inputted login information.
     * Sends the login information back into SignInActivity.
     * @param inflater inflate the fragment
     * @param container SignInActivity
     * @param savedInstanceState Null
     * @return fragment_login.xml
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Log In");
        View view =  inflater.inflate(R.layout.fragment_log_in, container, false);
        mLoginFragmentListener = (LoginFragmentListenter) getActivity();
        final EditText emailText = view.findViewById(R.id.et_login_email);
        final EditText passwordText = view.findViewById(R.id.et_login_password);
        final Button loginButton = view.findViewById(R.id.button_login_signin);
        final Button registerButton = view.findViewById(R.id.button_login_go_to_register);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailText.getText().toString();
                String pwd = passwordText.getText().toString();
                if(TextUtils.isEmpty(email) || !email.contains("@uw.edu")) {
                    Toast.makeText(v.getContext(), "Enter valid email address"
                            , Toast.LENGTH_SHORT).show();
                    emailText.requestFocus();
                }
                else if(TextUtils.isEmpty(pwd) || pwd.length() < 6) {
                    Toast.makeText(v.getContext(), "Enter valid password (at least 6 characters)"
                            , Toast.LENGTH_SHORT).show();
                    passwordText.requestFocus();
                }
                else {
                    mLoginFragmentListener.login(emailText.getText().toString(),
                            passwordText.getText().toString());
                }
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.sign_in_fragment_id, new RegisterFragment(), "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });
        return view;
    }
}
