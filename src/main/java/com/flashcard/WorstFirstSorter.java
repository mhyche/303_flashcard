package com.flashcard;

import java.util.*;

public class WorstFirstSorter implements CardOrganizer {

    @Override
    public List<Card> organize(List<Card> cards) {
        List<Card> sorted = new ArrayList<>(cards);

        sorted.sort((c1, c2) -> Integer.compare(c2.getWrongCount(), c1.getWrongCount()));

        return sorted;
    }
}
