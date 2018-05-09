package edu.brown.cs.sgd.general;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
// import java.util.HashMap;
import java.util.List;
// import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.twitter.hbc.core.Client;

import edu.brown.cs.sgd.api.News;
import edu.brown.cs.sgd.api.Story;
import edu.brown.cs.sgd.api.Tweet;
import edu.brown.cs.sgd.api.TwitterApiHandler;
import edu.brown.cs.sgd.api.WebhoseApiHandler;
import edu.brown.cs.sgd.user.User;

/**
 * This is a web socket implementation for when the map needs to update a
 * client.
 * 
 * @author gk16
 */
@WebSocket
public class UpdatingWebSocket {
  private static final Gson GSON = new Gson();
  private static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();
  private static int nextId = 0;
  private static final TwitterApiHandler handler = new TwitterApiHandler();
  private static final Client client = handler.createClient();

  private static final WebhoseApiHandler newsHandler = new WebhoseApiHandler();
  private static final Cache<Integer, Story> idToTweetCache = CacheBuilder
      .newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build();
  private static final Cache<Integer, Story> idToStoryCache = CacheBuilder
      .newBuilder().expireAfterWrite(6, TimeUnit.MINUTES).build();
  private static final Cache<Story, Integer> storyToIdCache = CacheBuilder
      .newBuilder().expireAfterWrite(6, TimeUnit.MINUTES).build();

  private static CacheLoader<Integer, User> l1 = new CacheLoader<Integer, User>() {
    @Override
    public User load(Integer s) throws SQLException {
      if (UserDatabase.getConn() != null) {
        try (PreparedStatement prep = UserDatabase.getConn()
            .prepareStatement("SELECT * FROM user WHERE " + " id = ?;")) {
          prep.setInt(1, s);
          ResultSet rs = prep.executeQuery();
          if (rs.next()) {
            String login = rs.getString(2);
            String password = rs.getString(3);
            double happy = rs.getDouble(4);
            double sad = rs.getDouble(5);
            double political = rs.getDouble(6);
            double mystery = rs.getDouble(7);
            double love = rs.getDouble(8);
            User u = new User(null, login, s, password);
            u.updatePreference("happy", true, happy);
            u.updatePreference("sad", true, sad);
            u.updatePreference("political", true, political);
            u.updatePreference("mystery", true, mystery);
            u.updatePreference("love", true, love);
            return u;
          } else {
            return null;
          }
        } catch (SQLException e) {
          return null;
        }

      }
      return null;
    }
  };
  private static final LoadingCache<Integer, User> idToUserCache = CacheBuilder
      .newBuilder().build(l1);
  private static CacheLoader<String, Integer> l2 = new CacheLoader<String, Integer>() {
    @Override
    public Integer load(String s) throws SQLException {
      if (UserDatabase.getConn() != null) {
        try (PreparedStatement prep = UserDatabase.getConn()
            .prepareStatement("SELECT id FROM user WHERE username = ?;")) {
          prep.setString(1, s);
          ResultSet rs = prep.executeQuery();
          if (rs.next()) {
            int id = rs.getInt(1);
            return id;
          }
        }
      }
      return Integer.MIN_VALUE;
    }
  };

  private static final LoadingCache<String, Integer> nameToIdCache = CacheBuilder
      .newBuilder().build(l2);

  private static enum MESSAGE_TYPE {
    CONNECT, FINDTWEETS, FINDSTORIES, UPDATETWEETS, UPDATESTORIES, USER, VOTE, SEARCH, USERPREFERENCE
  }

  /**
   * connected connects the WebSocket.
   *
   * @param session
   *          - the session to create
   * @throws IOException
   */
  @OnWebSocketConnect
  public void connected(Session session) throws IOException {
    // Adds the session to the queue
    sessions.add(session);
    // Building the CONNECT message
    JsonObject json = new JsonObject();
    json.addProperty("type", MESSAGE_TYPE.CONNECT.ordinal());
    session.getRemote().sendString(GSON.toJson(json));
  }

  @OnWebSocketClose
  public void closed(Session session, int statusCode, String reason) {
    // Removes the session from the queue
    sessions.remove(session);
  }

  @OnWebSocketMessage
  public void message(Session session, String message) throws IOException {
    JsonObject received = GSON.fromJson(message, JsonObject.class);
    JsonObject payload = received.get("payload").getAsJsonObject();
    if (received.get("type").getAsInt() == MESSAGE_TYPE.USER.ordinal()) {
      // If the message is of type USER, we must determine which user this is

      // Need to check if the user exists and if it does - then invoke the
      // previously assigned id
      String username = payload.get("username").getAsString();
      int id = 0;
      int stored;
      try {
        stored = nameToIdCache.get(username);

        if (stored == Integer.MIN_VALUE) {
          nameToIdCache.put(username, nextId);
          id = nextId;
          nextId++;
        } else {
          id = stored;
        }
      } catch (ExecutionException e) {
        nameToIdCache.put(username, nextId);
        id = nextId;
        nextId++;
        General.printErr(e.getMessage());
      }

      // Send USER message to all users
      JsonObject json = new JsonObject();
      json.addProperty("type", MESSAGE_TYPE.USER.ordinal());
      JsonObject p = new JsonObject();
      p.addProperty("id", id);
      p.addProperty("name", username);
      json.add("payload", p);
      session.getRemote().sendString(GSON.toJson(json));

    } else if (received.get("type").getAsInt() == MESSAGE_TYPE.FINDTWEETS
        .ordinal()) { // If the message is of type FINDTWEETS, we must get new
                      // tweets

      int id = payload.get("id").getAsInt();
      String username = payload.get("name").getAsString();

      if (username == null) {
        return;
      }

      User user;

      user = idToUserCache.getIfPresent(id);

      if (user == null) {
        user = new User(null, username, id, null);
        idToUserCache.put(id, user);
        UserDatabase.addUser(user);
      }

      // Get current tweets
      List<Tweet> tw = TwitterApiHandler.read(client);

      List<Story> storyList = new ArrayList<Story>();
      for (Tweet t : tw) {
        idToTweetCache.put(t.getId(), t);
        storyList.add(t);
      }

      // Filter tweets
      storyList = General.filterStories(storyList, user, true);

      // Listify
      List<List<String>> tweets = new ArrayList<List<String>>();
      for (Story t : storyList) {
        tweets.add(t.listify());
      }

      // Send UPDATETWEETS message to all users
      JsonObject json = new JsonObject();
      json.addProperty("type", MESSAGE_TYPE.UPDATETWEETS.ordinal());
      JsonObject p = new JsonObject();
      p.addProperty("id", id);
      p.addProperty("tweets", GSON.toJson(tweets));
      json.add("payload", p);
      for (Session sesh : sessions) {
        sesh.getRemote().sendString(GSON.toJson(json));
      }
    } else if (received.get("type").getAsInt() == MESSAGE_TYPE.FINDSTORIES
        .ordinal()) { // If the message is of type FINDSTORIES, we must get
                      // news stories

      int id = payload.get("id").getAsInt();
      String username = payload.get("name").getAsString();

      if (username == null) {
        return;
      }

      User user;

      user = idToUserCache.getIfPresent(id);

      if (user == null) {
        user = new User(null, username, id, null);
        idToUserCache.put(id, user);
        UserDatabase.addUser(user);
      }
      List<News> st;
      if (user.getLogin().equals("Alex")) {
        st = newsHandler.queryWebhoseWithQuery("politics");
      } else {
        st = newsHandler.queryWebHoseBreaking();
      }

      // Get current news pieces
      // List<News> st = newsHandler.queryWebHoseBreaking();

      List<Story> storyList = new ArrayList<>();
      for (News s : st) {
        if (storyToIdCache.getIfPresent(s) == null) {
          storyToIdCache.put(s, s.getId());
          idToStoryCache.put(s.getId(), s);
        }
        storyList.add(s);
      }

      // Filter stories
      storyList = General.filterStories(storyList, user, false);

      // Listifying
      List<List<String>> stories = new ArrayList<List<String>>();
      for (Story a : storyList) {
        stories.add(a.listify());
      }

      // Send UPDATESTORIES message to all users
      JsonObject json = new JsonObject();
      json.addProperty("type", MESSAGE_TYPE.UPDATESTORIES.ordinal());
      JsonObject p = new JsonObject();
      p.addProperty("id", id);
      p.addProperty("stories", GSON.toJson(stories));
      json.add("payload", p);
      for (Session sesh : sessions) {
        sesh.getRemote().sendString(GSON.toJson(json));
      }
    } else if (received.get("type").getAsInt() == MESSAGE_TYPE.VOTE.ordinal()) {

      // Update the user preferences here
      int id = payload.get("id").getAsInt();
      String username = payload.get("name").getAsString();
      int storyId = payload.get("story").getAsInt();
      String type = payload.get("type").getAsString();

      if (username == null) {
        return;
      }

      User user;

      user = idToUserCache.getIfPresent(id);
      if (user == null) {
        System.out.println("NEW USER");
        user = new User(null, username, id, null);
        idToUserCache.put(id, user);
        UserDatabase.addUser(user);
      }

      boolean upvote = payload.get("upVote").getAsBoolean();
      Story s = null;
      if (type.equals("news")) {
        s = idToStoryCache.getIfPresent(storyId);
      } else if (type.equals("tweet")) {
        s = idToTweetCache.getIfPresent(storyId);
      } else {
        return;
      }
      System.out.println(s.getCatScores());
      // System.out.println(s.getTitle());
      // System.out.println(s.getFirstLines());
      if (s != null && s.getCatScores() != null) {
        double happy = s.getCatScores().get("happy");
        double sad = s.getCatScores().get("sad");
        double political = s.getCatScores().get("political");
        double mystery = s.getCatScores().get("mystery");
        double love = s.getCatScores().get("love");
        user.updatePreference("happy", upvote, happy);
        user.updatePreference("sad", upvote, sad);
        user.updatePreference("political", upvote, political);
        user.updatePreference("mystery", upvote, mystery);
        user.updatePreference("love", upvote, love);
      }
      UserDatabase.addUpdate(user);
    } else if (received.get("type").getAsInt() == MESSAGE_TYPE.SEARCH
        .ordinal()) {
      System.out.println("searching");

      // Find the stories containing the keyword
      String word = payload.get("word").getAsString();
      List<Story> tweets = General.searchStories(idToTweetCache.asMap(), word);
      List<Story> stories = General.searchStories(idToStoryCache.asMap(), word);

      tweets = General.rankStories(tweets, true);
      stories = General.rankStories(stories, false);

      List<List<String>> tweetList = new ArrayList<List<String>>();
      List<List<String>> storyList = new ArrayList<List<String>>();
      for (Story t : tweets) {
        tweetList.add(t.listify());
      }
      for (Story s : stories) {
        storyList.add(s.listify());
      }

      // Send UPDATESTORIES message to all users
      JsonObject json = new JsonObject();
      json.addProperty("type", MESSAGE_TYPE.SEARCH.ordinal());
      // consider calling the stories search thing in webhose
      JsonObject p = new JsonObject();
      p.addProperty("tweets", GSON.toJson(tweetList));
      p.addProperty("stories", GSON.toJson(storyList));
      json.add("payload", p);
      for (Session sesh : sessions) {
        sesh.getRemote().sendString(GSON.toJson(json));
      }

    } else if (received.get("type").getAsInt() == MESSAGE_TYPE.USERPREFERENCE
        .ordinal()) {
      // Find the stories containing the keyword
      int id = payload.get("id").getAsInt();
      String username = payload.get("name").getAsString();
      System.out.println("id: " + id + " | username: " + username);
      User user = null;
      // user = idToUserCache.getIfPresent(id);
      try {
        user = l1.load(id);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      if (user == null) {
        user = new User(null, username, id, null);
        idToUserCache.put(id, user);
        UserDatabase.addUser(user);
      }

      List<List<String>> prefs = user.getPrefList();

      // Send USERPREFERENCE message to all users
      JsonObject json = new JsonObject();
      json.addProperty("type", MESSAGE_TYPE.USERPREFERENCE.ordinal());
      JsonObject p = new JsonObject();
      p.addProperty("preferences", GSON.toJson(prefs));
      json.add("payload", p);
      // for (Session sesh : sessions) {
      session.getRemote().sendString(GSON.toJson(json));
      // }

    }
  }

}
