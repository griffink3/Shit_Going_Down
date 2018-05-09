package edu.brown.cs.sgd.processing;

import java.util.List;

import edu.brown.cs.sgd.api.Story;
import edu.brown.cs.sgd.api.Tweet;
import edu.brown.cs.sgd.general.General;

/**
 * Handles processing tweets.
 * 
 * @author gk16
 */
public class TwitterProcessor {

  public TwitterProcessor() {
    // something here?
  }

  public static Tweet process(String msg) {
    String id = null;
    String firstLines = null;
    String title = null;
    String location = null;
    String source = null;
    String favorites = null;
    String retweets = null;
    String image = null;
    String name = null;
    String time = null;
    String[] tokens = msg.split(",");
    for (String token : tokens) {
      List<String> list = General.splitTokenByColon(token);
      id = getUrl(list, id);
      firstLines = getFirstLines(list, firstLines);
      title = getTitle(list, title);
      location = getLocation(list, location);
      source = getSource(list, source);
      favorites = getPopularity(list, favorites);
      retweets = getComments(list, retweets);
      image = getImage(list, image);
      name = getName(list, name);
      time = getTime(list, time);
    }
    return new Tweet(id, firstLines, null, title, location, source, favorites,
        retweets, image, name, time);
  }

  private static String getUrl(List<String> list, String id) {
    if (General.removeQuotes(list.get(0)).equals("id")) {
      if (list.size() > 1 && id == null) {
        id = General.removeQuotes(list.get(1));
      }
    }
    return id;
  }

  private static String getFirstLines(List<String> list, String firstLines) {
    if (General.removeQuotes(list.get(0)).equals("text")) {
      if (list.size() > 1 && firstLines == null) {
        firstLines = General.removeQuotes(list.get(1));
      }
    }
    return firstLines;
  }

  private static String getTitle(List<String> list, String title) {
    if (General.removeQuotes(list.get(0)).equals("screen_name")) {
      if (list.size() > 1 && title == null) {
        title = General.removeQuotes(list.get(1));
      }
    }
    return title;
  }

  private static String getLocation(List<String> list, String location) {
    if (General.removeQuotes(list.get(0)).equals("location")) {
      if (list.size() > 1 && location == null) {
        location = General.removeQuotes(list.get(1));
      }
    }
    return location;
  }

  private static String getSource(List<String> list, String source) {
    if (General.removeQuotes(list.get(0)).equals("source")) {
      if (list.size() > 1 && source == null) {
        source = General.removeQuotes(list.get(1));
      }
    }
    return source;
  }

  private static String getPopularity(List<String> list, String popularity) {
    if (General.removeQuotes(list.get(0)).equals("favorite_count")) {
      if (list.size() > 1 && popularity == null) {
        popularity = General.removeQuotes(list.get(1));
      }
    }
    return popularity;
  }

  private static String getComments(List<String> list, String comments) {
    if (General.removeQuotes(list.get(0)).equals("retweet_count")) {
      if (list.size() > 1 && comments == null) {
        comments = General.removeQuotes(list.get(1));
      }
    }
    return comments;
  }

  private static String getImage(List<String> list, String image) {
    if (General.removeQuotes(list.get(0)).equals("profile_image_url")) {
      if (list.size() > 1 && image == null) {
        image = General.removeQuotes(list.get(1));
      }
    }
    return image;
  }

  private static String getName(List<String> list, String name) {
    if (General.removeQuotes(list.get(0)).equals("name")) {
      if (list.size() > 1 && name == null) {
        name = General.removeQuotes(list.get(1));
      }
    }
    return name;
  }

  private static String getTime(List<String> list, String time) {
    if (General.removeQuotes(list.get(0)).equals("created_at")) {
      if (list.size() > 1 && time == null) {
        time = General.removeQuotes(list.get(1));
      }
    }
    return time;
  }

}
