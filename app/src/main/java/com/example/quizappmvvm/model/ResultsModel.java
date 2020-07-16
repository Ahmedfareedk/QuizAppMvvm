package com.example.quizappmvvm.model;

public class ResultsModel {
    private long correct, missed, wrong;

    public ResultsModel() {
    }

    public ResultsModel(long correct, long missed, long wrong) {
        this.correct = correct;
        this.missed = missed;
        this.wrong = wrong;
    }

    public long getCorrect() {
        return correct;
    }

    public long getMissed() {
        return missed;
    }

    public long getWrong() {
        return wrong;
    }
}
