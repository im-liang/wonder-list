function delete_card(info) {
    var key = info.key;
    var ownerID = info.ownerID;

    if(id !== ownerID){
        return console.error('user id does not match');
    }

    $.ajax({
        url: '/ajax/todo-list/remove-todo-list',
        type: 'post',
        data: JSON.stringify({IDtoken: id_token, key: key}),
        contentType: "application/json; charset=utf-8",
        dataType: 'json'
    }).done(function (data) {
        if (data.result === true) {
            $('div[key="'+key+'"]').parent().parent().remove();
        } else {
            console.error(data.error);
        }
    }).fail(function (err) {
        console.error(err);
    });
}