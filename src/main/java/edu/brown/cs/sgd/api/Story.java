package edu.brown.cs.sgd.api;
import java.util.List;
import java.util.Map;

import edu.brown.cs.sgd.classifier.SimpleClassifier;

/**
 * An abstract class for a story.
 *
 * @author anakai
 *
 */
public abstract class Story {
	protected String url;
	protected String firstLines;
	protected String title;
	protected String source;
	protected String location;
	protected String image;
	protected int id;
	protected static int currId = 0;
	protected String uuid;
	protected Map<String, Double> categoryScores;
	private static final SimpleClassifier sc = new SimpleClassifier();
	
	/**
	 * A constructor for a Story.
	 * 
	 * @param title - title of the story
	 * @param url - url that we can find the story at
	 * @param source - source of the story
	 * @param image - url to the link
	 * @param text - text of the story
	 */
	public Story(String title, String url, String source, String image, String text) {
		this.title = title;
		this.url = url;
		this.source = source;
		this.firstLines = text;
		this.image = image;
		this.id = currId;
		this.categoryScores = sc.analyzeText(this.firstLines);
		currId++;
	}
	
	/**
	 * A getter for the url.
	 *
	 * @return - a String representing the URL
	 */
	public String getURL() {
	  return this.url;
	}

	/**
	 * getFirstLines returns the text of the story.
	 *
	 * @return - the text of the story as a String
	 */
	public String getFirstLines() {
	  return this.firstLines;
	}

	/**
	 * getTitle returns the title of the Story.
	 *
	 * @return - the title of the story as a String
	 */
	public String getTitle() {
	  return this.title;
	}

	/**
	 * getId returns the unique id of the story.
	 *
	 * @return the id as an int
	 */
	public int getId() {
	  return id;
	}
	  
	/**
	 * getCatScores returns the category scores for a story.
	 *
	 * @return a Map of the categories to their scores
	 */
	public Map<String, Double> getCatScores() {
	  return categoryScores;
	}
	  
	/**
	 * getLocation returns the location of the story.
	 *
	 * @return - the location as a String
	 */
	public String getLocation() {
		return this.location;
	}
	
	/**
	 * getUUID returns the UUID of the story.
	 *
	 * @return - the UUID of the story as a String
	 */
	public String getUUID() {
		return this.uuid;
	}
	  
	/**
	 * listify turns a story into a list of Strings.
	 *
	 * @return - a list of strings representing a story
	 */
	public abstract List<String> listify();
	  

  
}


