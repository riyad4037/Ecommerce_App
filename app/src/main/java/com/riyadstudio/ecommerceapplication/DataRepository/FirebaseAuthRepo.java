package com.riyadstudio.ecommerceapplication.DataRepository;

import android.content.Context;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.riyadstudio.ecommerceapplication.R;

public class FirebaseAuthRepo {

    private Context context;

    private FirebaseAuth firebaseAuth;
    private BeginSignInRequest signInRequest;
    private SignInClient oneTapClient;

    private FirebaseDatabase firebaseDatabase;

    private FirebaseStorage firebaseStorage;

    public FirebaseAuthRepo(Context context) {
        this.context = context;
    }

    public FirebaseAuth getFirebaseAuth(){
        if (firebaseAuth == null) {
            firebaseAuth = FirebaseAuth.getInstance();
        }
        return firebaseAuth;
    }


    public FirebaseDatabase getFirebaseDatabase() {
        if(firebaseDatabase == null){
            firebaseDatabase = FirebaseDatabase.getInstance();
        }
        return firebaseDatabase;
    }

    public FirebaseStorage getStorageInstance() {
        if(firebaseStorage == null){
            firebaseStorage = FirebaseStorage.getInstance();
        }
        return firebaseStorage;
    }

    private void googleSignInInitialization(){
        signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(String.valueOf(R.string.web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();

        oneTapClient = Identity.getSignInClient(context);
    }


}
