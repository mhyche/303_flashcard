package com.flashcard;

import java.util.*;

public class RecentMistakesFirstSorter implements CardOrganizer {

    @Override
    public List<Card> organize(List<Card> cards) {
        List<Card> sorted = new ArrayList<>(cards);

        sorted.sort((c1, c2) -> {
            if (c1.getLastWrongTime() == 0 && c2.getLastWrongTime() == 0) {
                return 0;
            }
            if (c1.getLastWrongTime() == 0) {
                return 1;
            }
            if (c2.getLastWrongTime() == 0) {
                return -1;
            }
            return Long.compare(c2.getLastWrongTime(), c1.getLastWrongTime());
        });

        return sorted;
    }
}
