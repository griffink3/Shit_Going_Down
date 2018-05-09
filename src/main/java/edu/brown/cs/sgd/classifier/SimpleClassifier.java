package edu.brown.cs.sgd.classifier;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class for simple sentiment classification.
 *
 * @author charlieholtz
 *
 */
public class SimpleClassifier {

  private final static Set<String> sentiments = new HashSet<String>();

  private static Map<String, Set<String>> sentimentDictionary = new HashMap<String, Set<String>>();

  /**
   * Constructor.
   */
  public SimpleClassifier() {
    sentiments.add("filler");
    sentiments.add("happy");
    sentiments.add("sad");
    sentiments.add("political");
    sentiments.add("mystery");
    sentiments.add("love");
    updateWords();
  }

  /**
   * Updates Sentiment Dictionary!
   */
  public static void updateWords() {

    for (String s : sentiments) {
      System.out.println("Updating " + s + " words...");
      String filepath = "data/sentiments/" + s + ".txt";

      try {
        BufferedReader br = new BufferedReader(new FileReader(filepath));
        String line = br.readLine();

        while (line != null) {

          String[] words = line.replaceAll("[^a-zA-Z ]", " ").toLowerCase()
              .split(" ");

          for (String word : words) {

            if (sentimentDictionary.containsKey(s)) {
              sentimentDictionary.get(s).add(word);
            } else {
              Set<String> newSet = new HashSet<String>();
              sentimentDictionary.put(s, newSet);
              sentimentDictionary.get(s).add(word);
            }
          }
          line = br.readLine();

        }
        br.close();
      } catch (FileNotFoundException fnf) {
        System.out.println("ERROR: File not found.");
      } catch (IOException ioe) {
        System.out.println("ERROR: Problem with buffered reader!");
      }

    }
  }

  /**
   * Analyzes and returns score based on input filename.
   *
   * @param fileName
   *          : directory to file to analyze.
   * @return map of categories to category scores.
   */
  public Map<String, Double> analyze(String fileName) {

    Map<String, Set<String>> score = new HashMap<String, Set<String>>();
    Integer totalWords = 0;

    try {
      BufferedReader br = new BufferedReader(new FileReader(fileName));
      String line = br.readLine();

      while (line != null) {

        String[] words = line.replaceAll("[^a-zA-Z ]", "").toLowerCase()
            .split("\\s+");

        for (String word : words) {
          totalWords += 1;

          for (String key : sentimentDictionary.keySet()) {

            if (sentimentDictionary.get(key).contains(word)) {
              if (!key.equals("filler")) {
                totalWords += 1;
              }
              if (score.containsKey(key)) {
                score.get(key).add(word);
              } else {
                Set<String> newSet = new HashSet<String>();
                score.put(key, newSet);
                score.get(key).add(word);
              }
            }
          }

        }

        line = br.readLine();

      }
      br.close();

    } catch (FileNotFoundException fnf) {
      System.out.println("ERROR: File not found.");
    } catch (IOException ioe) {
      System.out.println("ERROR: Problem with buffered reader!");
    }

    // Send to score printer.
    scorePrinter(score, totalWords);
    return (addScore2Map(score, totalWords));
  }

  /**
   * Analyzes and returns score based on input filename.
   *
   * @param text
   *          : string text to analyze.
   * @return map of categories to category scores.
   */
  public Map<String, Double> analyzeText(String text) {

    Map<String, Set<String>> score = new HashMap<String, Set<String>>();
    Integer totalWords = 0;

    // IF TEXT IS NOT ANYTHING!
    if (text == null) {
      return null;
    }

    String[] words = text.replaceAll("[^a-zA-Z ]", "").toLowerCase()
        .split("\\s+");

    for (String word : words) {
      totalWords += 1;

      for (String key : sentimentDictionary.keySet()) {

        if (sentimentDictionary.get(key).contains(word)) {
          if (score.containsKey(key)) {
            score.get(key).add(word);
          } else {
            Set<String> newSet = new HashSet<String>();
            score.put(key, newSet);
            score.get(key).add(word);
          }
        }
      }
    }

    return (addScore2Map(score, totalWords));
  }

  /**
   * Adds all the scores to the hashmap of category scores.
   *
   * @param score
   *          : score map.
   * @param totalWords
   *          : total words in article.
   * @return hashmap of categories to score
   */
  public Map<String, Double> addScore2Map(Map<String, Set<String>> score,
      Integer totalWords) {

    Map<String, Double> categoryScores = initializeMap();

    for (String s : score.keySet()) {

      double num = (double) (score.get(s).size());
      double total = (double) (totalWords);
      Double categoryScore = ((double) (num / total) * 100.);

      categoryScores.put(s, categoryScore);
    }
    return categoryScores;
  }

  private Map<String, Double> initializeMap() {
    Map<String, Double> map = new HashMap<String, Double>();
    for (String s : sentiments) {
      map.put(s, 0.0);
    }
    return map;
  }

  /**
   * Prints score of article.
   *
   * @param score
   *          : score map.
   * @param totalWords
   *          : total words in article.
   */
  public void scorePrinter(Map<String, Set<String>> score, Integer totalWords) {

    StringBuilder result = new StringBuilder();

    result.append("-----------------ARTICLE INFO: ------------------\n");

    for (String s : score.keySet()) {
      result.append("Score for " + s + " words is ");

      double num = (double) (score.get(s).size());
      double total = (double) (totalWords);
      result.append(num + " / " + total + " ---> " + ""
          + ((double) (num / total) * 100.) + " %.");
      result.append("\n");
    }

    System.out.println(result);
  }

}
