package com.flashcard;

import java.util.*;

public class FlashCardApp {
    public static void main(String[] args) throws Exception {
        if (args.length == 0 || args[0].equals("--help")) {
            showHelp();
            return;
        }

        String filename = args[0];
        String order = "random";
        int repetitions = 1;
        boolean invert = false;

        for (int i = 1; i < args.length; i++) {
            switch (args[i]) {
                case "--help":
                    showHelp();
                    return;
                case "--order":
                    if (i + 1 < args.length) {
                        order = args[++i];
                        if (!order.equals("random") && !order.equals("worst-first") && !order.equals("recent-mistakes-first")) {
                            System.out.println("Invalid order: " + order);
                            return;
                        }
                    } else {
                        System.out.println("Missing value for --order");
                        return;
                    }
                    break;
                case "--repetitions":
                    if (i + 1 < args.length) {
                        repetitions = Integer.parseInt(args[++i]);
                    } else {
                        System.out.println("Missing value for --repetitions");
                        return;
                    }
                    break;
                case "--invertCards":
                    invert = true;
                    break;
                default:
                    System.out.println("Unknown option: " + args[i]);
                    showHelp();
                    return;
            }
        }

        List<Card> cards = FlashCardManager.loadCards(filename);
        Scanner scanner = new Scanner(System.in);
        long totalTime = 0;
        int totalQuestions = 0;

        for (int rep = 0; rep < repetitions; rep++) {
            // Sort cards based on the selected order
            if (order.equals("recent-mistakes-first")) {
                RecentMistakesFirstSorter sorter = new RecentMistakesFirstSorter();
                cards = sorter.organize(cards);
            } else if (order.equals("worst-first")) {
                WorstFirstSorter sorter = new WorstFirstSorter();
                cards = sorter.organize(cards);
            } else if (order.equals("random")) {
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

    private static void showHelp() {
        System.out.println("Usage: flashcard <cards-file> [options]");
        System.out.println("Options:");
        System.out.println("--help");
        System.out.println("--order <order>");
        System.out.println("--repetitions <num>");
        System.out.println("--invertCards");
    }
}
