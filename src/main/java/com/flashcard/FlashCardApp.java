package com.flashcard;

import java.util.*;
import java.io.*;

public class FlashCardApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static List<Card> cards = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        System.out.println("Welcome to Flashcards App! ");

        while (true) {
            System.out.println("What do you want to do?");
            System.out.println("1. Study");
            System.out.println("2. Edit my collections");
            System.out.println("3. Quit");
            System.out.print("Choose an option (1-3): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    studyMenu();
                    break;
                case "2":
                    editCollections();
                    break;
                case "3":
                    System.out.println("Bye bye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please enter 1, 2, or 3.");
            }
        }
    }

    private static void studyMenu() throws IOException {
        System.out.print("Enter flashcards filename: ");
        String filename = scanner.nextLine();
        cards = FlashCardManager.loadCards(filename);

        String order = getOption("Order (random / worst-first / recent-mistakes-first): ", Arrays.asList("random", "worst-first", "recent-mistakes-first"));
        int repetitions = getIntOption("How many repetitions? ", 1);
        boolean invert = getYesNoOption("Invert cards (yes/no)? ");

        studySession(cards, order, repetitions, invert);
    }

    private static void studySession(List<Card> cards, String order, int repetitions, boolean invert) {
        long totalTime = 0;
        int totalQuestions = 0;

        for (int rep = 0; rep < repetitions; rep++) {
            // Sort cards
            if (order.equals("recent-mistakes-first")) {
                cards = new RecentMistakesFirstSorter().organize(cards);
            } else if (order.equals("worst-first")) {
                cards = new WorstFirstSorter().organize(cards);
            } else {
                Collections.shuffle(cards);
            }

            for (Card card : cards) {
                System.out.println((invert ? card.getAnswer() : card.getQuestion()) + " ?");
                long start = System.currentTimeMillis();
                String userAnswer = scanner.nextLine();
                long duration = System.currentTimeMillis() - start;

                totalTime += duration;
                totalQuestions++;

                if (userAnswer.equalsIgnoreCase(invert ? card.getQuestion() : card.getAnswer())) {
                    System.out.println("Correct!");
                    card.answeredCorrectly();
                } else {
                    System.out.println("Wrong! Correct answer: " + (invert ? card.getQuestion() : card.getAnswer()));
                    card.answeredWrong();
                }
            }
        }

        showAchievements(cards, totalTime, totalQuestions);
    }

    private static void showAchievements(List<Card> cards, long totalTime, int totalQuestions) {
        System.out.println("\nAchievements:");
        if (totalTime / totalQuestions < 5000) {
            System.out.println("- FAST LEARNER (answered in < 5 seconds)");
        }
        if (AchievementTracker.allCorrect(cards)) {
            System.out.println("- CORRECT (All correct in last round)");
        }
        for (Card c : cards) {
            if (AchievementTracker.repeatedMoreThanFiveTimes(c)) {
                System.out.println("- REPEAT (answered a card > 5 times): " + c.getQuestion());
            }
            if (AchievementTracker.confident(c)) {
                System.out.println("- CONFIDENT (answered correctly ≥ 3 times): " + c.getQuestion());
            }
        }
    }

    private static void editCollections() {
        System.out.print("Enter flashcards filename to edit: ");
        String filename = scanner.nextLine();
        
        try {
            cards = FlashCardManager.loadCards(filename);
        } catch (IOException e) {
            System.out.println("Error loading file.");
            return;
        }

        while (true) {
            System.out.println("\nEditing Menu:");
            System.out.println("1. Add a new card");
            System.out.println("2. Delete a card");
            System.out.println("3. Update a card");
            System.out.println("4. Save and exit");
            System.out.print("Choose an option (1-4): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    addCard();
                    break;
                case "2":
                    deleteCard();
                    break;
                case "3":
                    updateCard();
                    break;
                case "4":
                    saveCards(filename);
                    return;
                default:
                    System.out.println("Invalid choice. Please enter 1, 2, 3, or 4.");
            }
        }
    }

    private static void addCard() {
        System.out.print("Enter the question: ");
        String question = scanner.nextLine();
        System.out.print("Enter the answer: ");
        String answer = scanner.nextLine();

        cards.add(new Card(question, answer));
        System.out.println("Card added successfully.");
    }

    private static void deleteCard() {
        if (cards.isEmpty()) {
            System.out.println("No cards available to delete.");
            return;
        }

        System.out.println("\nSelect a card to delete:");
        for (int i = 0; i < cards.size(); i++) {
            System.out.println((i + 1) + ". " + cards.get(i).getQuestion());
        }

        System.out.print("Enter the number of the card to delete: ");
        int cardIndex = Integer.parseInt(scanner.nextLine()) - 1;

        if (cardIndex >= 0 && cardIndex < cards.size()) {
            cards.remove(cardIndex);
            System.out.println("Card deleted successfully.");
        } else {
            System.out.println("Invalid card number.");
        }
    }

    private static void updateCard() {
        if (cards.isEmpty()) {
            System.out.println("No cards available to update.");
            return;
        }

        System.out.println("\nSelect a card to update:");
        for (int i = 0; i < cards.size(); i++) {
            System.out.println((i + 1) + ". " + cards.get(i).getQuestion());
        }

        System.out.print("Enter the number of the card to update: ");
        int cardIndex = Integer.parseInt(scanner.nextLine()) - 1;

        if (cardIndex >= 0 && cardIndex < cards.size()) {
            Card card = cards.get(cardIndex);
            System.out.print("Enter new question (current: " + card.getQuestion() + "): ");
            String newQuestion = scanner.nextLine();
            System.out.print("Enter new answer (current: " + card.getAnswer() + "): ");
            String newAnswer = scanner.nextLine();

            card = new Card(newQuestion, newAnswer);  // Update card
            cards.set(cardIndex, card);  // Set updated card back
            System.out.println("Card updated successfully.");
        } else {
            System.out.println("Invalid card number.");
        }
    }

    private static void saveCards(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Card card : cards) {
                writer.write(card.getQuestion() + " | " + card.getAnswer());
                writer.newLine();
            }
            System.out.println("Cards saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving file.");
        }
    }

    private static String getOption(String prompt, List<String> options) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().toLowerCase();
            if (options.contains(input)) {
                return input;
            }
            System.out.println("Invalid option. Try again.");
        }
    }

    private static int getIntOption(String prompt, int defaultVal) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    private static boolean getYesNoOption(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim().toLowerCase();
        return input.equals("yes") || input.equals("y");
    }
}
