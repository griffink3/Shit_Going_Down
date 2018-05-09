package edu.brown.cs.sgd.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * User is a template class for the Users of the web application.
 *
 * @author sgd
 *
 */
public class User {
  private UserPreference prefs;
  private String name;
  private int id;
  private String login;
  private String password;

  //private Integer votePower = 5;

  /**
   * A constructor for a User.
   *
   * @param name
   *          - a String representing the User's name
   * @param login
   *          - a String representing the User's login
   * @param sessionId
   *          - a String representing the sessionId
   * @param password
   *          - a String representing the User's password
   */
  public User(String name, String login, int id, String password) {
    this.name = name;
    this.login = login;
    this.id = id;
    this.password = password;
    this.prefs = new UserPreference();
  }
  
  /**
   * Getter method for user id.
   *
   * @return - the user's id
   */
  public int getID() {
	  return this.id;
  }
  
  public String getLogin() {
	  return this.login;
  }
  
  public String getPassword() {
	  if (this.password == null) {
		  return "";
	  }
	  return this.password;
  }
  
  public String getName() {
	  if (this.name == null) {
		  return "";
	  }
	  return this.name;
  }

  /**
   * Method of either upvoting or downvoting a category of story.
   *
   * @param category
   *          : which category to update
   * @param upvote
   *          : true if up-vote, false if down-vote
   * @param value
   *          the value of the story in the category
   */
  public void updatePreference(String category, Boolean upvote,
      double value) {

    if (upvote) {
      prefs.updateImportance(category, value);
    } else {
      prefs.updateImportance(category, value * -1);
    }
  }

  /**
   * Gets the preference value for the specified category.
   * 
   * @param category
   *          the specified category
   */
  public double getPreference(String category) {
    return prefs.getImportance(category);
  }

  /**
   * Gets the categories taken into account by this particular user.
   * 
   * @return a set of all the categories stored in the preference map
   */
  public Set<String> getPrefCategories() {
    return prefs.getCategories();
  }

  /**
   * Gets the user preferences as a list of list of strings.
   * 
   * @return the user preferences as a list of list of strings
   */
  public List<List<String>> getPrefList() {
    List<List<String>> list = new ArrayList<List<String>>();
    for (String s : prefs.getPrefMap().keySet()) {
      List<String> l = new ArrayList<String>();
      l.add(s);
      l.add(Double.toString(prefs.getImportance(s)));
      list.add(l);
    }
    return list;
  }

}
