package com.flashcard;

import java.util.List;

public class AchievementTracker {

    public static boolean allCorrect(List<Card> cards) {
        return cards.stream().allMatch(card -> card.getWrongCount() == 0);
    }

    public static boolean repeatedMoreThanFiveTimes(Card card) {
        return (card.getCorrectCount() + card.getWrongCount()) > 5;
    }

    public static boolean confident(Card card) {
        return card.getCorrectCount() >= 3;
    }
}