TodoListGenerator = function () {
    var div;
    var init_info;
    var self = this;
    self.ownerID = null;
    self.ownerName = null;
    function getViewPromise() {
        return new Promise(function (resolve, reject) {
            $.ajax({
                url: '/plain/todo-list-view.html',
                dataType: 'html'
            }).done(function (result) {
                var temps = $(result);
                TEMP_TODOLIST_VIEW = null;
                if (!temps) {
                    return reject('didn\'t get script tag');
                }
                TEMP_TODOLIST_VIEW = temps[0].text;
                TEMP_ITEM_FINISH = temps[2].text;
                TEMP_ITEM_UNFINISH = temps[4].text;
                resolve();
            }).fail(function (err) {
                console.error(err);
                return reject('Fail from get /plain/todo-list-view.html');
            });
        });
    }

    this.refresh = function (divToOperate, info) {
        div = divToOperate;
        init_info = info;

        if (typeof TODO_LIST_CARD_VIEW === 'undefined') {
            TODO_LIST_CARD_VIEW = getViewPromise();
        }
        TODO_LIST_CARD_VIEW.then(function () {
            var list_info = {
                key: info.key,
                list_name: info.name,
                ownerName: info.ownerName,
                ownerID: info.ownerID,
                ownershipType:info.ownershipType
            };
            var div1 = $('<div/>');
            var html = Mustache.to_html(TEMP_TODOLIST_VIEW, list_info);
            div1.html(html);
            var item_list = info.itemPreview;
            var item_num = 0;
            for (var i in item_list) {
                if (i==10) break;
                var item = item_list[i];
                item_num++;
                if (item.completed == true) {
                    var item_info = {
                        item_description: item.description
                    };
                    var finish_html = Mustache.to_html(TEMP_ITEM_FINISH, item_info);
                    var li = $('<li/>').html(finish_html);
                    li.appendTo(div1.find('#already_done'));
                }
                else if (item.completed == false) {
                    var item_info = {
                        item_description: item.description
                    };
                    var unfinish_html = Mustache.to_html(TEMP_ITEM_UNFINISH, item_info);
                    var li = $('<li/>').html(unfinish_html);
                    li.appendTo(div1.find('#unfinish_list'));
                }
            }
            if (profile==null || profile.getId() != info.ownerID) {
                div1.find('#delete_btn').hide();
                div1.find('#edit_btn').hide();
                div1.find('#view_btn').click(function () {
                    view_card({key:list_info.key});
                });
            }else{
                div1.find('#view_btn').hide();
                div1.find('#delete_btn').click(function(){
                    delete_card({key:list_info.key,ownerID:list_info.ownerID});
                });
                div1.find('#edit_btn').click(function(){
                    edit_card({key:list_info.key})
                        .then(function (retrievedInfo) {
                            var list_info = {
                                key: retrievedInfo.key,
                                name: retrievedInfo.meta.name,
                                ownerName: self.ownerName,
                                ownerID: self.ownerID,
                                ownershipType: retrievedInfo.meta.ownershipType
                            };
                            list_info.itemPreview=retrievedInfo.list;
                            var total = 0;
                            var done =0;
                            for (var i in list_info.itemPreview){
                                total++;
                                if(list_info.itemPreview[i].completed) done++;
                            }
                            list_info.completedNum = done;
                            list_info.totalItemsNum = total;
                            self.refresh(div, list_info);
                        });
                });
            }
            div1.find('#already_done').attr('id',list_info.key);
            div1.find('#count-done').attr('data-target','#'+list_info.key);
            div1.find('.count-todos').text((info.totalItemsNum-info.completedNum)+' Item Left');
            if(info.completedNum) div1.find('#count-done').text((info.completedNum)+' Done');
            else div1.find('#count-done').hide();
            div.html('');
            div1.appendTo(div);
        });
    };

    this.init = function (divToOperate, info) {
        self.ownerID = info.ownerID;
        self.ownerName = info.ownerName;
        return this.refresh(divToOperate, info);
    };
};

