package edu.brown.cs.sgd.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.brown.cs.sgd.general.General;
import edu.brown.cs.sgd.processing.NERProcessor;

/**
 * News is a class for news stories which extends Story.
 *
 * @author anakai
 *
 */
public class News extends Story {

  private String perfScore;
  private String published;
  private String domainRank;
  private String summary;
  private static final NERProcessor processor = new NERProcessor(null);

  /**
   * A constructor for News.
   *
   * @param title
   *          - a String title of news story
   * @param url
   *          - the url of the news story as a String
   * @param source
   *          - the source of the news story as a String
   * @param fullText
   *          - the full text of the news story as a String
   * @param perfScore
   *          - the performance score of the news story as a String
   * @param mainImage
   *          - the url of the main image of the news story
   * @param published
   *          - the time the news story was published
   * @param domainRank
   *          - the domain rank of the news story
   * @param uuid
   *          - the unique id of the news story
   */
  public News(String title, String url, String source, String fullText,
      String perfScore, String mainImage, String published, String domainRank,
      String uuid) {
    super(title, url, source, mainImage, fullText);

    this.location = General.parseNERText(processor.process(this.firstLines),
        "LOCATION");

    this.uuid = uuid.trim();
    this.domainRank = domainRank;
    this.published = published;
    this.perfScore = perfScore;
    this.summary = "";
  }

  @Override
  public List<String> listify() {
    List<String> newsStory = new ArrayList<>();
    newsStory.add(this.location);
    newsStory.add(this.source);
    newsStory.add(this.perfScore);
    newsStory.add(this.domainRank);
    newsStory.add(this.published);
    newsStory.add(this.image);
    newsStory.add(this.summary);
    newsStory.add(this.firstLines);
    newsStory.add(this.url);
    newsStory.add(this.title);
    newsStory.add(Integer.toString(id));
    if (categoryScores != null) {
      newsStory.add(Double.toString(categoryScores.get("happy")));
      newsStory.add(Double.toString(categoryScores.get("sad")));
      newsStory.add(Double.toString(categoryScores.get("political")));
      newsStory.add(Double.toString(categoryScores.get("mystery")));
      newsStory.add(Double.toString(categoryScores.get("love")));
    }
    return newsStory;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof News)) {
      return false;
    }
    News that = (News) o;
    return that.getUUID().equals(this.uuid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid);
  }

  /**
   * @return the news article's domain rank
   */
  public String getDomainRank() {
    return this.domainRank;
  }

}
