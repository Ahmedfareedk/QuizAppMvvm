package com.example.quizappmvvm;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class SplashFragment extends Fragment {

    private ProgressBar progressBar;
    private TextView initTextView;
    private FirebaseAuth firebaseAuth;
    private NavController navController;
    private static final String SPLASH_TAG = "TAG";
    public SplashFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = view.findViewById(R.id.splash_progress);
        initTextView = view.findViewById(R.id.initializing);
        firebaseAuth = FirebaseAuth.getInstance();
        navController = Navigation.findNavController(view);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if(currentUser == null){
            initTextView.setText("Creating Account...");
            firebaseAuth.signInAnonymously().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    initTextView.setText("Account Created");
                    navController.navigate(R.id.action_splashFragment_to_listFragment);
                }else{
                    Log.d(SPLASH_TAG, "Splash Log: " + task.getException().getLocalizedMessage());
                }
            });
        }else{
            initTextView.setText("Logged in");
            navController.navigate(R.id.action_splashFragment_to_listFragment);
        }
    }
}