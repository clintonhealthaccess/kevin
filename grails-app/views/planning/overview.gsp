<!DOCTYPE html>
<html>
<head>
<title>Dashboard</title>
<link rel="shortcut icon" href="/kevin/static/TOkNu9LDencLzreU6u7DZPxhBGtzxvlYwXguYj4EPRe.ico" type="image/x-icon" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<meta name="layout" content="main"/>
<!-- for admin forms -->
<script type="text/javascript" src="https://www.google.com/jsapi"></script>
<script src="/kevin/static/zTefQR2N5AoN2Msj8TzWI9HZCHVcOi0E193FoAXpZeN.js" type="text/javascript" ></script>
<link href="/kevin/static/xE7k3fs7bOpYdHBpGTGjaIHAy5d363BgWIl6WudnwCE.css"
type="text/css" rel="stylesheet" media="screen, projection" />
<link href="/kevin/static/r5xUOzkicfotWxyObgJKgKVpSTA4u6qjTn9INsx21v.css" type="text/css" rel="stylesheet" media="screen, projection" />
</head>

<body>
<div id="spinner" class="spinner" style="display: none;"> <img src="/kevin/static/RUqKjjg0blqYq46cT6pZl3avB2GM1AWLPs1pE4vbKUr.gif" alt="Loading..." /> </div>

<div id="content" class="push">
  <div class="wrapper">
    <div id="report">

      <div class="main">
        <ul class='horizontal' id='tab-nav'>
          <li><a class="selected" href="/kevin/dashboard/view/1/1?objective=1&dashboardEntity=1">Undertakings</a></li>
          <li><a href="/kevin/dsr/view/1/1/1?dashboardEntity=1">Projected Budget</a></li>
        </ul>
        <ul class='horizontal' id="tab-subnav">
          <li><a class='selected' href="#">Overview</a></li>
          <li><a href="#">Services</a></li>
          <li><a href="#">Activities</a></li>
          <li><a href="#">Investments</a></li>
        </ul>
        
        <p class='show-question-help moved'><a href="#">Show Tips</a></p>
        <div class='question-help-container'>
          <div class='question-help push-20'> <a class="hide-question-help" href="#">Close tips</a> Some help information for the Performance tab </div>
        </div>
        
        <div id='questions'>
          <div class='question push-20'>
            <h4 class='section-title'> <span class='question-default'> <img src="/kevin/static/PnPNkVfHIpcScmB9ptVadgIVvvaqw4c5Kn0odzx0exg.png" /> </span>Operational Undertakings: Kigali HD</h4>
            <ul class="overview-section">
              <li>
                <h5 class="left"><a href="#">Services</a></h5>
                <p class="right"><a class="overview-all" href="#">View All <span>60</span> Services</a></p>
                
                <h6>Recently Added</h6>
                <ul class="overview-recent">
                  <li><a href="#">Malaria Consultations</a><span class="overview-manage right"><a href="#">edit</a><a href="#">delete</a></span></li>
                  <li><a href="#">HIV Consultations</a><span class="overview-manage right"><a href="#">edit</a><a href="#">delete</a></span></li>
                  <li><a href="#">Provide Condoms</a><span class="overview-manage right"><a href="#">edit</a><a href="#">delete</a></span></li>
                  <li><a href="#">Flu Vaccinations</a><span class="overview-manage right"><a href="#">edit</a><a href="#">delete</a></span></li>
                </ul>

                <p class="overview-new"><a class="next gray medium" href="#">Create New Service</a></p>
              </li>

              <li>
                <h5><a href="#">Activities</a></h5>
                <p>You haven't added any activites yet. <a href="#">Add your first activity</a></p>
                <p class="overview-new"><a class="next gray medium" href="#">Create New Activity</a></p>
              </li>

              <li>
                <h5 class="left"><a href="#">Investments</a></h5>
                <p class="right"><a class="overview-all" href="#">View All <span>10</span> Services</a></p>

                <h6>Recently Added</h6>
                <ul class="overview-recent">
                  <li><a href="#">Security Upgrade</a><span class="overview-manage right"><a href="#">edit</a><a href="#">delete</a></span></li>
                  <li><a href="#">Paint Building</a><span class="overview-manage right"><a href="#">edit</a><a href="#">delete</a></span></li>
                  <li><a href="#">Build Waiting Room</a><span class="overview-manage right"><a href="#">edit</a><a href="#">delete</a></span></li>
                </ul>
                
                <p class="overview-new"><a class="next gray medium" href="#">Create New Investment</a></p>
              </li>
              
            </ul>
          </div>
        </div>

      </div>
    </div>
  </div>
</div>

<script src="/kevin/static/lInBH1emtinXHaXgdDOicoMATGOYHtkVu76B6Z9pgWG.js" type="text/javascript" ></script>
<script src="/kevin/static/SIqAArNWIgTC3ovWp2FIR5EHa3UyV7tQmIh3CVeLwa1.js" type="text/javascript" ></script>
<script src="/kevin/static/Ah2SUWwEeXLsKP0dFumcARvEre4AWmdlBmazKKaH1ln.js" type="text/javascript" ></script>
<script src="/kevin/static/wnYHZlUNJYYBKshR8xWqn42JbAFOfxIzaiMUWM8ynLd.js" type="text/javascript" ></script>
<script type="text/javascript"> //Styling the main menu $('#main-menu >
li').hover( function () { //show its submenu if (!$('ul', this).hasClass('open')) { $('ul', this).addClass('open'); $('ul', this).show(); }

 }, function () { var self = this; //hide its submenu $('ul', this).slideUp(10, function(){ $('ul', self).removeClass('open'); }); } ); </script>
</body>
</html>