package com.example.quizappmvvm.view.quiz;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.quizappmvvm.R;
import com.example.quizappmvvm.databinding.FragmentQuizBinding;
import com.example.quizappmvvm.model.QuestionsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class QuizFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "TaskLog";
    private NavController navController;
    private FirebaseFirestore firestore;
    private String quizId;
    private String quizName;
    private FragmentQuizBinding quizBinding;
    private List<QuestionsModel> questionsList;
    private List<QuestionsModel> questionsToAnswerList;
    private long totalQuestionsToAnswer = 10;
    private CountDownTimer counterDown;
    private boolean canAnswer = false;
    private int currentQuestion = 0;
    private int correctAnswers = 0;
    private int wrongAnswers = 0;
    private int notAnsweredQuestions = 0;
    private FirebaseAuth firebaseAuth;

    private String currentUserId;


    public QuizFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment with view binding
         quizBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_quiz, container, false);
         firebaseAuth = FirebaseAuth.getInstance();

         if(firebaseAuth.getCurrentUser() != null){
             currentUserId = firebaseAuth.getCurrentUser().getUid();
         }
        return quizBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
        navController = Navigation.findNavController(view);

        questionsList = new ArrayList<>();
        questionsToAnswerList = new ArrayList<>();

        //getting quiz id
        assert getArguments() != null;
        quizId = QuizFragmentArgs.fromBundle(getArguments()).getQuizId();
        //getting the total question for each quiz
        totalQuestionsToAnswer = QuizFragmentArgs.fromBundle(getArguments()).getTotalQuestions();

        //getting quiz name
        quizName = QuizFragmentArgs.fromBundle(getArguments()).getQuizName();

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
      quizBinding.nextQuestionBtn.setOnClickListener(this);
      quizBinding.closeQuizBtn.setOnClickListener(this);

    }

    private void loadUi() {
        quizBinding.questionNumberValue.setText("1");
        quizBinding.loadingQuizTv.setText(quizName +" loaded");

        enableOptions();

        loadQuestions(1);
    }

    private void loadQuestions(int questionNumber){
        quizBinding.questionTv.setText(questionsToAnswerList.get(questionNumber-1).getQuestion());
        quizBinding.firstOptionBtn.setText(questionsToAnswerList.get(questionNumber-1).getOption_a());
        quizBinding.secondOptionBtn.setText(questionsToAnswerList.get(questionNumber-1).getOption_b());
        quizBinding.thirdOptionBtn.setText(questionsToAnswerList.get(questionNumber-1).getOption_c());

        canAnswer = true;

        currentQuestion = questionNumber;
        //starting count down timer
        startTimer(questionNumber);
    }

    private void startTimer(int questionNumber) {

        quizBinding.questionPeriodProgressBar.setVisibility(View.VISIBLE);
        long timeToAnswer = questionsToAnswerList.get(questionNumber-1).getTimer();
        quizBinding.questionPeriodSeconds.setText(timeToAnswer+"");


        counterDown = new CountDownTimer(timeToAnswer * 1000, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                quizBinding.questionPeriodSeconds.setText(String.valueOf(millisUntilFinished /1000));

                Long percent = millisUntilFinished /(timeToAnswer*10);
                quizBinding.questionPeriodProgressBar.setProgress(percent.intValue());

            }
            @Override
            public void onFinish() {
                canAnswer = false;
                notAnsweredQuestions++;
                quizBinding.verifyingTv.setText(getResources().getString(R.string.timeup));
                quizBinding.verifyingTv.setTextColor(getResources().getColor(R.color.wrong_answer_red));
                showNextBtn();
            }
        };
        counterDown.start();
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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.first_option_btn:
                verifyAnswer(quizBinding.firstOptionBtn);
                break;
            case R.id.second_option_btn:
                verifyAnswer(quizBinding.secondOptionBtn);
                break;
            case R.id.third_option_btn:
                verifyAnswer(quizBinding.thirdOptionBtn);
                break;
            case R.id.next_question_btn:
                if(currentQuestion == totalQuestionsToAnswer){
                    //submit results
                    submitResults();
                }else{
                    currentQuestion++;
                    loadQuestions(currentQuestion);
                    resetOptions();
                }
                break;
            case R.id.close_quiz_btn:
                navController.navigate(R.id.action_quizFragment_to_detailsFragment);
        }
    }

    private void submitResults() {
        HashMap<String, Object>results = new HashMap<>();
        results.put("correct", correctAnswers);
        results.put("wrong", wrongAnswers);
        results.put("missed", notAnsweredQuestions);
        firestore.collection("QuestionsList").document(quizId).collection("Results").document(currentUserId)
                .set(results).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        //navigate to results fragment
                        QuizFragmentDirections.ActionQuizFragmentToResultFragment action = QuizFragmentDirections.actionQuizFragmentToResultFragment();
                        action.setQuizId(quizId);
                        navController.navigate(action);
                    }else {
                        Toast.makeText(getActivity(), "An Error Occurred ! ", Toast.LENGTH_LONG).show();
                    }
                });
    }


    private void verifyAnswer(Button selectedButton){
        String answer = questionsToAnswerList.get(currentQuestion-1).getAnswer();

        if(canAnswer){
           // selectedButton.setTextColor(getResources().getColor(R.color.colorLightText));
            if(answer.equals(selectedButton.getText().toString())){
                selectedButton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                selectedButton.setBackground(getResources().getDrawable(R.drawable.correct_answer_btn_background, null));
                colorizeVerifyingTV(true);
                correctAnswers++;
            }else{
                selectedButton.setBackground(getResources().getDrawable(R.drawable.wrong_answer_btn_background, null));
                colorizeVerifyingTV(false);
                wrongAnswers++;
            }

            canAnswer = false;
            counterDown.cancel();
            showNextBtn();
        }

    }

    private void showNextBtn() {
        if(currentQuestion == totalQuestionsToAnswer){
            quizBinding.nextQuestionBtn.setText(R.string.submit);
        }
        quizBinding.nextQuestionBtn.setEnabled(true);
        quizBinding.nextQuestionBtn.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        quizBinding.verifyingTv.setVisibility(View.VISIBLE);
    }

    private void colorizeVerifyingTV(boolean state){
        if(state){
            quizBinding.verifyingTv.setText("Correct Answer");
            quizBinding.verifyingTv.setTextColor(getResources().getColor(R.color.correct_answer_green));
        }else{
            quizBinding.verifyingTv.setText("Wrong Answer");
            quizBinding.verifyingTv.setTextColor(getResources().getColor(R.color.wrong_answer_red));
        }
    }

    private void resetOptions() {
        quizBinding.questionNumberValue.setText(currentQuestion+"");
        quizBinding.firstOptionBtn.setBackground(getResources().getDrawable(R.drawable.quiz_options_btn_background));
        quizBinding.secondOptionBtn.setBackground(getResources().getDrawable(R.drawable.quiz_options_btn_background));
        quizBinding.thirdOptionBtn.setBackground(getResources().getDrawable(R.drawable.quiz_options_btn_background));

        quizBinding.firstOptionBtn.setTextColor(getResources().getColor(R.color.colorLightText));
        quizBinding.secondOptionBtn.setTextColor(getResources().getColor(R.color.colorLightText));
        quizBinding.thirdOptionBtn.setTextColor(getResources().getColor(R.color.colorLightText));



        quizBinding.verifyingTv.setVisibility(View.INVISIBLE);
        quizBinding.nextQuestionBtn.setEnabled(false);
        quizBinding.nextQuestionBtn.setTextColor(getResources().getColor(R.color.colorLightText));

    }
}
