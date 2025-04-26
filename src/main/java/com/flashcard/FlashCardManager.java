package com.flashcard;

import java.io.*;
import java.util.*;

public class FlashCardManager {

    public static List<Card> loadCards(String filename) throws IOException {
        List<Card> cards = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\\|");
            if (parts.length == 2) {
                cards.add(new Card(parts[0].trim(), parts[1].trim()));
            }
        }
        reader.close();
        return cards;
    }
}
