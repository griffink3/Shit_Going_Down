package edu.brown.cs.sgd.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.event.Event;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

import edu.brown.cs.sgd.general.General;
import edu.brown.cs.sgd.processing.TwitterProcessor;

/**
 * Handlers getting data from the Twitter's Streaming API using Hosebird Client.
 * 
 * @author gk16
 */
public class TwitterApiHandler {

  // These are the available client numbers (from closed clients)
  private static final List<Integer> availNums = new ArrayList<Integer>();

  // Setting up blocking queues: Be sure to size these properly based on
  // expected TPS of your stream
  private static final BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(
      100000);
  private static final BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<Event>(
      1000);

  // Declaring the host to connect to, the endpoint, and authentication (basic
  // auth or oauth)
  private static final Hosts hosebirdHosts = new HttpHosts(
      Constants.STREAM_HOST);
  private static final StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();

  // Optional: set up some followings and track terms - can change these later
  private static final List<Long> followings = new ArrayList<Long>(
      Arrays.asList(1234L, 566788L));
  private static final List<String> terms = new ArrayList<String>(
      Arrays.asList("twitter", "api"));

  // These secrets should be read from a config file
  private static final Authentication hosebirdAuth = new OAuth1(
      "9GUns0QstIFeRh7rNTdyPmbRX",
      "T9Tzn2hwTHK4CLSKWgIwIauTLmnQA30qcIrutZraDJK5zsTcvW",
      "2193423985-OwpDiX4S81svnoBhV31ZdkKs7QHKXAr4NUDJLRn",
      "0MVFuTINCdGpTVcFc62QQABlLEvjaS4GbHEuQITBEmNh9");

  // List of current clients
  private static final List<Client> clients = new ArrayList<Client>();

  /**
   * An instance of the preference information of a single user.
   */
  public TwitterApiHandler() {
    hosebirdEndpoint.followings(followings);
    hosebirdEndpoint.trackTerms(terms);
  }

  /**
   * Creates a new client and returns it.
   * 
   * @return a new client
   */
  public Client createClient() {
    StringBuilder sb = new StringBuilder();
    sb.append("Hosebird-Client-");
    if (availNums.isEmpty()) {
      sb.append(Integer.toString(availNums.size()));
    } else {
      sb.append(Integer.toString(availNums.get(0)));
    }
    ClientBuilder builder = new ClientBuilder().name(sb.toString())
        .hosts(hosebirdHosts).authentication(hosebirdAuth)
        .endpoint(hosebirdEndpoint)
        .processor(new StringDelimitedProcessor(msgQueue))
        .eventMessageQueue(eventQueue);
    Client hosebirdClient = builder.build();
    clients.add(hosebirdClient);
    // Attempts to establish a connection.
    try {
      hosebirdClient.connect();
    } catch (Exception e) {
      General.printErr(e.getMessage());
    }
    return hosebirdClient;
  }

  /**
   * Closes the specified client.
   *
   * @param client
   *          the client to close
   */
  public static void closeConnection(Client client) {
    try {
      client.stop();
    } catch (Exception e) {
      General.printErr(e.getMessage());
    }
  }

  /**
   * Reads from a given twitter client.
   *
   * @param client
   *          client to read from
   * @return returns 500 tweets as stories
   */
  public static List<Tweet> read(Client client) {
    // All on same thread
    List<Tweet> tweets = new ArrayList<>();
    while (!client.isDone()) {
      String msg;
      try {
        msg = msgQueue.take();
        // Do something with the returned story
        if (tweets.size() < General.NUM_TWEETS) {
          tweets.add(TwitterProcessor.process(msg));
        } else if (tweets.size() >= General.NUM_TWEETS) {
          return tweets;
        }
      } catch (InterruptedException e) {
        General.printErr(e.getMessage());
      }
    }
    return tweets;
  }

}
