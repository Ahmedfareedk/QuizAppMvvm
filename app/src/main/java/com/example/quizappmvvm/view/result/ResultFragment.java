package com.example.quizappmvvm.view.result;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.quizappmvvm.R;
import com.example.quizappmvvm.databinding.FragmentResultBinding;
import com.example.quizappmvvm.model.ResultsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.FirestoreRegistrar;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ResultFragment extends Fragment {

    private String quizId;
    private FirebaseFirestore firestore;
    private List<ResultsModel> resultsList;
    private FragmentResultBinding resultBinding;
    private String currentUserId;
    private FirebaseAuth firebaseAuth;
    private DocumentSnapshot result;
    private NavController navController;



    public ResultFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        resultBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_result, container, false);
        // Inflate the layout for this fragment
        return resultBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
        assert getArguments() != null;
        firebaseAuth = FirebaseAuth.getInstance();
        navController = Navigation.findNavController(view);

        if(firebaseAuth.getCurrentUser() != null) {
            currentUserId = firebaseAuth.getCurrentUser().getUid();
        }
        quizId = ResultFragmentArgs.fromBundle(getArguments()).getQuizId();

        resultBinding.gotoHomeBtn.setOnClickListener(v -> navController.navigate(R.id.action_resultFragment_to_listFragment));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        firestore.collection("QuestionsList").document(quizId).collection("Results")
                .document(currentUserId).get().addOnCompleteListener(task -> {
                   if(task.isSuccessful()){
                        result = task.getResult();
                        fetchResultsToViews();
                   }else{
                       Toast.makeText(getContext(), "Error Occurred", Toast.LENGTH_SHORT).show();
                   }
                });



    }

    private void fetchResultsToViews() {
        Long correct =result.getLong("correct");
        Long wrong =result.getLong("wrong");
        Long missed =result.getLong("missed");


        Long total = correct + wrong + missed;
        Long percent = (correct * 100) / total;

        resultBinding.correctAnswersValue.setText(correct.toString());
        resultBinding.wrongAnswersValue.setText(wrong.toString());
        resultBinding.missedAnswersValue.setText(missed.toString());

        resultBinding.progressBarText.setText(percent +"%");
        resultBinding.resultProgressBar.setProgress(percent.intValue());

    }

}
