package com.example.quizappmvvm.view.quiz;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
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
public class QuizFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "TaskLog";
    private FirebaseFirestore firestore;
    private String quizId;
    private FragmentQuizBinding quizBinding;
    private List<QuestionsModel> questionsList;
    private List<QuestionsModel> questionsToAnswerList;
    private long totalQuestionsToAnswer = 10;
    private CountDownTimer counterDown;
    private boolean canAnswer = false;
    private int currentQuestion = 0;

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
                      loadUi();
                  }else{
                      quizBinding.loadingQuizTv.setText("Error Loading Data!");
                  }
    
              });

      quizBinding.firstOptionBtn.setOnClickListener(this);
      quizBinding.secondOptionBtn.setOnClickListener(this);
      quizBinding.thirdOptionBtn.setOnClickListener(this);

    }

    private void loadUi() {
        quizBinding.questionNumberValue.setText("1");
        quizBinding.loadingQuizTv.setText("Quiz questions loaded");

        enableOptions();

        loadQuestions(1);
    }

    private void loadQuestions(int questionNumber){
        quizBinding.questionTv.setText(questionsToAnswerList.get(questionNumber).getQuestion());
        quizBinding.firstOptionBtn.setText(questionsToAnswerList.get(questionNumber).getOption_a());
        quizBinding.secondOptionBtn.setText(questionsToAnswerList.get(questionNumber).getOption_b());
        quizBinding.thirdOptionBtn.setText(questionsToAnswerList.get(questionNumber).getOption_c());

        canAnswer = true;

        currentQuestion = questionNumber;
        //starting count down timer
        startTimer(questionNumber);
    }

    private void startTimer(int questionNumber) {

        quizBinding.questionPeriodProgressBar.setVisibility(View.VISIBLE);
        long timeToAnswer = questionsToAnswerList.get(questionNumber).getTimer();
        quizBinding.questionPeriodSeconds.setText(timeToAnswer+"");


        counterDown = new CountDownTimer(timeToAnswer * 1000, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                quizBinding.questionPeriodSeconds.setText(String.valueOf(millisUntilFinished /1000));

                long percent = millisUntilFinished /(timeToAnswer*10);
                quizBinding.questionPeriodProgressBar.setProgress((int)percent);

            }

            @Override
            public void onFinish() {

                canAnswer = false;
            }
        }.start();



    }

    private void enableOptions(){
        quizBinding.firstOptionBtn.setVisibility(View.VISIBLE);
        quizBinding.secondOptionBtn.setVisibility(View.VISIBLE);
        quizBinding.thirdOptionBtn.setVisibility(View.VISIBLE);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.first_option_btn:
                checkAnswer(quizBinding.firstOptionBtn.getText().toString());
                break;
            case R.id.second_option_btn:
                checkAnswer(quizBinding.secondOptionBtn.getText().toString());
                break;

            case R.id.third_option_btn:
                checkAnswer(quizBinding.thirdOptionBtn.getText().toString());
                break;
        }
    }

    private void checkAnswer(String selectedAnswer){
        String answer = questionsToAnswerList.get(currentQuestion).getAnswer();

        if(canAnswer){
            if(answer.equals(selectedAnswer)){

                Log.d("Check answer", "Correct Answer");

            }else{

                Log.d("Check answer", "Wrong Answer");
            }
        }

    }
}
