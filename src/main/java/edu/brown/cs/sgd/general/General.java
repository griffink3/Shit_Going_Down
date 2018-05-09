package edu.brown.cs.sgd.general;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.brown.cs.sgd.api.News;
import edu.brown.cs.sgd.api.Story;
import edu.brown.cs.sgd.api.Tweet;
import edu.brown.cs.sgd.user.User;

/**
 * General handles small logical methods that do not pertain to any specific
 * class.
 */

public final class General {

  public static final int NUM_TWEETS = 500;
  public static final int NUM_THREADS = 50;
  private static final double CORRECTION_FACTOR = 0.75;

  private General() {
  }

  /**
   * If the token is surrounded by quotes, removes the quotes.
   *
   * @param token
   *          token to be scrubbed
   * @return token scrubbed of quotes
   */
  public static String removeQuotes(String token) {
    if (token.startsWith("\"")) {
      token = token.substring(1, token.length());
    }
    if (token.endsWith("\"")) {
      token = token.substring(0, token.length() - 1);
    }
    return token;
  }

  /**
   * Splits token by colon (but only the first colon).
   *
   * @param token
   *          token to be split
   * @return split token as list of strings
   */
  public static List<String> splitTokenByColon(String token) {
    String[] tokens = token.split(":");
    List<String> tokens1 = new ArrayList<String>();
    tokens1.add(tokens[0]);
    StringBuilder sb = new StringBuilder();
    for (int i = 1; i < tokens.length; i++) {
      sb.append(tokens[i]);
    }
    tokens1.add(sb.toString());
    return tokens1;
  }

  /**
   * Filters a list of stories based on the given user preferences for category
   * types.
   *
   * @param stories
   *          list of stories to filter
   * @param user
   *          user whose preference to filter for
   * @return filtered list of stories
   */
  public static List<Story> filterStories(List<Story> stories, User user,
      boolean tweet) {
    List<Story> filtered = new ArrayList<Story>();
    String cat1 = "";
    String cat2 = "";
    double importance1 = 0;
    double importance2 = 0;
    for (String category : user.getPrefCategories()) {
      if (user.getPreference(category) > importance1) {
        cat2 = cat1;
        importance2 = importance1;
        cat1 = category;
        importance1 = user.getPreference(category);
      } else if (user.getPreference(category) > importance2) {
        cat2 = category;
        importance2 = user.getPreference(category);
      }
    }
    int max = -1;
    for (Story story : stories) {
      if (story.getCatScores() != null) {
        int w = getPopularity(story, tweet);
        double total = getTotalWeight(story);
        if (w > max) {
          max = w;
        }
        if (cat1.equals("") || cat2.equals("")) {
          if (filterStory(w, max)) {
            filtered.add(story);
          }
        } else {
          if (filterStory(story, cat1, cat2, total, tweet, w, max)) {
            filtered.add(story);
          }
        }
      }
    }
    return filtered;
  }

  private static double getTotalWeight(Story story) {
    double total = 0;
    for (String s : story.getCatScores().keySet()) {
      total = total + story.getCatScores().get(s);
    }
    return total;
  }

  private static int getPopularity(Story story, boolean tweet) {
	  try {
    if (tweet) {
      if (((Tweet) story).getPopularity() == null
          || ((Tweet) story).getComments() == null) {
        return 0;
      }
      return Integer.parseInt(((Tweet) story).getPopularity())
          + Integer.parseInt(((Tweet) story).getComments());
    } else {
      if (((News) story).getDomainRank() == null) {
        return 0;
      }
      return Integer.parseInt(((News) story).getDomainRank());
    }
	  } catch (Exception e) {
		  return 0;
	  }
  }

  private static boolean filterStory(int popularity, int prevMax) {
    // The probability is the relative popularity
    double prob = 0;
    if (prevMax > 0) {
      assert (popularity <= prevMax);
      prob = ((double) popularity / (double) prevMax);
    }
    // Correcting to increase overall probability
    prob = prob + (1 - prob) * CORRECTION_FACTOR;
    return generateProb(prob);
  }

  private static boolean filterStory(Story story, String cat1, String cat2,
      double total, boolean tweet, int popularity, int prevMax) {
    if (story.getCatScores() == null) {
      return false;
    }
    // The probability is the proportion of the story's weight in the two most
    // important categories (according to the user) in relation to the story's

    // total weight plus the relative popularity scaled to the remainder
    double prob = 0;
    if (total > 0) {
      prob = (story.getCatScores().get(cat1) + story.getCatScores().get(cat2))
          / total;
    }
    // Adding the relative popularity scaled to the remainder
    if (prevMax > 0) {
      assert (popularity <= prevMax);
      prob = prob + (1 - prob) * ((double) popularity / (double) prevMax);
    }

    // Correcting to increase overall probability
    prob = prob + (1 - prob) * CORRECTION_FACTOR;
    return generateProb(prob);
  }

  /**
   * Simulates probability of event occurring given the probability as a double
   * between 0 and 1.
   *
   * @param prob
   *          double between 0 and 1 detailing the probability
   * @return boolean dependent on the probability passed in
   */
  public static boolean generateProb(double prob) {
    assert (prob <= 1);
    return (new Random()).nextDouble() < prob;
  }

  /**
   * Parses text that has been processed by NER based on the delimiter
   * specified.
   *
   * @param text
   *          NER processed text
   * @param delimiter
   *          delimiter to split on
   * @return returned the first instance of the specified type or null if error
   *         occurs/instance isn't found
   */
  public static String parseNERText(String text, String delimiter) {
    try {
      if (text != null) {
        String entity = delimiter.toUpperCase();
        Pattern pattern = Pattern.compile("[a-zA-Z0-9.%]+\t" + entity + "\t");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
          int start = matcher.start();
          int end = matcher.end();
          String inputText = text.substring(start, end);
          inputText = inputText.replaceAll("/" + entity, "");
          return inputText.split("\t")[0];
        }
      }
    } catch (Exception e) {
      General.printErr(e.getMessage());
    }
    return "";
  }

  /**
   * Searches the stories in a map for the given word.
   * 
   * @param stories
   *          map mapping story ids to stories
   * @param word
   *          key word to search for
   * @return list of the stories containing the keyword
   */
  public static List<Story> searchStories(Map<Integer, Story> stories,
      String searchWord) {
	  System.out.println(searchWord);

    List<Story> results = Collections.synchronizedList(new ArrayList<Story>());
    String word = searchWord.toLowerCase();

    class Worker implements Runnable {
      // Queue that holds all the remaining tasks
      private final BlockingQueue<Integer> queue;

      // Shared set of found stories
      List<Story> resultSet;

      // Initialize Worker with blocking queue and shared result set
      Worker(BlockingQueue<Integer> q, List<Story> r) {
        queue = q;
        resultSet = r;
      }

      @Override
      public void run() {
        // Run task from queue while queue isn't empty
        int l;
        try {
          while (queue.size() != 0) {
            l = queue.take();
            Story s = stories.get(l);
            if (s.getFirstLines().toLowerCase().contains(word)) {
              resultSet.add(s);
            }
          }
        } catch (InterruptedException e) {
          General.printErr(e.getMessage());
        }
      }

    }

    int nThreads = NUM_THREADS;
    // Create a thread pool
    ExecutorService exServe = Executors.newFixedThreadPool(nThreads);

    // Create Blocking Queue with all story ids
    BlockingQueue<Integer> queue = new LinkedBlockingQueue<Integer>();
    for (int id : stories.keySet()) {
      try {
        queue.put(id);
      } catch (InterruptedException e) {
        General.printErr(e.getMessage());
      }
    }

    // Create a worker for each thread
    List<Callable<Object>> todo = new ArrayList<Callable<Object>>(nThreads);
    Worker worker = new Worker(queue, results);
    for (int i = 0; i < nThreads; i++) {
      todo.add(Executors.callable(worker));
    }

    // Run threads and shut down the thread pool
    try {
      exServe.invokeAll(todo);
      exServe.shutdownNow();
    } catch (InterruptedException e) {
      General.printErr(e.getMessage());
    }

    return results;

  }

  /**
   * Method to rank stories based on popularity.
   * 
   * @param stories
   *          list of stories
   * @param tweet
   *          boolean specifying whether the stories passed in are tweets
   * @return ranked list of stories
   */
  public static List<Story> rankStories(List<Story> stories, boolean tweet) {
    Map<Integer, List<Story>> map = new HashMap<Integer, List<Story>>();
    List<Story> ranked = new ArrayList<Story>();
    for (Story t : stories) {
      int weight = 0;
      if (tweet) {
        weight = Integer.parseInt(((Tweet) t).getPopularity())
            + Integer.parseInt(((Tweet) t).getComments());
      } else {
        weight = Integer.parseInt(((News) t).getDomainRank());
      }
      if (map.containsKey(weight)) {
        map.get(weight).add(t);
      } else {
        List<Story> l = new ArrayList<Story>();
        l.add(t);
        map.put(weight, l);
      }
    }
    List<Integer> weights = Arrays
        .asList(map.keySet().toArray(new Integer[map.keySet().size()]));
    Collections.sort(weights, Collections.reverseOrder());
    for (int w : weights) {
      for (Story s : map.get(w)) {
        ranked.add(s);
      }
    }
    return ranked;
  }

  /**
   * Prints out an error message in accordance with CS0320's guidelines.
   *
   * @param msg
   *          message to print.
   */
  public static void printErr(String msg) {
    System.out.println("ERROR: " + msg);
  }
}
