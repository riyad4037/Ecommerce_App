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
import android.widget.Button;
import android.widget.ImageButton;
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


public class SignupFragment extends Fragment {

    private TextInputEditText signupUserNameEditText;
    private TextInputEditText signupEmailAddressEditText;
    private TextInputEditText signupPasswordEditText;

    private Button signupButtonTextView;

    private ImageButton continueWithFacebookTextViewSignupPage;
    private ImageButton continueWithGoogleTextViewSignupPage;

    private FirebaseAuth firebaseAuth;

    private CallbackManager callbackManager;

    private String TAG = "Signup Fragment Page";

    private BeginSignInRequest signInRequest;
    private SignInClient oneTapClient;

    private FirebaseDatabase firebaseDatabase;

    public SignupFragment() {
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
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        signupUserNameEditText = view.findViewById(R.id.SignupUserNameEditText);
        signupEmailAddressEditText = view.findViewById(R.id.SignupEmailAddressEditText);
        signupPasswordEditText = view.findViewById(R.id.SignupPasswordEditText);
        signupButtonTextView = view.findViewById(R.id.SignupButtonTextView);
        continueWithFacebookTextViewSignupPage = view.findViewById(R.id.ContinueWithFacebookTextViewSignupPage);
        continueWithGoogleTextViewSignupPage = view.findViewById(R.id.ContinueWithGoogleTextViewSignupPage);

        //Signup Button
        signupButtonTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = signupEmailAddressEditText.getText().toString();
                String password = signupPasswordEditText.getText().toString();
                String name = signupUserNameEditText.getText().toString();

                if (email == null | password == null) {
                    Toast.makeText(getActivity(), "Email and Password can not be empty.", Toast.LENGTH_SHORT).show();
                } else {
                    firebaseAuth.createUserWithEmailAndPassword(signupEmailAddressEditText.getText().toString(), signupPasswordEditText.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {

                                    User newUser = new User(name, email);

                                    DatabaseReference databaseReference = firebaseDatabase.getReference().child("users_information");
                                    databaseReference.setValue(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            LoginFragment loginFragment = new LoginFragment();
                                            getActivity().getSupportFragmentManager().beginTransaction()
                                                    .replace(R.id.AuthFrameLayout, loginFragment)
                                                    .addToBackStack(null)
                                                    .commit();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.wtf(TAG, "Failure in data insertion: " + e.getLocalizedMessage());
                                        }
                                    });


                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "Login Failed! " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        //Facebook Login

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG,"facebook: onSuccess: "+ loginResult);
                        AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                        firebaseAuth.signInWithCredential(credential)
                                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in User's information
                                            Log.d(TAG, "signInWithCredential:success");
                                            insertUserInfoIntoRTDB();
                                        } else {
                                            // If sign in fails, display a message to the User.
                                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                                            Toast.makeText(getActivity(), "Authentication failed.",Toast.LENGTH_SHORT).show();
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
                        Log.d(TAG, "Facebook Exception: "+e.getLocalizedMessage());

                    }
                });

        continueWithFacebookTextViewSignupPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("public_profile"));
            }
        });

        // Google SignIn

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
                        Log.e(TAG, "Error in ResultLauncher: "+e.getLocalizedMessage());
                    }

                }
            }
        });

        continueWithGoogleTextViewSignupPage.setOnClickListener(new View.OnClickListener() {
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
                                    Log.e(TAG, "Error in Intent Sender: "+e.getLocalizedMessage());
                                }

                            }
                        })
                        .addOnFailureListener(getActivity(), new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Error in Begin Signin: "+e.getLocalizedMessage());
                            }
                        });
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