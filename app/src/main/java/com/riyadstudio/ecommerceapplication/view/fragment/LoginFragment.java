package com.riyadstudio.ecommerceapplication.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.riyadstudio.ecommerceapplication.DataRepository.FirebaseAuthRepo;
import com.riyadstudio.ecommerceapplication.R;
import com.riyadstudio.ecommerceapplication.model.User;
import com.riyadstudio.ecommerceapplication.view.MainActivity;

import java.util.Arrays;


public class LoginFragment extends Fragment {

    private TextInputEditText EmailEditText;
    private TextInputEditText passwordEditText;

    private CheckBox rememberMe;
    private TextView forgotPasswordTextView;
    private MaterialButton LoginButton;
    private MaterialButton NavigateToSignUpButton;

    private ImageButton signInWithGoogle;
    private ImageButton signInWithFacebook;
    private FirebaseAuth firebaseAuth;
    private SignInClient oneTapClient;

    private BeginSignInRequest signInRequest;
    private String TAG = "Login Fragment";

    private CallbackManager callbackManager;

    private FirebaseDatabase firebaseDatabase;


    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = new FirebaseAuthRepo(getActivity()).getFirebaseAuth();
        firebaseDatabase = new FirebaseAuthRepo(getActivity()).getFirebaseDatabase();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        EmailEditText = view.findViewById(R.id.EmailAddressEditText);
        passwordEditText = view.findViewById(R.id.PasswordEditText);
        rememberMe = view.findViewById(R.id.RememberMeCheckBoxLoginPage);
        forgotPasswordTextView = view.findViewById(R.id.ForgetPasswordLoginPageTextView);
        LoginButton = view.findViewById(R.id.LogInButtonTextView);
        NavigateToSignUpButton = view.findViewById(R.id.SignupButtonLoginPageTextView);
        signInWithGoogle = view.findViewById(R.id.ContinueWithGoogleTextViewLoginPage);
        signInWithFacebook = view.findViewById(R.id.ContinueWithFacebookTextViewLoginPage);


        //Google Login
        signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();

        oneTapClient = Identity.getSignInClient(getActivity());

        ActivityResultLauncher<IntentSenderRequest> intentSenderRequest = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    try {
                        SignInCredential signInCredential = oneTapClient.getSignInCredentialFromIntent(result.getData());
                        String idToken = signInCredential.getGoogleIdToken();
                        if (idToken != null) {
                            AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                            firebaseAuth.signInWithCredential(firebaseCredential)
                                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                insertUserInfoIntoRTDB();
                                            } else {
                                                Log.w(TAG, "signInWithCredential:failure", task.getException());
                                            }
                                        }
                                    });
                            Toast.makeText(getActivity(), "Email: " + signInCredential.getId(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (ApiException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Error in ResultLauncher: " + e.getLocalizedMessage());
                    }

                }
            }
        });

        signInWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oneTapClient.beginSignIn(signInRequest)
                        .addOnSuccessListener(getActivity(), new OnSuccessListener<BeginSignInResult>() {
                            @Override
                            public void onSuccess(BeginSignInResult beginSignInResult) {
                                try {
                                    IntentSenderRequest intentSender = new IntentSenderRequest.Builder(beginSignInResult.getPendingIntent().getIntentSender()).build();
                                    intentSenderRequest.launch(intentSender);
                                } catch (Exception e) {
                                    Log.e(TAG, "Error in Intent Sender: " + e.getLocalizedMessage());
                                }

                            }
                        })
                        .addOnFailureListener(getActivity(), new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Error in Begin Signin: " + e.getLocalizedMessage());
                            }
                        });
            }
        });

        //facebook Login
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG, "facebook: onSuccess: " + loginResult);
                        AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                        firebaseAuth.signInWithCredential(credential)
                                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in User's information
                                            insertUserInfoIntoRTDB();
                                        } else {
                                            // If sign in fails, display a message to the User.
                                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                                            Toast.makeText(getActivity(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                                            getActivity().finish();
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "Facebook login Canceled");
                    }

                    @Override
                    public void onError(@NonNull FacebookException e) {
                        Log.d(TAG, "Facebook Exception: " + e.getLocalizedMessage());

                    }
                });

        signInWithFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("public_profile"));
            }
        });

        NavigateToSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignupFragment signupFragment = new SignupFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.AuthFrameLayout, signupFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ForgetPasswordFragment forgetPasswordFragment = new ForgetPasswordFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.AuthFrameLayout, forgetPasswordFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailText = EmailEditText.getText().toString().trim();
                String passwordText = passwordEditText.getText().toString().trim();
                if (emailText.isEmpty() || passwordText.isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter the password and email.", Toast.LENGTH_SHORT).show();
                } else {
                    firebaseAuth.signInWithEmailAndPassword(emailText, passwordText)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "Email or Password is not correct;", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });


        return view;
    }

    private void insertUserInfoIntoRTDB() {

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String name = currentUser.getDisplayName();
        String email = currentUser.getEmail();
        String pp = String.valueOf(currentUser.getPhotoUrl());

        User newUser = new User(name, email);
        newUser.setProfilePictureUrl(pp);

        DatabaseReference databaseReference = firebaseDatabase.getReference().child("users_information");
        databaseReference.child(currentUser.getUid()).setValue(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "signInWithCredential:success");
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.wtf(TAG, "Failure in data insertion: " + e.getLocalizedMessage());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}