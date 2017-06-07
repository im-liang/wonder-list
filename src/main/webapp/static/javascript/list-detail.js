function edit_card(info) {
    var key = info.key;

    if(!id_token) return console.error('no login found');

    function retriveMetaInfo(value) {
        return new Promise(function (resolve, reject) {
            var retrivedInfo = {name: 'false name'};
            $.ajax({
                url: '/ajax/todo-list/todo-list-meta-read',
                type: 'post',
                data: JSON.stringify({IDtoken: id_token, key: key}),
                contentType: "application/json; charset=utf-8",
                dataType: 'json'
            }).done(function (json) {
                if (!json.result) {
                    console.error(json.error);
                    return reject(json.error);
                }
                resolve({meta: json});
            }).fail(function (err) {
                console.error(err);
                reject(err);
            });
        });
    }

    function retriveInfo(value) {
        return new Promise(function (resolve, reject) {
            $.ajax({
                url: '/ajax/todo-list/todo-list-read',
                type: 'post',
                data: JSON.stringify({IDtoken: id_token, key: key}),
                contentType: "application/json; charset=utf-8",
                dataType: 'json'
            }).done(function (json) {
                if (!json.result) {
                    console.error(json.error);
                    return reject(json.error);
                }
                resolve({key: key, meta: value.meta, list: json.list});
            }).fail(function (err) {
                console.error(err);
                reject(err);
            });
        });
    }

    function writeBackMetaInfo(retrivedInfo) {
        return new Promise(function (resolve, reject) {
            retrivedInfo.meta.IDtoken = id_token;
            retrivedInfo.meta.key = key;
            $.ajax({
                url: '/ajax/todo-list/todo-list-meta-write',
                type: 'post',
                data: JSON.stringify(retrivedInfo.meta),
                contentType: "application/json; charset=utf-8",
                dataType: 'json'
            }).done(function (json) {
                if (!json.result) {
                    console.error(json.error);
                    return reject(json.error);
                }
                resolve(retrivedInfo);
            }).fail(function (err) {
                console.error(err);
                reject(err);
            });
        });
    }

    function writeBackListInfo(retrivedInfo) {
        return new Promise(function (resolve, reject) {
            var data = {};
            data.IDtoken = id_token;
            data.key = key;
            data.list = retrivedInfo.list;
            $.ajax({
                url: '/ajax/todo-list/todo-list-write',
                type: 'post',
                data: JSON.stringify(data),
                contentType: "application/json; charset=utf-8",
                dataType: 'json'
            }).done(function (json) {
                if (!json.result) {
                    console.error(json.error);
                    return reject(json.error);
                }
                resolve(retrivedInfo);
            }).fail(function (err) {
                console.error(err);
                reject(err);
            });
        });
    }

    return retriveMetaInfo()
        .then(retriveInfo)
        .then(function (retrivedInfo) {
            retrivedInfo.key = key;
            return showEditView(retrivedInfo);
        })
        .then(writeBackMetaInfo)
        .then(writeBackListInfo);
}

function new_card() {
    if(!id_token) return console.error('no login found');

    function createNewList(retrivedInfo){
        return new Promise(function (resolve, reject) {
            retrivedInfo.meta.IDtoken = id_token;
            $.ajax({
                url: '/ajax/todo-list/add-todo-list',
                type: 'post',
                data: JSON.stringify(retrivedInfo.meta),
                contentType: "application/json; charset=utf-8",
                dataType: 'json'
            }).done(function (json) {
                if (!json.result) {
                    console.error(json.error);
                    return reject(json.error);
                }
                retrivedInfo.key = json.key;
                resolve(retrivedInfo);
            }).fail(function (err) {
                console.error(err);
                reject(err);
            });
        });
    }

    function writeBackListInfo(retrivedInfo) {
        return new Promise(function (resolve, reject) {
            var data = {};
            data.IDtoken = id_token;
            data.key = retrivedInfo.key;
            data.list = retrivedInfo.list;
            $.ajax({
                url: '/ajax/todo-list/todo-list-write',
                type: 'post',
                data: JSON.stringify(data),
                contentType: "application/json; charset=utf-8",
                dataType: 'json'
            }).done(function (json) {
                if (!json.result) {
                    console.error(json.error);
                    return reject(json.error);
                }
                resolve(retrivedInfo);
            }).fail(function (err) {
                console.error(err);
                reject(err);
            });
        });
    }

    var initInfo = {
        meta: {
            name: 'new list',
            ownershipType: 'private',
            ownerName: name || "You don't deserve a name"
        },
        list: []
    };

    return showEditView(initInfo)
        .then(createNewList)
        .then(writeBackListInfo);
}

function view_card(info){
    var key = info.key;

    function retriveMetaInfo(value) {
        return new Promise(function (resolve, reject) {
            var retrivedInfo = {name: 'false name'};
            $.ajax({
                url: '/ajax/todo-list/todo-list-meta-read',
                type: 'post',
                data: JSON.stringify({IDtoken: id_token || 'asdfghjkl', key: key}),
                contentType: "application/json; charset=utf-8",
                dataType: 'json'
            }).done(function (json) {
                if (!json.result) {
                    console.error(json.error);
                    return reject(json.error);
                }
                resolve({meta: json});
            }).fail(function (err) {
                console.error(err);
                reject(err);
            });
        });
    }

    function retriveInfo(value) {
        return new Promise(function (resolve, reject) {
            $.ajax({
                url: '/ajax/todo-list/todo-list-read',
                type: 'post',
                data: JSON.stringify({IDtoken: id_token || 'asdfghjkl', key: key}),
                contentType: "application/json; charset=utf-8",
                dataType: 'json'
            }).done(function (json) {
                if (!json.result) {
                    console.error(json.error);
                    return reject(json.error);
                }
                resolve({key: key, meta: value.meta, list: json.list});
            }).fail(function (err) {
                console.error(err);
                reject(err);
            });
        });
    }

    return retriveMetaInfo()
        .then(retriveInfo)
        .then(function (retrivedInfo) {
            retrivedInfo.key = key;
            return showEditView(retrivedInfo, true);
        })
        .catch(function (err) {
            return true;
        });
}

function showEditView(info, readOnly) {
    function getTemplate(value) {
        return new Promise(function (resolve, reject) {
            if (typeof TEMP_TODOLIST_DETAIL_VIEW === 'undefined') {
                $.ajax({
                    url: '/plain/todo-list-detail-view.html',
                    dataType: 'html'
                }).done(function (result) {
                    var temps = $(result);
                    temps.find('#todo-list-detail');
                    if (!temps) {
                        return reject('didn\'t get script tag');
                    }
                    TEMP_TODOLIST_DETAIL_VIEW = temps.text();
                    return resolve();
                }).fail(function (err) {
                    console.error(err);
                    return reject('FFFail');
                });
            } else {
                return resolve();
            }
        });
    }

    function showAndWait() {
        return new Promise(function (resolve, reject) {
            var detailView = $('#todo-list-detail-modal');
            var toComplete;
            var toCancel;
            var completePromise = new Promise(function (resolve, reject) {
                toComplete = resolve;
                toCancel = reject;
            });
            _deserializeListEditView(detailView, info, toComplete, toCancel, readOnly);
            detailView.modal('show');
            completePromise.then(function () {
                if(readOnly) resolve();
                resolve(_serializeListEditView(detailView, info.key));
            }).catch(function () {
                reject();
            });
        });
    }

    return getTemplate().then(showAndWait);
}


function _deserializeListEditView(view, infoForTemp, toComplete, toCancel, readOnly) {

    var options = {
     year: "numeric", month: "numeric",
    day: "numeric", hour: "2-digit", minute: "2-digit"
};
    var format ={format:'m/d/yyyy, H:i P'};
    var html = Mustache.to_html(TEMP_TODOLIST_DETAIL_VIEW, infoForTemp);
    view.html(html);
    if(infoForTemp.meta.ownershipType === 'public') view.find('.detail-public').attr('checked', 'checked');
    if(infoForTemp.meta.ownershipType === 'private') view.find('.detail-private').attr('checked', 'checked');
    infoForTemp.list.forEach(function (item) {
        var tr = $('<tr/>');
        $('<td class="hidden-xs"/>').attr('class', 'detail-cat').text(item.category).attr('contenteditable','true').appendTo(tr);
        $('<td/>').attr('class', 'detail-des').text(item.description).attr('contenteditable','true').appendTo(tr);
        $('<td class="hidden-xs hidden-sm"/>').appendTo(tr).append($('<input/>').attr('value',new Date(item.startDate).toLocaleTimeString("en-us", options)).attr('isoTime',item.startDate).attr('readonly','').attr('type','text').attr('class','detail-startDate').attr('size','16').datetimepicker(format));
        $('<td class="hidden-xs"/>').appendTo(tr).append($('<input/>').attr('value',new Date(item.endDate).toLocaleTimeString("en-us", options)).attr('isoTime',item.endDate).attr('readonly','').attr('type','text').attr('size','16').attr('class','detail-endDate').datetimepicker(format));
        $('<td/>').appendTo(tr).append( $('<input/>').attr('type','checkbox').attr(item.completed?'checked':'unchecked','').attr('class', 'detail-completed'));
        tr.find('.detail-startDate')
            .datetimepicker()
            .on('hide', function(ev){
                ev.date.setTime(ev.date.getTime()+ev.date.getTimezoneOffset()*60*1000);
               tr.find('.detail-startDate').attr('isoTime',ev.date.toISOString())
            });
        tr.find('.detail-endDate')
            .datetimepicker()
            .on('hide', function(ev){
                ev.date.setTime(ev.date.getTime()+ev.date.getTimezoneOffset()*60*1000);
                tr.find('.detail-endDate').attr('isoTime',ev.date.toISOString())
            });

        if(!readOnly){
            $('<td class="hidden-xs hidden-sm"/>').appendTo(tr).append($('<a/>').attr('href','#').text('Up').attr('class', 'up btn btn-primary')).click(function () {
                var row = $(this).parents("tr:first");
                row.insertBefore(row.prev());
            });
            $('<td class="hidden-xs hidden-sm"/>').appendTo(tr).append($('<a/>').attr('href','#').text('Down').attr('class', 'down btn btn-primary')).click(function () {
                var row = $(this).parents("tr:first");
                row.insertAfter(row.next());
            });
            var removeButton = $('<button/>').attr('type','button').attr('name',"rm").text('Remove').attr('class', 'detail-remove btn btn-danger');
            $('<td class="hidden-xs"/>').appendTo(tr).append(removeButton);
            removeButton.click(function() {
                $(this).closest('tr').remove();
            });
        }else{
            tr.find('.detail-cat').attr('contenteditable','false');
            tr.find('.detail-des').attr('contenteditable','false');
            tr.find('.detail-startDate').datetimepicker('remove');
            tr.find('.detail-endDate').datetimepicker('remove');
            tr.find('.detail-completed').attr('disabled','disabled');
        }

        tr.appendTo(view.find('.detail-list'));
    });
    
    view.find('.detail-add-btn').click(function () {
        var tr = $('<tr/>');
        $('<td class="hidden-xs"/>').attr('class', 'detail-cat').text('default type').attr('contenteditable','true').appendTo(tr);
        $('<td/>').attr('class', 'detail-des').text('default description').attr('contenteditable','true').appendTo(tr);

        $('<td class="hidden-xs hidden-sm"/>').appendTo(tr).append($('<input/>').attr('class','.detail-startDate').attr('readonly','').attr('isoTime',new Date().toISOString()).attr('type','text').attr('size','16').attr('class','detail-startDate').attr('value',new Date().toLocaleTimeString("en-us", options)).datetimepicker(format));
        $('<td class="hidden-xs"/>').appendTo(tr).append($('<input/>').attr('isoTime',new Date().toISOString()).attr('class','.detail-endDate').attr('readonly','').attr('type','text').attr('size','16').attr('class','detail-endDate').attr('value',new Date().toLocaleTimeString("en-us", options)).datetimepicker(format));
        $('<td/>').appendTo(tr).append($('<input/>').attr('type','checkbox').attr('class', 'detail-completed'));
        tr.find('.detail-startDate')
            .datetimepicker()
            .on('hide', function(ev){
                ev.date.setTime(ev.date.getTime()+ev.date.getTimezoneOffset()*60*1000);
                tr.find('.detail-startDate').attr('isoTime',ev.date.toISOString())
            });
        tr.find('.detail-endDate')
            .datetimepicker()
            .on('hide', function(ev){
                ev.date.setTime(ev.date.getTime()+ev.date.getTimezoneOffset()*60*1000);
                tr.find('.detail-endDate').attr('isoTime',ev.date.toISOString())
            });
        if(!readOnly){
            $('<td class="hidden-xs hidden-sm"/>').appendTo(tr).append($('<a/>').attr('href','#').text('Up').attr('class', 'up btn btn-primary')).click(function () {
                var row = $(this).parents("tr:first");
                row.insertBefore(row.prev());
            });
            $('<td class="hidden-xs hidden-sm"/>').appendTo(tr).append($('<a/>').attr('href','#').text('Down').attr('class', 'down btn btn-primary')).click(function () {
                var row = $(this).parents("tr:first");
                row.insertAfter(row.next());
            });
            var removeButton = $('<button/>').attr('type','button').attr('name',"rm").text('Remove').attr('class', 'detail-remove btn btn-danger');
            $('<td class="hidden-xs"/>').appendTo(tr).append(removeButton);
            removeButton.click(function() {
                $(this).closest('tr').remove();
            });
        }

        tr.appendTo(view.find('.detail-list'));
    });
    if(readOnly){
        view.find('.detail-save-btn').hide();
        view.find('.detail-add-btn').hide();
        view.find('.detail-name').attr('disabled','disabled');
        view.find('.detail-public').attr('disabled','disabled');
        view.find('.detail-private').attr('disabled','disabled');
    }else{
        view.find('.detail-save-btn').click(function () {
            toComplete();
            view.modal('hide');
        });
    }

    view.find('.detail-cancel-btn').click(function () {
        toCancel();
        view.modal('hide');
    });
        sortables_init();
}

function _serializeListEditView(view, key) {
    var infoForTemp = {
        key: '',
        meta: {},
        list: []
    };
    infoForTemp.meta.name = view.find(".detail-name").val();
    infoForTemp.meta.ownershipType = view.find('input[name=ownershipType]:checked').val();
    var detailList = view.find('.detail-list').find('tr');
    var listData = [];
    detailList.each(function (index, item) {
        var itemData = {
            category: item.getElementsByClassName("detail-cat")[0].innerHTML,
            description: item.getElementsByClassName("detail-des")[0].innerHTML,
            startDate: $(item.getElementsByClassName("detail-startDate")).attr('isotime'),
            endDate: $(item.getElementsByClassName("detail-endDate")).attr('isotime'),
            completed: $(item.getElementsByClassName("detail-completed")[0]).is(':checked')
        };
        listData.push(itemData);
    });
    infoForTemp.key = key;
    infoForTemp.list = listData;
    return infoForTemp;
}


function updateVal(currentEle, value) {
    $(document).off('click');
    $(currentEle).html('<input class="thVal" type="text" value="' + value + '" />');
    $(".thVal").focus();
    $(".thVal").keyup(function (event) {
        if (event.keyCode == 13) {
            $(currentEle).html($(".thVal").val().trim());
        }
    });

    $(document).click(function () {
        $(currentEle).html($(".thVal").val().trim());
    });
}

$(function editListItem() {
    $(".table-hover td").dblclick(function (e) {
        console.log("clicked twice");
        e.stopPropagation();
        var currentEle = $(this);
        var value = $(this).html();
        updateVal(currentEle, value);
    });
});