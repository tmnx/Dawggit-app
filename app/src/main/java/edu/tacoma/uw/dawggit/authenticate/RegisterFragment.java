/*
 * TCSS 450 - Spring 2020
 * Dawggit
 * Team 6: Codie Bryan, Kevin Bui, Minh Nguyen, Sean Smith
 */
package edu.tacoma.uw.dawggit.authenticate;

import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import edu.tacoma.uw.dawggit.R;

/**
 * This fragment is responsible for adding new users to the database.
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 * @author Kevin Bui
 * @author Codie Bryan
 */
public class RegisterFragment extends Fragment {

    /**Used for firebase authentication.*/
    private FirebaseAuth mAuth;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    /**Allows information to be passed from this fragment back into SignInActivity*/
    private RegisterFragmentListener mRegisterFragmentListener;

    /**Allows information to be passed from this fragment back into SignInActivity*/
    public interface RegisterFragmentListener {
        void registerNewAccount(String username,
                                       String email,
                                       String password);
    }

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Initializes firebase
     * @param savedInstanceState null
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    /**
     * Renders the fragment_register view.
     * Validates registration information through firebase.
     * Retrieves registration information, then passes it back into SignInActivity.
     * @param inflater fragment_register.xml
     * @param container SignInActivity
     * @param savedInstanceState Null
     * @return a View
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view =  inflater.inflate(R.layout.fragment_register, container, false);
        Objects.requireNonNull(getActivity()).setTitle("Register a New Account");
        mRegisterFragmentListener = (RegisterFragmentListener) getActivity();
        final EditText username = view.findViewById(R.id.et_register_username);
        final EditText email = view.findViewById(R.id.et_register_email);
        final EditText pwd = view.findViewById(R.id.et_register_password);



        Button registerButton = view.findViewById(R.id.button_register_confirm);
        registerButton.setOnClickListener(v -> {
            final String usernameText = username.getText().toString();
            final String emailText = email.getText().toString();
            final String pwdText = pwd.getText().toString();
            if (TextUtils.isEmpty(emailText) || !emailText.contains("uw.edu")) {
                Toast.makeText(v.getContext(), "Enter a valid uw email address"
                        , Toast.LENGTH_SHORT).show();
                email.requestFocus();
            } else if (TextUtils.isEmpty(pwdText) || pwdText.length() < 6) {
                Toast.makeText(v.getContext(), "Enter valid password (at least 6 characters)"
                        , Toast.LENGTH_SHORT).show();
                pwd.requestFocus();
            } else if (usernameText.length() > 20) {
                Toast.makeText(v.getContext(),
                        "Username is too long, only 20 characters", Toast.LENGTH_SHORT).show();
                username.requestFocus();
            } else if (email.length() > 30) {
                Toast.makeText(v.getContext(),
                        "Email is too long, only 30 characters", Toast.LENGTH_SHORT).show();
                email.requestFocus();
            } else if ( pwdText.length() > 255) {
                Toast.makeText(v.getContext(),
                        "Password is too long, only 255 characters", Toast.LENGTH_SHORT).show();
            } else {

                mAuth.createUserWithEmailAndPassword(emailText, pwdText)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("Tag", "{createUserWithEmail:success}");
                                    Toast.makeText(v.getContext(), "Please verify account from email.",
                                            Toast.LENGTH_SHORT).show();
                                    mRegisterFragmentListener.registerNewAccount(usernameText, emailText, pwdText);
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    user.sendEmailVerification()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(v.getContext(), "Email verification sent",
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("tag", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(v.getContext(), "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    //updateUI(null);
                                }
                            }
                        });
            }
        });
        return view;
    }
}
