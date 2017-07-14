profile = null;
name = null;
id_token = null;
var id = null;
var startCursor = null;
var listOptions = ["public", "my-list", "private"];
var currentList = listOptions[0];

$(window).on('load', function() {
    $('.loader-parent').show();
});

$(document).ready(function() {
    $('.loader-parent').hide();
    $('#add-card-btn').click(function () {
        new_card().then(function () {
            listLists(currentList,false);
        })
    });
    $('[data-toggle="tooltip"]').tooltip();

    listLists('public',false);

    if(id_token == null) {
        $('.g-signin2').show();
        $('.private').hide();
        $('#sign-out').hide();
    }else {
        $('.g-signin2').hide();
        $('.private').show();
        $('#sign-out').show();
    }
});

// load more
$(window).scroll(function() {
    if($(window).scrollTop() + $(window).height() == $(document).height()) {
        listLists(currentList,true);
    }
});


function listLists(ownership, loadMore) {
    var obj = null;
    var listDiv = $('.lists');
    if(ownership == 'public') {
        var username = 'asdfghjkl';
    }else {
        var username = id_token;
    }
    if(loadMore == false) {
        listDiv.empty();
        obj = {IDtoken: username, ownershipType:ownership, itemPreviewNum:10};
    }else {
        obj = {IDtoken: username, ownershipType:ownership, startCursor:startCursor,itemPreviewNum:10};
    }
    $.ajax({
        url: '/ajax/todo-list/list-todo-list',
        type: 'post',
        data: JSON.stringify(obj),
        contentType: "application/json; charset=utf-8",
        dataType: 'json'
    }).done(function (data) {
        if(data.result === true) {
            var lists = data.list;
            startCursor = data.endCursor;
            for(var i in lists) {
                var listTemplate = new TodoListGenerator();
                var div = $("<div class='col-xs-12 col-md-4 col-sm-6 col-lg-3'></div>");
                listDiv.append(div);
                listTemplate.init(div, lists[i]);
            }
            if(ownership == 'public') {
                currentList = listOptions[0];
                $('.navbar-btn').removeClass('current');
                $('.navbar-public').addClass('current');

            }else if (ownership == 'private'){
                currentList = listOptions[2];
                $('.navbar-btn').removeClass('current');
                $('.navbar-private').addClass('current');
            }else {
                currentList = listOptions[1];
                $('.navbar-btn').removeClass('current');
                $('.navbar-my').addClass('current');
            }
        }else {
            console.error(data.error);
        }
    }).fail(function (err) {
        console.error(err);
    });
}

function signOut() {
    var auth2 = gapi.auth2.getAuthInstance();
    auth2.signOut().then(function () {
        console.log('User signed out.');
        $('.g-signin2').show();
        $('.private').hide();
        $('#sign-out').hide();
        profile = null;
        name = null;
        id_token = null;
        id = null;
        listLists('public',false);
    });
}
function onSignIn(googleUser) {
    profile = googleUser.getBasicProfile();
    name = profile.getName();
    id_token = googleUser.getAuthResponse().id_token;
    id = profile.getId();
    $('.g-signin2').hide();
    $('.private').show();
    $('#sign-out').show();
    listLists('my-list',false);
}