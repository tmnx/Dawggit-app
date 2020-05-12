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
 * This fragment is responsible for adding new users to the database.
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {
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
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
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
     * Renders the fragment_register view.
     * Validates registration information.
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
        getActivity().setTitle("Register a New Account");
        mRegisterFragmentListener = (RegisterFragmentListener) getActivity();
        final EditText username = view.findViewById(R.id.et_register_username);
        final EditText email = view.findViewById(R.id.et_register_email);
        final EditText pwd = view.findViewById(R.id.et_register_password);
        Button registerButton = view.findViewById(R.id.button_register_confirm);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameText = username.getText().toString();
                String emailText = email.getText().toString();
                String pwdText = pwd.getText().toString();
                if(TextUtils.isEmpty(emailText) || !emailText.contains("uw.edu")) {
                    Toast.makeText(v.getContext(), "Enter a valid uw email address"
                            , Toast.LENGTH_SHORT).show();
                    email.requestFocus();
                }
                else if(TextUtils.isEmpty(pwdText) || pwdText.length() < 6) {
                    Toast.makeText(v.getContext(), "Enter valid password (at least 6 characters)"
                            , Toast.LENGTH_SHORT).show();
                    pwd.requestFocus();
                }
                else {
                    mRegisterFragmentListener.registerNewAccount(usernameText, emailText, pwdText);
                }
            }
        });
        return view;
    }
}
