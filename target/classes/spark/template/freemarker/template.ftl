<!DOCTYPE html>
  <head>
    <meta charset="utf-8">
    <title id="title">${title}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="Content-Type: text/html; charset=utf-8" content="text/html; charset=utf-8" />
    <!-- In real-world webapps, css is usually minified and
         concatenated. Here, separate normalize from our code, and
         avoid minification for clarity. -->
    <link rel="stylesheet" href="css/normalize.css">
    <link rel="stylesheet" href="css/html5bp.css">
    <link rel="stylesheet" href="css/index.css">
    <link rel="stylesheet" href="css/login.css">
    <link rel="stylesheet" href="css/start.css">
    <!-- include the core styles -->
	<link rel="stylesheet" href="css/alertify.core.css">
	<link rel="stylesheet" href="css/alertify.bootstrap.css">
	<!-- include a theme, can be included into the core instead of 2 separate files -->
	<link rel="stylesheet" href="css/alertify.default.css">
    <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css?family=Carter+One" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css?family=Poppins" rel="stylesheet">
  </head>
  <body>
     ${content}
     <!-- Again, we're serving up the unminified source for clarity. -->
     <script src="js/jquery-3.1.1.js"></script>
     <script src="js/main.js"></script>
     <script src="js/UpdatingWebSocket.js"></script>
     <script src="js/alertify.js"></script>
     <script src="https://developers.google.com/maps/documentation/javascript/examples/markerclusterer/markerclusterer.js">
     </script>
     <script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.4.0/Chart.min.js"></script>
     <script async defer
     src="https://maps.googleapis.com/maps/api/js?key=AIzaSyCpkWc_uvtyG-veTgDtc4mUSEz3JUK0Bgc&callback=initMap&libraries=visualization,places">
     </script>
     <script>window.twttr = (function(d, s, id) {
      var js, fjs = d.getElementsByTagName(s)[0],
        t = window.twttr || {};
      if (d.getElementById(id)) return t;
      js = d.createElement(s);
      js.id = id;
      js.src = "https://platform.twitter.com/widgets.js";
      fjs.parentNode.insertBefore(js, fjs);

      t._e = [];
      t.ready = function(f) {
        t._e.push(f);
      };
      return t;
    }(document, "script", "twitter-wjs"));</script>
  </body>
  <!-- See http://html5boilerplate.com/ for a good place to start
       dealing with real world issues like old browsers.  -->
</html>
