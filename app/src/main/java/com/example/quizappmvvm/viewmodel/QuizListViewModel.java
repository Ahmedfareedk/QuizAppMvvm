package com.example.quizappmvvm.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.quizappmvvm.callback.OnFirestoreTaskComplete;
import com.example.quizappmvvm.model.QuizModel;
import com.example.quizappmvvm.viewmodel.repo.QuizListRepository;

import java.util.List;

public class QuizListViewModel extends ViewModel implements OnFirestoreTaskComplete {

    private MutableLiveData<List<QuizModel>> liveData = new MutableLiveData<>();
    private QuizListRepository repository = new QuizListRepository(this);

    public QuizListViewModel(){
        repository.getQuizData();
    }

    @Override
    public void onQuizListDataAdded(List<QuizModel> quizList) {
        liveData.setValue(quizList);
    }

    @Override
    public void onError(Exception error) {

    }

    public LiveData<List<QuizModel>> getLiveData() {
        return liveData;
    }
}
