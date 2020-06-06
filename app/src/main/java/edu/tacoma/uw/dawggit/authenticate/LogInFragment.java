/*
 * TCSS 450 - Spring 2020
 * Dawggit
 * Team 6: Codie Bryan, Kevin Bui, Minh Nguyen, Sean Smith
 */
package edu.tacoma.uw.dawggit.authenticate;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import edu.tacoma.uw.dawggit.MainActivity;
import edu.tacoma.uw.dawggit.R;
import edu.tacoma.uw.dawggit.main.HomeActivity;
import edu.tacoma.uw.dawggit.main.HomeFragment;

/**
 * This fragment is responsible for displaying and retrieving login information.
 * A simple {@link Fragment} subclass.
 * Use the {@link LogInFragment#newInstance} factory method to
 * create an instance of this fragment.
 * @author Kevin Bui
 * @author Codie Bryan
 */
public class LogInFragment extends Fragment {




    /**Used for firebase authorization.*/
    private FirebaseAuth mAuth;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private boolean flag;
    /**
     * Allows information to be passed from this fragment back into SignInActivity
     */
    private LoginFragmentListenter mLoginFragmentListener;

    /**
     * /**Allows information to be passed from this fragment back into SignInActivity
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
    private static LogInFragment newInstance(String param1, String param2) {
        LogInFragment fragment = new LogInFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public boolean getFlag() {
        return flag;
    }


    /**
     * Initializes firebase
     *
     * @param savedInstanceState null
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flag = false;


        mAuth = FirebaseAuth.getInstance();


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
     *
     * @param inflater           inflate the fragment
     * @param container          SignInActivity
     * @param savedInstanceState Null
     * @return fragment_login.xml
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(getActivity()).setTitle("Log In");
        View view =  inflater.inflate(R.layout.fragment_log_in, container, false);
        mLoginFragmentListener = (LoginFragmentListenter) getActivity();
        final EditText emailText = view.findViewById(R.id.et_login_email);
        final EditText passwordText = view.findViewById(R.id.et_login_password);
        final Button loginButton = view.findViewById(R.id.button_login_signin);
        final Button registerButton = view.findViewById(R.id.button_login_go_to_register);
        final Button forgotButton = view.findViewById(R.id.button_forgot);
        forgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailText.getText().toString();

                if (TextUtils.isEmpty(email) || !email.contains("@uw.edu")) {
                    Toast.makeText(v.getContext(), "Enter valid email address"
                            , Toast.LENGTH_SHORT).show();
                    emailText.requestFocus();
                } else {

                    mAuth.sendPasswordResetEmail(emailText.getText().toString());
                    Toast.makeText(v.getContext(), "Email Sent. Please check email inbox."
                            , Toast.LENGTH_SHORT).show();
                    emailText.requestFocus();


                }
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailText.getText().toString();
                String pwd = passwordText.getText().toString();
                if (TextUtils.isEmpty(email) || !email.contains("@uw.edu")) {
                    Toast.makeText(v.getContext(), "Enter valid email address"
                            , Toast.LENGTH_SHORT).show();
                    emailText.requestFocus();
                } else if (TextUtils.isEmpty(pwd) || pwd.length() < 6) {
                    Toast.makeText(v.getContext(), "Enter valid password (at least 6 characters)"
                            , Toast.LENGTH_SHORT).show();
                    passwordText.requestFocus();
                } else {

                    mAuth.signInWithEmailAndPassword(emailText.getText().toString(), passwordText.getText().toString())
                            .addOnCompleteListener(LogInFragment.this.getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    // if account has been created.
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), "successful login", Toast.LENGTH_SHORT).show();
                                        FirebaseUser user = mAuth.getCurrentUser();



                                        // if email is verified, go to next activity.
                                        assert user != null;
                                        if (user.isEmailVerified()) {

                                            Intent i = new Intent(getActivity(), HomeActivity.class);

                                            startActivity(i);
                                            getActivity().finish();
                                            //mLoginFragmentListener.login(emailText.getText().toString(),
                                                   // passwordText.getText().toString());
                                        } else { // login is successful, but account is not verified.
                                            Toast.makeText(getContext(), "Please verify account with email", Toast.LENGTH_SHORT).show();
                                        }
                                        // if sign in fails, display message to the user
                                    } else {
                                        Log.w("login", "signInWithEmail:failure", task.getException());
                                        Toast.makeText(getContext(), "" + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                                    // if sign in fails, display message to the user                   
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Failed to login", Toast.LENGTH_SHORT).show();
                            Log.e("LogInFragment", e.toString());
                        }
                    });
                }
            }
        });


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogInFragment.this.getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.sign_in_fragment_id, new RegisterFragment(), "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });
        registerButton.setOnClickListener(v -> getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.sign_in_fragment_id, new RegisterFragment(), "findThisFragment")
                .addToBackStack(null)
                .commit());
        return view;
    };
}




