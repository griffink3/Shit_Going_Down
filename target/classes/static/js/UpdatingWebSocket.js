const MESSAGE_TYPE = {
  CONNECT: 0,
  FINDTWEETS: 1,
  FINDSTORIES: 2,
  UPDATETWEETS: 3,
  UPDATESTORIES: 4,
  USER: 5,
  VOTE: 6,
  SEARCH: 7,
  USERPREFERENCE: 8
};

let conn;
let myId = -1;
let myName = "";

const setup_connection = () => {
  // Setting up the WebSocket connection and assign it to `conn`
  conn = new WebSocket("ws://localhost:4567/find");
  conn.onerror = err => {
    console.log('Connection error:', err);
    return;
  };
  setTimeout(function(){
      var json = {};
      json.type = MESSAGE_TYPE.USER;
      json.payload = {};
      var tempuser=prompt("Enter a username:");
      while(!tempuser){
          var tempuser=prompt("Enter a valid username:");
      }
      json.payload.username = tempuser;
      conn.send(JSON.stringify(json));
      /*
      alertify.prompt("Enter a username:", function (e, str) {
          // str is the input text
          if (e) {
              // user clicked "ok"
              json.payload.username = str;
          } else {
              // user clicked "cancel"
              json.payload.username = "default";
          }
          conn.send(JSON.stringify(json));
      }, "Username");*/
      console.log("WTF");
  },1000);
}

// Setup the WebSocket connection for live updating of new sources.
const setup_live_updating = () => {
  conn.onmessage = msg => {
    const data = JSON.parse(msg.data);
    switch (data.type) {
      default:
        console.log('Unknown message type!', data.type);
        break;
      case MESSAGE_TYPE.CONNECT:

        // Get username here and then send message to the backend
        var json = {};
        json.type = MESSAGE_TYPE.USER;
        json.payload = {};
        json.payload.username =myId;
        conn.send(JSON.stringify(json));
        break;
      case MESSAGE_TYPE.USER:
        // Assigning myId
        myId = data.payload.id;
        myName = data.payload.name;
        console.log("WTF");
        get_user_pref();
        //console.log(myId+" | "+myName);
      case MESSAGE_TYPE.UPDATETWEETS:
        console.log("UPDATING TWEETS");
        // Updates with the new news stories and new tweets - finish doing this
        //const $user = document.getElementById("userId");
        const tweets = data.payload.tweets;
        parseTweets(tweets);
        break;
      case MESSAGE_TYPE.UPDATESTORIES:
        // Updates with the new news stories and new tweets - finish doing this
        console.log("UPDATING STORIES");
        //const $user = document.getElementById("userId");
        const stories = data.payload.stories;
        parseStories(stories);
        break;
      case MESSAGE_TYPE.SEARCH:
        // Getting a message with the stories found
        const rankedTweets = data.payload.tweets;
        const rankedStories = data.payload.stories;
        // parse ranked tweets here
        parseSearchedNews(rankedStories);
        parseSearchedTweets(rankedTweets);
        break;
      case MESSAGE_TYPE.USERPREFERENCE:
        // Getting a message with the stories found
        const preferences = data.payload.preferences; // as a list of list of strings
        // DO SOMETHING WITH THE USER PREFERENCES
        let parsedPrefs = JSON.parse(preferences);
        updatePrefChart(preferences);
        //$("#preferences").empty();
        //$("#preferences").append(parsedPrefs);
        break;
    }
  };
}

// Should be called from the front end when it needs to update tweets
const find_new_tweets = () => {
  // Send a FIND message to the server using `conn`
  var json = {};
  json.type = MESSAGE_TYPE.FINDTWEETS;
  json.payload = {};
  json.payload.id = myId;
  json.payload.name = myName;
  conn.send(JSON.stringify(json));
}

// Should be called from the front end when it needs to update news articles
const find_new_stories = () => {
  // Send a FIND message to the server using `conn`
  var json = {};
  json.type = MESSAGE_TYPE.FINDSTORIES;
  json.payload = {};
  json.payload.id = myId;
  json.payload.name = myName;
  conn.send(JSON.stringify(json));
}

const vote = (upVote, storyId, storyType) => {
  // Send a vote message to the server using `conn`
  var json = {};
  json.type = MESSAGE_TYPE.VOTE;
  json.payload = {};
  json.payload.id = myId;
  json.payload.name = myName;
  json.payload.story = storyId;
  json.payload.upVote = upVote;
  json.payload.type=storyType;
  conn.send(JSON.stringify(json));
  get_user_pref();
}

// Should be called from the front end when the user needs to search by key word
const search_by_word = (word) => {
  // Send a SEARCH message to the server using `conn`
  var json = {};
  json.type = MESSAGE_TYPE.SEARCH;
  json.payload = {};
  json.payload.word = word;
  json.payload.id = myId;
  conn.send(JSON.stringify(json));
}

// Should be called when the front end wants to update the user's weighted preference
const get_user_pref = () => {
  // Send a USERPREFERENCE message to the server using `conn`
  var json = {};
  json.type = MESSAGE_TYPE.USERPREFERENCE;
  json.payload = {};
  if(myName=='Alex'){
    json.payload.id = 20;
  }else{
    json.payload.id = myId;
  }
  json.payload.name = myName;
  conn.send(JSON.stringify(json));
}

function get_username() {
  alertify.prompt("Enter a username:", function (e, str) {
      // str is the input text
      if (e) {
          // user clicked "ok"
          return str;
      } else {
          // user clicked "cancel"
          return "default";
      }
  }, "Username");
}
