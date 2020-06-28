package com.example.quizappmvvm.viewmodel.repo;

import androidx.annotation.NonNull;

import com.example.quizappmvvm.callback.OnFirestoreTaskComplete;
import com.example.quizappmvvm.model.QuizModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class QuizListRepository {
    private OnFirestoreTaskComplete taskComplete;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference quizCollectionReference = firestore.collection("QuestionsList");

    public QuizListRepository(OnFirestoreTaskComplete taskComplete) {
        this.taskComplete = taskComplete;
    }

    public void getQuizData(){
        quizCollectionReference.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){

                taskComplete.onQuizListDataAdded(Objects.requireNonNull(task.getResult()).toObjects(QuizModel.class));

            }else{
                taskComplete.onError(task.getException());
            }
        });
    }
}
