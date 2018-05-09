package edu.brown.cs.sgd.api;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import edu.brown.cs.sgd.webhose.WebhoseIOClient;

/**
 * Class to handle requests to getting news stories using WebhoseApi.
 *
 * @author anakai
 *
 */
public class WebhoseApiHandler {
  private WebhoseIOClient client;
  private Set<String> sites;
  private String querySite;

  /**
   * Constructor for a WebhoseApiHandler.
   */
  public WebhoseApiHandler() {
    this.client = WebhoseIOClient
        .getInstance("fd3b4e80-a80c-4a38-8bc7-b6c41854747e");
    this.sites = initializeSites();
    this.querySite = siteQuery();
  }

  /**
   * A function that initializes which sites to draw data from.
   *
   * @return - a set of strings indicating which sites to get stories from
   */

  private Set<String> initializeSites() {
    Set<String> temp = new HashSet<>();
    temp.add("abcnews.com");
    temp.add("apnews.com");
    temp.add("cnn.com");
    temp.add("bbc.com");
    temp.add("news.google.com");
    temp.add("espn.com");
    temp.add("nbcnews.com");
    temp.add("politico.com");
    temp.add("yahoo.com");
    temp.add("buzzfeed.com");
    temp.add("nytimes.com");
    temp.add("aljazeera.com");
    temp.add("ew.com");
    temp.add("msnbc.com");
    temp.add("reuters.com");
    temp.add("timesofindia.indiatimes.com");
    temp.add("bloomberg.com");
    return temp;
  }

  /**
   * addSite allows the user to add a source in.
   *
   * @param s
   *          - the source to add
   */

  public void addSite(String s) {
    this.sites.add(s);
    this.querySite = siteQuery();
  }

  /**
   * removeSite removes a site from the sites list.
   *
   * @param s
   *          - the site to remove
   */
  public void removeSite(String s) {
    this.sites.remove(s);
    this.querySite = siteQuery();
  }

  /**
   * siteQuery turns the sites list into a string of sites to query.
   *
   * @return - a String in the format needed for the webhose client
   */
  private String siteQuery() {
    StringBuilder toQuery = new StringBuilder();
    toQuery.append("(");
    for (String s : this.sites) {
      toQuery.append("site:");
      toQuery.append(s);
      toQuery.append(" OR ");
    }
    String toReturn = toQuery.substring(0, toQuery.length() - 3).trim() + ")";
    return toReturn;

  }

  /**
   * queryWebHoseBreaking allows you to get breaking news from the Webhose News
   * Api.
   *
   * @return a list of stories that were queried
   */
  public List<News> queryWebHoseBreaking() {
    // System.out.println(this.querySite);
    List<News> results = new ArrayList<>();
    try {
      Map<String, String> query = new HashMap<>();
      // gets only articles from news sites in english published within the
      // last day
      query.put("q",
          "site_type:news language:english published:>1524369600000 is_first:true "
              + this.querySite);
      // keeps crawled articles for less than a day
      query.put("ts", "1524415091661");
      // sort by facebook shares
      query.put("sort", "social.facebook.shares");
      JsonElement result = client.query("filterWebContent", query);
      results = resultsProcess(result);
      for (int i = 0; i < 3; i++) {
        JsonElement resultTwo = this.client.getNext();
        List<News> listTwo = resultsProcess(resultTwo);
        results.addAll(listTwo);
      }
      // JsonElement resultTwo = this.client.getNext();
      // List<News> listTwo = resultsProcess(resultTwo);
      // results.addAll(listTwo);
      System.out.println(results.size());
      return results;
    } catch (IOException e) {
      return new ArrayList<>();

    } catch (URISyntaxException e) {
      return new ArrayList<>();
    }

  }

  private List<News> resultsProcess(JsonElement result) {
    List<News> results = new ArrayList<>();
    JsonArray postArray = result.getAsJsonObject().getAsJsonArray("posts");
    for (JsonElement j : postArray) {
      String published = stripQuotationMarks(
          j.getAsJsonObject().get("published").toString());
      JsonElement thread = j.getAsJsonObject().get("thread");
      String title = stripQuotationMarks(
          j.getAsJsonObject().get("title").toString());
      String url = stripQuotationMarks(
          j.getAsJsonObject().get("url").toString());
      String source = stripQuotationMarks(
          thread.getAsJsonObject().get("site_full").toString());
      String mainImage = stripQuotationMarks(
          thread.getAsJsonObject().get("main_image").toString());
      String fullText = stripQuotationMarks(
          j.getAsJsonObject().get("text").toString());
      String perfScore = stripQuotationMarks(
          thread.getAsJsonObject().get("performance_score").toString());
      String domainRank = stripQuotationMarks(
          thread.getAsJsonObject().get("domain_rank").toString());
      String uuid = stripQuotationMarks(
          j.getAsJsonObject().get("uuid").toString());
      News curr = new News(title, url, source, fullText, perfScore, mainImage,
          published, domainRank, uuid);
      results.add(curr);
    }
    return results;
  }

  /**
   * queryWebhoseWithQuery queries based on keywords.
   *
   * @param s
   *          - the queries (as a string) that you want to use
   * @return - a list of stories
   */
  public List<News> queryWebhoseWithQuery(String s) {
    List<News> results = new ArrayList<>();
    try {
      Map<String, String> query = new HashMap<>();
      // gets only articles from news sites in english published within the
      // last day
      query.put("q", s
          + " site_type:news language:english published:>1524369600000 is_first:true");
      // keeps crawled articles for less than a day
      query.put("ts", "1524415091661");
      // sort by facebook shares
      query.put("sort", "social.facebook.shares");
      JsonElement result = client.query("filterWebContent", query);
      results = resultsProcess(result);
      for (int i = 0; i < 3; i++) {
    	  JsonElement next = client.getNext();
    	  if (next == null) {
    		  break;
    	  }
    	  List<News> resultTwo = resultsProcess(next);
    	  results.addAll(resultTwo);
      }
      return results;
    } catch (IOException e) {
      return new ArrayList<>();

    } catch (URISyntaxException e) {
      return new ArrayList<>();
    }
  }

  /**
   * listNews turns each story in the news list to a List of lists of strings.
   *
   * @param news
   *          - a list of stories to convert
   * @return - a list of lists of strings
   */
  public List<List<String>> listNews(List<News> news) {
    List<List<String>> storyList = new ArrayList<>();
    for (News s : news) {
      List<String> converted = s.listify();
      storyList.add(converted);
    }
    return storyList;
  }

  /**
   * stripQuotationMarks gets rid of quotation marks on a string if they exist.
   *
   * @param s
   *          - the string to strip quotation marks from.
   * @return - a String with quotation marks stripped or the original String
   */

  private String stripQuotationMarks(String s) {
    if (s.matches("\".*\"")) {
      s = s.substring(1, s.length() - 1);
    }
    return s;
  }

}
