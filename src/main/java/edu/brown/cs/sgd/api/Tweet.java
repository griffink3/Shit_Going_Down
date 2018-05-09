package edu.brown.cs.sgd.api;

import java.util.ArrayList;
import java.util.List;

/**
 * A class representing Tweets that extends the Story abstract class.
 *
 * @author anakai
 *
 */
public class Tweet extends Story {
  private String author;
  private String time;
  private String popularity;
  private String comments;
  private String summary;

  /**
   * A constructor for a Tweet.
   *
   * @param url
   *          - Tweet url
   * @param firstLines
   *          - text of the Tweet
   * @param summary
   *          - summary of the Tweet
   * @param title
   *          - title of the Tweet
   * @param location
   *          - location of Tweet
   * @param source
   *          - source of Tweet
   * @param popularity
   *          - popularity of Tweet
   * @param comments
   *          - comments on Tweet
   * @param image
   *          - url for the Tweet's image
   * @param author
   *          - author of the Tweet
   * @param time
   *          - time the Tweet was tweeted
   */
  public Tweet(String url, String firstLines, String summary, String title,
      String location, String source, String popularity, String comments,
      String image, String author, String time) {
    super(title, url, source, image, firstLines);
    this.summary = summary;
    this.uuid = "";
    this.location = location;
    this.popularity = popularity;
    this.comments = comments;
    this.image = image;
    this.author = author;
    this.time = time;
    this.id = currId;
    currId++;
  }

  /**
   * getSummary is a getter for summary.
   *
   * @return - the Tweet's summary as a string
   */
  public String getSummary() {
    return this.summary;
  }

  /**
   * getAuthor is a getter for the author.
   *
   * @return - the author as a String
   */
  public String getAuthor() {
    return this.author;
  }

  /**
   * getTime is a getter for the time
   *
   * @return - Tweet's publishing time as a String.
   */
  public String getTime() {
    return this.time;
  }

  /**
   * getPopularity is a getter for the popularity of the tweet.
   *
   * @return - popularity as a String
   */
  public String getPopularity() {
    return this.popularity;
  }

  /**
   * getComments is a getter for the comments on a tweet.
   *
   * @return - the comments as a String.
   */
  public String getComments() {
    return this.comments;
  }

  @Override
  public List<String> listify() {
    List<String> temp = new ArrayList<String>();
    temp.add(this.url);
    temp.add(this.firstLines);
    temp.add(this.summary);
    temp.add(this.title);
    temp.add(this.location);
    temp.add(this.source);
    temp.add(this.popularity);
    temp.add(this.comments);
    temp.add(this.image);
    temp.add(this.author);
    temp.add(this.time);
    temp.add(Integer.toString(id));
    if (categoryScores != null) {
      temp.add(Double.toString(categoryScores.get("happy")));
      temp.add(Double.toString(categoryScores.get("sad")));
      temp.add(Double.toString(categoryScores.get("political")));
      temp.add(Double.toString(categoryScores.get("mystery")));
      temp.add(Double.toString(categoryScores.get("love")));
    }
    return temp;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Tweet)) {
      return false;
    }
    Tweet that = (Tweet) o;
    return this.id == that.id;
  }

}
