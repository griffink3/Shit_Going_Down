package edu.brown.cs.sgd.general;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.twitter.hbc.core.Client;

import edu.brown.cs.sgd.api.News;
import edu.brown.cs.sgd.api.Story;
import edu.brown.cs.sgd.api.Tweet;
import edu.brown.cs.sgd.api.TwitterApiHandler;
import edu.brown.cs.sgd.api.WebhoseApiHandler;
import freemarker.template.Configuration;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import spark.ExceptionHandler;
import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

/**
 * Root class for the CS 32 Final project.
 *
 */
public final class Main {

  private static final int DEFAULT_PORT = 4567;
  private static final Gson GSON = new Gson();
  private static final TwitterApiHandler handler = new TwitterApiHandler();
  private static final WebhoseApiHandler newsHandler =
      new WebhoseApiHandler();
  private static final Client client = handler.createClient();
  private static final Map<String, Integer> nameToId =
      new HashMap<String, Integer>();
  private static int currId = 0;
  private static final String USER_DB = "data/users/user.sqlite3";

  public static TwitterApiHandler getHandler() {
    return handler;
  }

  /**
   * The initial method called when execution begins.
   *
   * @param args
   *          An array of command line arguments
   * @throws InterruptedException
   */
  public static void main(String[] args) throws InterruptedException {
    new Main(args).run();
  }

  private String[] args;

  private Main(String[] args) {
    this.args = args;
  }

  private void run() throws InterruptedException {
    OptionParser parser = new OptionParser();
    parser.accepts("gui");
    parser.accepts("port").withRequiredArg().ofType(Integer.class)
        .defaultsTo(DEFAULT_PORT);
    OptionSet options = parser.parse(args);

    if (options.has("gui")) {
      runSparkServer((int) options.valueOf("port"));
    }
    UserDatabase.startUpConn(USER_DB);

    List<News> st = newsHandler.queryWebHoseBreaking();
    List<Story> s = new ArrayList<>();
    for (News n : st) {
      s.add(n);
    }
  }

  private static FreeMarkerEngine createEngine() {
    Configuration config = new Configuration();
    File templates = new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.printf("ERROR: Unable use %s for template loading.%n",
          templates);
      System.exit(1);
    }
    return new FreeMarkerEngine(config);
  }

  private void runSparkServer(int port) {
    Spark.port(port);
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.exception(Exception.class, new ExceptionPrinter());

    FreeMarkerEngine freeMarker = createEngine();

    // Setup Spark Routes
    Spark.webSocket("/find", UpdatingWebSocket.class);
    Spark.get("/sgd", new StartHandler(), freeMarker);
    Spark.get("/home", new HomeHandler(), freeMarker);
    Spark.post("/login", new LoginHandler(), freeMarker);
    Spark.get("/", new MainHandler(), freeMarker);
    Spark.post("/getTweets", new TweetHandler());
    Spark.post("/getStories", new StoryHandler());
  }

  /**
   * Handles requests to the start page of sgd.
   * 
   */
  private static class StartHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("title", "SGD-Start");
      return new ModelAndView(variables, "start.ftl");
    }
  }

  /**
   * handles requests to the front page of sgd.
   *
   * @author sgd
   *
   */
  private static class HomeHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("title", "SGD-Start");
      return new ModelAndView(variables, "login.ftl");
    }
  }

  /**
   * Handles login and creating users.
   *
   * @author sgd
   *
   */
  private static class LoginHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String username = qm.value("username");
      Map<String, Object> variables =
          ImmutableMap.of("title", "SGD-LoggedIn", "username", username);
      System.out.println("USERNAME: " + username);
      return new ModelAndView(variables, "index.ftl");
    }
  }

  /**
   * Handles requesting tweets.
   *
   * @author sgd
   *
   */
  private static class TweetHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      List<Tweet> list = TwitterApiHandler.read(client);
      List<List<String>> tweets = new ArrayList<List<String>>();
      for (Tweet s : list) {
        tweets.add(s.listify());
      }
      Map<String, Object> variables;
      if (tweets == null) {
        System.out.println("ERROR: while fetching tweets.");
        variables = ImmutableMap.of();
      } else {
        variables = ImmutableMap.of("tweets", tweets);
      }
      return GSON.toJson(variables);
    }
  }

  /**
   * Handles requesting stories.
   *
   * @author sgd
   *
   */
  private static class StoryHandler implements Route {
    @Override
    public String handle(Request req, Response res) {
      List<List<String>> stories =
          newsHandler.listNews(newsHandler.queryWebHoseBreaking());
      Map<String, Object> variables;
      if (stories == null) {
        System.out.println("ERROR: while fetching stories.");
        variables = ImmutableMap.of();
      } else {
        variables = ImmutableMap.of("stories", stories);
      }
      return GSON.toJson(variables);
    }
  }

  /**
   * Handle requests to the front page of the SGD website.
   *
   * @author knoh1
   *
   */
  private static class MainHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      QueryParamsMap qm = req.queryMap();
      String username = qm.value("username");
      if (nameToId.containsKey(username)) {

      } else {
        nameToId.put(username, currId);
        currId++;
      }
      Map<String, Object> variables =
          ImmutableMap.of("title", "SGD - Home", "message", "index test");
      return new ModelAndView(variables, "index.ftl");
    }
  }

  /**
   * Display an error page when an exception occurs in the server.
   */
  private static class ExceptionPrinter implements ExceptionHandler {
    public void handle(Exception e, Request req, Response res) {
      res.status(500);
      StringWriter stacktrace = new StringWriter();
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
    }
  }
}
