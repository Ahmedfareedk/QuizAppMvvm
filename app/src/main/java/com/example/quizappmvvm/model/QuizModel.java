package com.example.quizappmvvm.model;

import com.google.firebase.firestore.DocumentId;

public class QuizModel {
    @DocumentId
    private String quizId;

    private String name, image, level, desc, visibility;
    private long questions;

    public QuizModel() {}

    public QuizModel(String quizId, String name, String image, String level, String desc, String visibility, long numOfQuestions) {
        this.quizId = quizId;
        this.name = name;
        this.image = image;
        this.level = level;
        this.desc = desc;
        this.visibility = visibility;
        this.questions = numOfQuestions;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public long getQuestions() {
        return questions;
    }

    public void setQuestions(long questions) {
        this.questions = questions;
    }


}
