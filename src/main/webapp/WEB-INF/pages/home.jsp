<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
<head>
    <title>Home Page</title>
    <link rel="shortcut icon" href="/logo/favicon.ico" type="image/x-icon"/>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="google-signin-client_id"
          content="651259629503-d0oips456nr7cam2fd368l3i7pik3r8b.apps.googleusercontent.com">
    <!-- Libraries -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <script src="https://apis.google.com/js/platform.js" async defer></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/mustache.js/2.3.0/mustache.js"></script>

    <!-- Our css and js -->
    <link rel="stylesheet" href="/stylesheets/home.css">
    <link rel="stylesheet" href="/stylesheets/to-do-lists.css">

    <script src="javascript/to-do-lists.js"></script>
    <script src="/javascript/todo-list-generator.js"></script>
    <script src="/javascript/list-detail.js"></script>
    <script src="/javascript/delete_card.js"></script>
    <!--local lib-->
    <link rel="stylesheet" href="/stylesheets/lib/bootstrap-datetimepicker.css">
    <script src="/javascript/lib/bootstrap-datetimepicker.js"></script>
    <script type="text/javascript" src="/javascript/lib/sortable_us.js"></script>
    <script type="text/javascript" src="https://cdn.jsdelivr.net/bluebird/latest/bluebird.core.min.js"></script>

</head>

<body>
<%-- for todolist detail view --%>
<div class="modal fade" id="todo-list-detail-modal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLongTitle"
     aria-hidden="true"></div>

<nav class="navbar navbar-inverse">
    <div class="container-fluid">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#myNavbar">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <img src="/logo/favicon-32x32.png" alt="logo">
        </div>
        <div class="collapse navbar-collapse" id="myNavbar">
            <ul class="nav navbar-nav dynamic-link">
                <li><a href="#" onclick="listLists('public',false)" class='navbar-btn navbar-public' data-toggle="tooltip" data-placement="bottom" title="display public lists">Public Lists</a></li>
                <li><a href='#' onclick="listLists('private',false)" class='private navbar-btn navbar-private' data-toggle="tooltip" data-placement="bottom" title="display your private lists">My Private Lists</a></li>
                <li><a href='#' onclick="listLists('my-list',false)" class='private navbar-btn navbar-my' data-toggle="tooltip" data-placement="bottom" title="display your public & private lists">My Lists</a></li>
                <li><a href='#' id="add-card-btn" class='private navbar-btn' data-toggle="tooltip" data-placement="bottom" title="add a new list">Add a List</a></li>
            </ul>
            <ul class="nav navbar-nav navbar-right">
                <li><div class="g-signin2" data-onsuccess="onSignIn"></div></li>
                <li><a href='#' class='navbar-btn' id='sign-out' onclick='signOut();'><span class='glyphicons glyphicons-log-out'></span>Sign out</a></li>
            </ul>
        </div>
    </div>
</nav>
<div class="container-fluid">
    <div class="row">
      <div class="col-sm-12 lists">
          <div class="loader-parent"><div class="loader"></div></div>
      </div>
  </div>

</div>
</body>
</html>
