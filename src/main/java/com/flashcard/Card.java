package com.flashcard;

public class Card {
    private String question;
    private String answer;
    private int correctCount = 0;
    private int wrongCount = 0;
    private long lastAnsweredTime = 0;
    private long lastWrongTime = 0;

    public Card(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public void answeredCorrectly() {
        correctCount++;
        lastAnsweredTime = System.currentTimeMillis();
    }

    public void answeredWrong() {
        wrongCount++;
        lastAnsweredTime = System.currentTimeMillis();
        lastWrongTime = System.currentTimeMillis();
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public int getWrongCount() {
        return wrongCount;
    }

    public long getLastAnsweredTime() {
        return lastAnsweredTime;
    }

    public long getLastWrongTime() {
        return lastWrongTime;
    }
}
