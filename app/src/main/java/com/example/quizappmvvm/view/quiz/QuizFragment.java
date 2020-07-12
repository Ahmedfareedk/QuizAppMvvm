package com.example.quizappmvvm.view.quiz;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.quizappmvvm.R;
import com.example.quizappmvvm.databinding.FragmentQuizBinding;
import com.example.quizappmvvm.model.QuestionsModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class QuizFragment extends Fragment {
    private static final String TAG = "TaskLog";
    private FirebaseFirestore firestore;
    private String quizId;
    private FragmentQuizBinding quizBinding;
    private List<QuestionsModel> questionsList;
    private List<QuestionsModel> questionsToAnswerList;
    private long totalQuestionsToAnswer = 10;

    public QuizFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment with view binding
         quizBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_quiz, container, false);
      
        return quizBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
        questionsList = new ArrayList<>();
        questionsToAnswerList = new ArrayList<>();

        //getting quiz id
        assert getArguments() != null;
        quizId = QuizFragmentArgs.fromBundle(getArguments()).getQuizId();
        //getting the total question for each quiz
        totalQuestionsToAnswer = QuizFragmentArgs.fromBundle(getArguments()).getTotalQuestions();

      firestore.collection("QuestionsList").document(quizId)
              .collection("Questions")
              .get()
              .addOnCompleteListener(task -> {
                  if(task.isSuccessful()){
                      questionsList = task.getResult().toObjects(QuestionsModel.class);
                      pickQuestion();
                  }else{
                      quizBinding.loadingQuizTv.setText("Error Loading Data!");
                  }
    
              });
    }


    private void pickQuestion(){

        for(int i = 0; i < totalQuestionsToAnswer; i++){
            int randomIndex = randomIndex(questionsList.size(), 0);
            questionsToAnswerList.add(questionsList.get(randomIndex));
            questionsList.remove(randomIndex);
            Log.i(TAG, questionsToAnswerList.get(i).getQuestion());
        }
    }

    private int randomIndex(int max, int min){
        return  ((int) (Math.random() * (max - min))) + min;
    }


}
