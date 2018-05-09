let map;
let infowindow;
const twitterIcon = "../img/twitter-icon.png";
const nytimesIcon = "../img/nytimes-icon.png";
const aljazeeraIcon = "../img/aljazeera-icon.png";
const ewIcon = "../img/ew.com-icon.png";
const reutersIcon = "../img/reuters-icon.png";
const yahooIcon = "../img/yahoo-icon.png";
const buzzfeedIcon = "../img/buzzfeed-icon.png";
const timesofindiaIcon = "../img/timesofindia-icon.png";
const bbcIcon = "../img/bbc-icon.png";
const politicoIcon = "../img/politico-icon.png";
const nbcIcon = "../img/nbcnews-icon.png";
const espnIcon = "../img/espn-icon.png";
const googleIcon = "../img/google-icon.png";
const cnnIcon = "../img/cnn-icon.png";
const apIcon = "../img/ap-icon.png";
const abcIcon = "../img/abcnews-icon.png";
const bloombergIcon = "../img/bloomberg-icon.png";
let options_markerclusterer = {
    zoomOnClick: false,
    gridSize: 90,
    minimumClusterSize: 3,
    averageCenter: true,
    imagePath: 'https://developers.google.com/maps/documentation/javascript/examples/markerclusterer/m'
};
let tweetMarkerCluster;
let tweetIDs = {};
let newsIDs = {};

let activeMarkers={};

let heatmap;
let heatMapData = [];

let happyHeatmap;
let happyData=[];

let sadHeatmap;
let sadData=[];

let politicalHeatmap;
let politicalData=[];

let loveHeatmap;
let loveData=[];

let mixedHeatmap1;
let mixedData1=[];
let mixedHeatmap2;
let mixedData2=[];
let mixedHeatmap3;
let mixedData3=[];
let mixedHeatmap4;
let mixedData4=[];

let activeHeatmap='happy';

let redGradient=[
          'rgba(255, 211, 209, 0)',
          'rgba(255, 211, 209, 1)',
          'rgba(255, 194, 189, 1)',
          'rgba(255, 177, 169, 1)',
          'rgba(255, 160, 149, 1)',
          'rgba(255, 143, 129, 1)',
          'rgba(255, 126, 109, 1)',
          'rgba(255, 109, 89, 1)',
          'rgba(255, 92, 69, 1)',
          'rgba(255, 75, 49, 1)',
          'rgba(255, 58, 29, 1)',
          'rgba(255, 41, 10, 1)'
      ];

let blueGradient=[
        'rgba(214, 226, 255, 0)',
        'rgba(214, 226, 255, 1)',
        'rgba(192, 206, 250, 1)',
        'rgba(171, 187, 245, 1)',
        'rgba(149, 167, 241, 1)',
        'rgba(128, 148, 236, 1)',
        'rgba(107, 128, 232, 1)',
        'rgba(85, 109, 227, 1)',
        'rgba(64, 89, 222, 1)',
        'rgba(42, 70, 218, 1)',
        'rgba(21, 50, 213, 1)',
        'rgba(0, 31, 209, 1)'
    ];

let greenGradient=[
        'rgba(199, 255, 208, 0)',
        'rgba(199, 255, 208, 1)',
        'rgba(179, 238, 188, 1)',
        'rgba(159, 222, 169, 1)',
        'rgba(139, 206, 149, 1)',
        'rgba(119, 189, 130, 1)',
        'rgba(99, 173, 111, 1)',
        'rgba(79, 157, 91, 1)',
        'rgba(59, 140, 72, 1)',
        'rgba(39, 124, 52, 1)',
        'rgba(19, 108, 33, 1)',
        'rgba(0, 92, 14, 1)'
    ];

let purpleGradient=[
        'rgba(255, 199, 249, 0)',
        'rgba(255, 199, 249, 1)',
        'rgba(244, 179, 238, 1)',
        'rgba(233, 159, 227, 1)',
        'rgba(222, 139, 216, 1)',
        'rgba(212, 119, 206, 1)',
        'rgba(201, 99, 195, 1)',
        'rgba(190, 79, 184, 1)',
        'rgba(180, 59, 174, 1)',
        'rgba(169, 39, 163, 1)',
        'rgba(158, 19, 152, 1)',
        'rgba(148, 0, 142, 1)'
    ];

function geocode(address) {
    var geocoder = new google.maps.Geocoder();
    geocoder.geocode({
        'address': address
    }, function(results, status) {
        if (status == 'OK') {
            var location = results[0].geometry.location; // reference LatLng value
            //createMarker(location);
            //map.setZoom(7);
            //map.setCenter(location);
            return location;
        } else {
            console.log("GEOCODE ERROR: " + status);
            return null;
        }
    });
}

function unicodeToChar(text) {
    return text.replace(/\\u[\dA-F]{4}/gi, function(match) {
        return String.fromCharCode(parseInt(match.replace(/\\u/g, ''), 16));
    });
}

function parseTweets(tweetsJSON) {
    const postParameters = {};
    let tweets = JSON.parse(tweetsJSON);
    //console.log(tweets);
    //let tweets = responseObject.tweets
    tweets = tweets.filter(function(n) {
        return (n[0] != undefined || n[1] != undefined || n[2] != undefined || n[3] != undefined || n[4] != undefined || n[5] != undefined)
    });

    let newTweetMarkers = tweets.map(function(tweet, i) {
        //const tempLoc = new google.maps.LatLng((Math.random() * (85 * 2) - 85), (Math.random() * (180 * 2) - 180));
        const tweetLink = unicodeToChar(tweet[0]);
        if (tweetLink in tweetIDs) {
            return null;
        } else {
            tweetIDs[tweetLink] = 1;
        }
        const tweetContent = unicodeToChar(tweet[1]);
        const tweetTitle = unicodeToChar(tweet[3]);
        const tweetLocation = unicodeToChar(tweet[4]);
        const tweetSource = unicodeToChar(tweet[5]);
        const tweetHearts = parseInt(tweet[6]);
        const tweetRT = parseInt(tweet[7]);
        let userImg = unicodeToChar(tweet[8]).replace(/\\/g, "");
        userImg = [
            userImg.slice(0, 4),
            ":",
            userImg.slice(4)
        ].join('');
        const author = unicodeToChar(tweet[9]);
        const time = tweet[10];
        const storyID = parseInt(tweet[11]);
        let happyScore = undefined;
        let sadScore = undefined;
        let politicalScore = undefined;
        let mysteryScore = undefined;
        let loveScore = undefined;
        if (tweet.length > 12) {
            happyScore = parseFloat(tweet[12]);
            sadScore = parseFloat(tweet[13]);
            politicalScore = parseFloat(tweet[14]);
            mysteryScore = parseFloat(tweet[15]);
            loveScore = parseFloat(tweet[16]);
        }
        if (tweetLocation == undefined || tweetLocation == "null" || !tweetLocation) {
            return null;
        }
        $.get("https://maps.googleapis.com/maps/api/geocode/json?address=" + tweetLocation + "&key=AIzaSyBe0qgKLRCgikyy45HdmBfXhuvgMTg7EGk", function(data, status) {
            if (status == "success") {
                if (data.results.length == 0) {
                    return null;
                }
                let tweetLatLng = data.results[0].geometry.location;
                if (tweetLatLng == undefined) {
                    return null;
                }
                //heatMapData.push({location: new google.maps.LatLng(tweetLatLng), weight: tweetHearts});
                //heatMapData.push(new google.maps.LatLng(tweetLatLng));
                var tempMarker = new google.maps.Marker({
                    position: tweetLatLng,
                    map: map,
                    draggable: false,
                    animation: google.maps.Animation.DROP,
                    icon: twitterIcon,
                    title: tweetTitle,
                    content: tweetContent,
                    source: tweetSource,
                    link: tweetLink,
                    hearts: tweetHearts,
                    retweets: tweetRT,
                    userImg: userImg,
                    author: author,
                    time: time,
                    type: 'tweet',
                    happy: happyScore,
                    sad: sadScore,
                    political: politicalScore,
                    love: loveScore,
                    storyID: storyID
                });
                setTimeout(function() {
                    //tempMarker.setMap(null);
                    tweetMarkerCluster.removeMarker(tempMarker);
                    delete tempMarker;
                    delete activeMarkers[storyID];
                }, 30000);
                //return tempMarker;
                // Add a marker clusterer to manage the markers.
                tweetMarkerCluster.addMarker(tempMarker);
                activeMarkers[storyID]=tempMarker;
            } else {
                console.log(status);
                return null;
            }
        });

    });
    setupClickHandlers();
}

function openNav() {
    document.getElementById("mySidenav").style.width = "20%";
}

function closeNav() {
    document.getElementById("mySidenav").style.width = "0";
}

function parseSearchedTweets(tweetsJSON){
    let stories = JSON.parse(tweetsJSON);
    //console.log(stories);
    openNav();
    const $searchList=$("#tweets-search-results-ul");
    $searchList.empty();
    for(let i=0;i<Math.min(10,stories.length);i++){
        const storyID=stories[i][11];
        $searchList.append('<li id="'+storyID+'" style="color:blue;">'+unicodeToChar(stories[i][1])+'</li>');
    }
    $("li").click(function(e){
        console.log(this.id);
        if(this.id in activeMarkers){
            google.maps.event.trigger(activeMarkers[this.id], 'click');
        }else{
            console.log("Marker doesn't exist anymore.");
        }
    });
}

function parseSearchedNews(storiesJSON){
    let stories = JSON.parse(storiesJSON);
    openNav();
    const $searchList=$("#news-search-results-ul");
    $searchList.empty();
    for(let i=0;i<Math.min(10,stories.length);i++){
        const storyID=stories[i][10];
        $searchList.append('<li id="'+storyID+'">'+stories[i][9]+'</li>');
    }
}

function parseStories(storiesJSON) {
    let stories = JSON.parse(storiesJSON);
    //console.log(stories);
    //let tweets = responseObject.tweets
    stories = stories.filter(function(n) {
        return (n[0] != undefined || n[1] != undefined || n[2] != undefined || n[3] != undefined || n[4] != undefined || n[5] != undefined)
    });

    let newStoryMarkers = stories.map(function(tweet, i) {
        //const tempLoc = new google.maps.LatLng((Math.random() * (85 * 2) - 85), (Math.random() * (180 * 2) - 180));
        const storyLocation = tweet[0];
        const storySource = tweet[1];

        const storyPerfScore = tweet[2];
        const storyDomainRank = tweet[3];
        const storyTime = tweet[4];
        const storyMainImg = tweet[5];

        const storyContent = tweet[7];
        const storyLink = tweet[8];
        const storyTitle = tweet[9];

        const storyID = parseInt(tweet[10]);
        let happyScore = undefined;
        let sadScore = undefined;
        let politicalScore = undefined;
        let mysteryScore = undefined;
        let loveScore = undefined;
        if (tweet.length > 12) {
            happyScore = parseFloat(tweet[11]);
            sadScore = parseFloat(tweet[12]);
            politicalScore = parseFloat(tweet[13]);
            mysteryScore = parseFloat(tweet[14]);
            loveScore = parseFloat(tweet[15]);
        }
        if (storyLocation == undefined || storyLocation == "null" || !storyLocation) {
            return null;
        }
        $.get("https://maps.googleapis.com/maps/api/geocode/json?address=" + storyLocation + "&key=AIzaSyBe0qgKLRCgikyy45HdmBfXhuvgMTg7EGk", function(data, status) {
            if (status == "success") {
                if (data.results.length == 0) {
                    return null;
                }
                let storyLatLng = data.results[0].geometry.location;
                if (storyLatLng == undefined) {
                    return null;
                }
                let tempIcon = nytimesIcon;
                if (storySource == 'www.nytimes.com') {
                  tempIcon = nytimesIcon;
                } else if (storySource == 'edition.cnn.com') {
                  tempIcon = cnnIcon;
                } else if (storySource == 'www.bbc.com') {
                  tempIcon = bbcIcon;
                } else if (storySource == 'au.news.yahoo.com' || storySource == 'www.yahoo.com') {
                  tempIcon = yahooIcon;
                } else if (storySource == 'www.bloomberg.com') {
                  tempIcon = bloombergIcon;
                } else if (storySource == 'www.aljazeera.com') {
                  tempIcon = aljazeeraIcon;
                } else if (storySource == 'www.ew.com') {
                  tempIcon = ewIcon;
                } else if (storySource == 'www.reuters.com') {
                  tempIcon = reutersIcon;
                } else if (storySource == 'www.buzzfeed.com') {
                  tempIcon = buzzfeedIcon;
                } else if (storySource == 'timesofindia.indiatimes.com') {
                  tempIcon = timesofindiaIcon;
                } else if (storySource == 'www.politico.com') {
                  tempIcon = politicoIcon;
                } else if (storySource == 'www.nbcnews.com') {
                  tempIcon = nbcIcon;
                } else if (storySource == 'www.espn.com') {
                  tempIcon = espnIcon;
                } else if (storySource == 'news.google.com') {
                  tempIcon = googleIcon;
                } else if (storySource == 'edition.cnn.com' || storySource == 'rss.cnn.com') {
                  tempIcon = cnnIcon;
                } else if (storySource == 'www.apnews.com') {
                  tempIcon = apIcon;
                } else if (storySource == 'www.abcnews.com') {
                  tempIcon = abcIcon;
                } else if (storySource == 'www.bloomberg.com') {
                  tempIcon = bloombergIcon;
                }
                //console.log(storySource);
                //heatMapData.push({location: new google.maps.LatLng(storyLatLng), weight: storyPerfScore});
                //heatMapData.push(new google.maps.LatLng(storyLatLng));
                var tempMarker = new google.maps.Marker({
                    position: storyLatLng,
                    map: map,
                    draggable: false,
                    animation: google.maps.Animation.DROP,
                    icon: tempIcon,
                    title: storyTitle,
                    content: storyContent,
                    source: storySource,
                    link: storyLink,
                    perfScore: storyPerfScore,
                    domainRank: storyDomainRank,
                    mainImg: storyMainImg,
                    time: storyTime,
                    type: 'news',
                    happy: happyScore,
                    sad: sadScore,
                    political: politicalScore,
                    love: loveScore,
                    storyID: storyID
                });
                setTimeout(function() {
                    //tempMarker.setMap(null);
                    tweetMarkerCluster.removeMarker(tempMarker);
                    delete tempMarker;
                    delete activeMarkers[storyID];
                }, 300000);
                //return tempMarker;
                // Add a marker clusterer to manage the markers.
                tweetMarkerCluster.addMarker(tempMarker);
                activeMarkers[storyID]=tempMarker;
            } else {
                console.log(status);
                return null;
            }
        });
    });
    setupClickHandlers();
}

function setupClickHandlers() {
    $(document).ajaxStop(function() {
        google.maps.event.addListener(tweetMarkerCluster, 'clusterclick', function(cluster) {

            var markers = cluster.getMarkers();
            //console.log(markers);
            var titles = [];

            for (i = 0; i < markers.length; i++) {
                titles.push(markers[i].getTitle() + '<br>');
            }
            //if (map.getZoom() <= tweetMarkerCluster.getMaxZoom()) {
            var contentStringStart = '<div id="content" style="max-height:500px;width:310px;text-align:left;padding-left:10px;margin-left:-10px;">' + '<div id="siteNotice">' + '</div>';
            var contentStringEnd = '</div>';
            var titleHeader = '<h1 id="firstHeading" class="firstHeading" style="color:black;">' + markers.length + ' Stories in this Area:</h1>';
            var bodyContent = '<div id="bodyContent">';
            for (i = 0; i < markers.length; i++) {
                if (markers[i].type === 'news') {
                    bodyContent += '<h3 class="newsTitle"><a id="newsclick' + markers[i].storyID + '" href="' + markers[i].link + '" target="_blank">' + markers[i].title + '</a></h3>';
                }
            }
            bodyContent += '</div>';
            infoWindow.setContent(contentStringStart + titleHeader + bodyContent + contentStringEnd);
            infoWindow.setPosition(cluster.getCenter());
            infoWindow.open(map);
            for (i = 0; i < markers.length; i++) {
                if (markers[i].type === 'tweet') {
                    twttr.widgets.createTweet(markers[i].link.toString(), document.getElementById("content"), {
                        linkColor: "#55acee",
                        theme: "light",
                        width: "300px",
                        conversation: "none",
                        cards: "hidden"
                    });
                }
            }
            google.maps.event.addListenerOnce( cluster, "visible_changed", function() {
               infoWindow.close();
           });

            //}
        });

        for (i = 0; i < tweetMarkerCluster.getMarkers().length; i++) {
            var marker = tweetMarkerCluster.getMarkers()[i];
            google.maps.event.addListener(marker, 'click', (function(marker) {
                return function() {
                    if (marker.type === 'tweet') {
                        var divID = "tweetContent" + marker.link;
                        if ($("#" + divID).length != 0) {
                            return;
                        }
                        var contentStringStart = '<div id="' + divID + '" style="max-height:500px;width:310px;text-align:left;padding-left:10px;margin-left:-10px;">';
                        var contentStringEnd = '</div>';
                        var titleHeader = '<h1 id="firstHeading" class="firstHeading">' + marker.title + '</h1>';
                        var bodyContent = '<div id="bodyContent">' + '</div>';
                        var voter = '<div class="vote circle"><div class="increment up" id="' + marker.storyID + '"></div><div class="increment down" id="' + marker.storyID + '"></div><div class="count">0</div></div>';
                        infoWindow.setContent(contentStringStart + voter + contentStringEnd);
                        infoWindow.setPosition(marker.getPosition());
                        //console.log(marker.link.toString()+" | "+document.getElementById(divID));
                        infoWindow.open(map, this);
                        google.maps.event.addListenerOnce( marker, "visible_changed", function() {
                           infoWindow.close();
                       });
                        twttr.widgets.createTweet(marker.link.toString(), document.getElementById(divID), {
                            linkColor: "#55acee",
                            theme: "light",
                            width: "300px"
                        });
                        $(".increment").click(function() {
                            console.log(this.id);
                            var count = parseInt($("~ .count", this).text());
                            if ($(this).hasClass("up")) {
                                var count = count + 1;
                                $("~ .count", this).text(count);
                                vote(true, this.id, 'tweet');
                            } else {
                                var count = count - 1;
                                $("~ .count", this).text(count);
                                vote(false, this.id, 'tweet');
                            }
                        });
                    } else if (marker.type === 'news') {
                        var contentStringStart = '<div id="news-content" style="max-height:500px;width:310px;text-align:left;padding-left:10px;margin-left:-10px;">';
                        var bodyContent = '<div id="bodyContent">';
                        bodyContent += '<h2>' + marker.title + '</h2>';
                        bodyContent += '<h4 id="source">Source: ' + marker.source + '</h4>';
                        var voter = '<div class="vote circle"><div class="increment up" id="' + marker.storyID + '"></div><div class="increment down" id="' + marker.storyID + '"></div><div class="count">0</div></div>';
                        if (marker.content.length > 700) {
                            let shortContent = marker.content.substring(0, 700);
                            bodyContent += shortContent + '<b> ... </br>';
                        } else {
                            bodyContent += '<p id="content">' + '<span style="white-space: pre-line">' + marker.content + '</span></p>';
                        }
                        bodyContent += '<p class="link"><a href="' + marker.link + '" target="_blank">' + "Click here for the full story." + '</a></p>';
                        bodyContent += '</div>'
                        infoWindow.setContent(contentStringStart + voter + bodyContent + '</div>');
                        infoWindow.setPosition(marker.getPosition());
                        infoWindow.open(map, this);
                        google.maps.event.addListenerOnce( marker, "visible_changed", function() {
                           infoWindow.close();
                       });
                        $(".increment").click(function() {
                            console.log(this.id);
                            var count = parseInt($("~ .count", this).text());
                            if ($(this).hasClass("up")) {
                                var count = count + 1;
                                $("~ .count", this).text(count);
                                vote(true, this.id, 'news');
                            } else {
                                var count = count - 1;
                                $("~ .count", this).text(count);
                                vote(false, this.id, 'news');
                            }
                        });
                    }
                }
            })(marker));
        }

        heatMapData=[];

        happyData=[];
        sadData=[];
        politicalData=[];
        loveData=[];

        let tempMarkers = tweetMarkerCluster.getMarkers();
        for (var j = 0; j < tempMarkers.length; j++) {
            //console.log(tempMarkers[j].happy+" | "+tempMarkers[j].sad+" | "+tempMarkers[j].political+" | "+tempMarkers[j].love);
            if(tempMarkers[j].happy!=undefined){
                happyData.push({location: tempMarkers[j].getPosition(), weight: tempMarkers[j].happy});
                /*if(tempMarkers[j].happy>tempMarkers[j].sad && tempMarkers[j].happy>tempMarkers[j].political && tempMarkers[j].happy>tempMarkers[j].love){
                    mixedData1.push({location: tempMarkers[j].getPosition(), weight: tempMarkers[j].happy});
                }*/
            }
            if(tempMarkers[j].sad!=undefined){
                sadData.push({location: tempMarkers[j].getPosition(), weight: tempMarkers[j].sad});
                // if(tempMarkers[j].sad>tempMarkers[j].happy && tempMarkers[j].sad>tempMarkers[j].political && tempMarkers[j].sad>tempMarkers[j].love){
                //     mixedData2.push({location: tempMarkers[j].getPosition(), weight: tempMarkers[j].sad});
                // }
            }
            if(tempMarkers[j].political!=undefined){
                politicalData.push({location: tempMarkers[j].getPosition(), weight: tempMarkers[j].political});
                // if(tempMarkers[j].political>tempMarkers[j].sad && tempMarkers[j].political>tempMarkers[j].happy && tempMarkers[j].political>tempMarkers[j].love){
                //     mixedData3.push({location: tempMarkers[j].getPosition(), weight: tempMarkers[j].political});
                // }
            }
            if(tempMarkers[j].love!=undefined){
                loveData.push({location: tempMarkers[j].getPosition(), weight: tempMarkers[j].love});
                // if(tempMarkers[j].love>tempMarkers[j].sad && tempMarkers[j].love>tempMarkers[j].happy && tempMarkers[j].love>tempMarkers[j].happy){
                //     mixedData4.push({location: tempMarkers[j].getPosition(), weight: tempMarkers[j].love});
                // }
            }
            //heatMapData.push(tempMarkers[j].getPosition());
        }
        //try{
            //heatmap.setMap(null);
            //heatmap.setData([]);
            /*mixedHeatmap1.setMap(null);
            mixedHeatmap1.setData([]);
            mixedHeatmap2.setMap(null);
            mixedHeatmap2.setData([]);
            mixedHeatmap3.setMap(null);
            mixedHeatmap3.setData([]);
            mixedHeatmap4.setMap(null);
            mixedHeatmap4.setData([]);*/
        resetHeatmaps();
        //}catch(err){
        //    console.log("ignore this");
        //}
        //happyHeatmap=new google.maps.visualization.HeatmapLayer({data: happyData, radius: 40, map: map, gradient: redGradient});
        if(activeHeatmap=='happy'){
            happyHeatmap=new google.maps.visualization.HeatmapLayer({data: happyData, radius: 40, map: map, gradient: greenGradient});
        }else if(activeHeatmap=='sad'){
            sadHeatmap=new google.maps.visualization.HeatmapLayer({data: sadData, radius: 40, map: map, gradient: blueGradient});
        }else if(activeHeatmap=='political'){
            politicalHeatmap=new google.maps.visualization.HeatmapLayer({data: politicalData, radius: 40, map: map, gradient: purpleGradient});
        }else if(activeHeatmap=='love'){
            loveHeatmap=new google.maps.visualization.HeatmapLayer({data: loveData, radius: 40, map: map, gradient: redGradient});
        }
        //mixedHeatmap1 = new google.maps.visualization.HeatmapLayer({data: mixedData1, radius: 50, map: map, gradient: greenGradient});
        //mixedHeatmap2 = new google.maps.visualization.HeatmapLayer({data: mixedData2, radius: 50, map: map, gradient: blueGradient});
        //mixedHeatmap3 = new google.maps.visualization.HeatmapLayer({data: mixedData3, radius: 50, map: map, gradient: purpleGradient});
        //mixedHeatmap4 = new google.maps.visualization.HeatmapLayer({data: mixedData4, radius: 50, map: map, gradient: redGradient});
    });
}

function initMap() {
    infoWindow = new google.maps.InfoWindow();
    map = new google.maps.Map(document.getElementById('map'), {
        zoom: 5,
        center: {
            lat: 38.695205,
            lng: -99.622656
        },
        mapTypeId: google.maps.MapTypeId.ROADMAP,
        streetViewControl: false
    });
    tweetMarkerCluster = new MarkerClusterer(map, [], options_markerclusterer);

    setup_connection();
    var input = document.getElementById('pac-input');
    var searchBox = new google.maps.places.SearchBox(input);
    map.controls[google.maps.ControlPosition.TOP_LEFT].push(input);

    // Bias the SearchBox results towards current map's viewport.
    map.addListener('bounds_changed', function() {
        searchBox.setBounds(map.getBounds());
    });

    var markers = [];
    // Listen for the event fired when the user selects a prediction and retrieve
    // more details for that place.
    searchBox.addListener('places_changed', function() {
        var places = searchBox.getPlaces();

        if (places.length == 0) {
            return;
        }

        // Clear out the old markers.
        markers.forEach(function(marker) {
            marker.setMap(null);
        });
        markers = [];

        // For each place, get the icon, name and location.
        var bounds = new google.maps.LatLngBounds();
        places.forEach(function(place) {
            if (!place.geometry) {
                console.log("Returned place contains no geometry");
                return;
            }
            var icon = {
                url: place.icon,
                size: new google.maps.Size(71, 71),
                origin: new google.maps.Point(0, 0),
                anchor: new google.maps.Point(17, 34),
                scaledSize: new google.maps.Size(25, 25)
            };

            // Create a marker for each place.
            markers.push(new google.maps.Marker({map: map, icon: icon, title: place.name, position: place.geometry.location}));

            if (place.geometry.viewport) {
                // Only geocodes have viewport.
                bounds.union(place.geometry.viewport);
            } else {
                bounds.extend(place.geometry.location);
            }
        });
        map.fitBounds(bounds);
    });


    setTimeout(function() {
        setup_live_updating();
        find_new_tweets();
        find_new_stories();
        setInterval(find_new_tweets, 30000);
        setInterval(find_new_stories, 300000);
        setInterval(setup_live_updating, 30000);
    }, 1000);
}


function updatePrefChart(prefJSON){
    let parsedPrefs=JSON.parse(prefJSON);
    console.log(parsedPrefs);
    let ctx = document.getElementById('myChart').getContext('2d');
    let tempdata=[];
    for(let i=0;i<parsedPrefs.length;i++){
        tempdata.push(parsedPrefs[i][1]);
    }
    data = {
        datasets: [{
            data: tempdata,
            backgroundColor: ['#ce3939','#cea731','#8dbc78','#2f53a8','#732fa8'],
            hoverBackgroundColor: ['#ff2626','#ffc71e','#a3ff7a','#2c69f7','#a22bff']
        }],

        // These labels appear in the legend and in the tooltips when hovering different arcs
        labels: [
            'Love',
            'Mystery',
            'Happy',
            'Sad',
            'Political'
        ]
    };
    var myDoughnutChart = new Chart(ctx, {
        type: 'doughnut',
        data: data,
        options: {}
    });
}

function resetHeatmaps(){
    try{
        happyHeatmap.setMap(null);
        happyHeatmap.setData([]);
    }catch(err){
        //console.log("ignore happy");
    }
    try{
        sadHeatmap.setMap(null);
        sadHeatmap.setData([]);
    }catch(err){
        ///console.log("ignore sad");
    }
    try{
        politicalHeatmap.setMap(null);
        politicalHeatmap.setData([]);
    }catch(err){
        //console.log("ignore pol");
    }
    try{
        loveHeatmap.setMap(null);
        loveHeatmap.setData([]);
    }catch(err){
        //console.log("ignore love");
    }
}

$(document).ready(function() {
    $("#search-input").on('keyup', function (e) {
        if (e.keyCode == 13) {
            console.log($(this).val());
            search_by_word($(this).val());
        }
    });
    $('#radio-form input').on('change', function() {
        resetHeatmaps();
       if(this.id==='sad-radio'){
           activeHeatmap='sad';
           sadHeatmap=new google.maps.visualization.HeatmapLayer({data: sadData, radius: 40, map: map, gradient: blueGradient});
       }else if(this.id=='happy-radio'){
           activeHeatmap='happy';
           happyHeatmap=new google.maps.visualization.HeatmapLayer({data: happyData, radius: 40, map: map, gradient: greenGradient});
       }else if(this.id=='political-radio'){
           activeHeatmap='political';
           politicalHeatmap=new google.maps.visualization.HeatmapLayer({data: politicalData, radius: 40, map: map, gradient: purpleGradient});
       }else if(this.id=='love-radio'){
           activeHeatmap='love';
           loveHeatmap=new google.maps.visualization.HeatmapLayer({data: loveData, radius: 40, map: map, gradient: redGradient});
       }
    });
    //setTimeout(function() {
    //    get_user_pref()
    //}, 1500);

    /*
    $( "#login-button" ).click(function() {
        let tempuser=$("#username_text").val();
        console.log(tempuser);
        const postParameters = {username: tempuser};

        $.post("/login", postParameters, responseJSON => {
        console.log(responseJSON);
            const responseObject = JSON.parse(responseJSON);

            const username = responseObject.username;
            console.log(username);
        });
    });*/
});
