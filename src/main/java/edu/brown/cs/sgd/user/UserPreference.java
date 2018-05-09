package edu.brown.cs.sgd.user;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Contains information about the preferences of a single user.
 * 
 * @author gk16
 */
public class UserPreference {

  private final Map<String, Double> preferenceMap =
      new HashMap<String, Double>();

  /**
   * An instance of the preference information of a single user. Always starts
   * with these five categories.
   */
  public UserPreference() {
    preferenceMap.put("happy", 0.0);
    preferenceMap.put("sad", 0.0);
    preferenceMap.put("political", 0.0);
    preferenceMap.put("mystery", 0.0);
    preferenceMap.put("love", 0.0);
  }

  /**
   * Updates the importance value for the specified category using the value
   * specified - specifically increments the already existing value (if the
   * category exists) instead of replacing.
   * 
   * @param category
   *          the category to update
   * @param importance
   *          the value to update by
   */
  public void updateImportance(String category, double importance) {
    if (preferenceMap.containsKey(category)) {
      double val = preferenceMap.get(category);
      if (val + importance < 0.0) {
        preferenceMap.replace(category, 0.0);
      } else {
        preferenceMap.replace(category, val + importance);
      }
    } else {
      preferenceMap.put(category, importance);
    }
  }

  /**
   * Gets the importance of a category if in the preference map.
   * 
   * @param category
   *          the category to get
   * @return the importance value
   */
  public double getImportance(String category) {
    if (preferenceMap.containsKey(category)) {
      return preferenceMap.get(category);
    } else {
      return 0.0;
    }
  }

  /**
   * Gets a set of all the categories stored in the map.
   * 
   * @return a set of the categories
   */
  public Set<String> getCategories() {
    return preferenceMap.keySet();
  }

  /**
   * Gets the preference map.
   * 
   * @return the preference map
   */
  public Map<String, Double> getPrefMap() {
    return preferenceMap;
  }

  /**
   * Gives a human-readable string representing this instance of user
   * preferences.
   *
   * @return string representation of user preferences
   */
  public String toString() {
    return null;
  }

}
