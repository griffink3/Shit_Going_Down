package edu.brown.cs.sgd.general;

import org.junit.Test;

public class GeneralTest {

  /**
   * Testing the djikstra's algorithm through the bacon object's connect
   * command.
   */

  @Test
  public void testGenerateProb() {
    System.out.println(General.generateProb(0.5));
    System.out.println(General.generateProb(0.75));
    System.out.println(General.generateProb(0.25));
  }

  @Test
  public void testSearchStories() {
    // Story story1 = new Story("Story1", null, null,
    // "This is the text of the first story.", null, null, null, null);
    // Story story2 = new Story("Story2", null, null,
    // "What available this is just random words.", null, null, null, null);
    // Story story3 = new Story("Story3", null, null,
    // "Available is the first words oh my god.", null, null, null, null);
    // Story story4 = new Story("Story4", null, null,
    // "Updates are available for your software", null, null, null, null);
    // Story story5 = new Story("Story5", null, null,
    // "Set up reminder options right now.", null, null, null, null);
    // Map<Integer, Story> map = new HashMap<Integer, Story>();
    // map.put(1, story1);
    // map.put(2, story2);
    // map.put(3, story3);
    // map.put(4, story4);
    // map.put(5, story5);
    // List<Story> result = General.searchStories(map, "first");
    // for (Story s : result) {
    // System.out.println(s.getTitle());
    // }
    // result = General.searchStories(map, "available");
    // for (Story s : result) {
    // System.out.println(s.getTitle());
    // }
    // result = General.searchStories(map, "words");
    // for (Story s : result) {
    // System.out.println(s.getTitle());
    // }
  }

}
