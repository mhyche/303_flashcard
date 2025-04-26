package com.flashcard;

import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class WorstFirstSorterTest {

    @Test
    void testWorstFirstOrdering() {
        Card card1 = new Card("Q1", "A1");
        Card card2 = new Card("Q2", "A2");
        Card card3 = new Card("Q3", "A3");

        card1.answeredWrong();
        card2.answeredWrong();
        card2.answeredWrong();
        card2.answeredWrong();
        card3.answeredWrong();
        card3.answeredWrong();

        List<Card> cards = Arrays.asList(card1, card2, card3);
        Collections.shuffle(cards);

        WorstFirstSorter sorter = new WorstFirstSorter();
        List<Card> sorted = sorter.organize(cards);

        assertEquals("Q2", sorted.get(0).getQuestion());
        assertEquals("Q3", sorted.get(1).getQuestion());
        assertEquals("Q1", sorted.get(2).getQuestion());
    }
}
