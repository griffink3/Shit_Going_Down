<#assign content>

<input id="pac-input" class="controls" type="text" placeholder="Search by Location..">
<input id="search-input" class="search-input" type="text" placeholder="Search by Keyword..">

<span class="open-label" onclick="openNav()">&#9776; Menu</span>

<div id="map"></div>

<div id="mySidenav" class="sidenav">
  <a href="javascript:void(0)" class="closebtn" onclick="closeNav()">&times;</a>
  <h3 class="search-results-label">Top News Search Results</h3>
  <ol type="1" class="search-results-ul" id="news-search-results-ul"></ol>
  <h3 class="search-results-label">Top Tweets Search Results</h3>
  <ol type="1" class="search-results-ul" id="tweets-search-results-ul"></ol>

    <form class="radio-buttons" id="radio-form">
        <label style="margin-left:7px;" for="happy-radio">Happy</label>
        <input type="radio" id="happy-radio" name="heatmap" checked="checked" class="flipswitch" />
        <br>
        <label style="margin-left:24px;" for="sad-radio">Sad</label>
        <input type="radio" id="sad-radio" name="heatmap" class="flipswitch" />
        <br>
        <label for="political-radio">Political</label>
        <input type="radio" id="political-radio" name="heatmap" class="flipswitch" />
        <br>
        <label style="margin-left:18.5px;" for="love-radio">Love</label>
        <input type="radio" id="love-radio" name="heatmap" class="flipswitch" />
    </form>

    <h4 id="preferences"></h4>
</div>
<div class="chartarea">
<canvas id="myChart"></canvas>
</div>


</#assign>
<#include "template.ftl">
