package com.example.quizappmvvm.callback;

import com.example.quizappmvvm.model.QuizModel;

import java.util.List;

public interface OnFirestoreTaskComplete {
    void onQuizListDataAdded(List<QuizModel> quizList);
    void onError(Exception error);
}
