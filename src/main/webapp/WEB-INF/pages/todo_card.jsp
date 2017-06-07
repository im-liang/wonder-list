<%@ page contentType="text/html;charset=UTF-8" language="java" %>
        <div class="todolist not-done">
            <h1>List Name</h1><button class="remove-item btn btn-default btn-xs pull-right"><span
                class="glyphicon glyphicon-pencil"></span></button>
            <h2>Auther</h2>
            <div class="btn-group" data-toggle="buttons">
                <label class="btn btn-primary active">
                    <input type="radio" name="options" id="option1" autocomplete="off" checked> Public
                </label>
                <label class="btn btn-primary">
                    <input type="radio" name="options" id="option2" autocomplete="off"> Private
                </label>

            </div>
            <input type="text" class="form-control add-todo" placeholder="Add todo">


            <hr>
            <ul id="sortable" class="list-unstyled">
                <li class="ui-state-default">
                <button id="checkAll" class="btn btn-success">Mark all as done</button>
                </li>
                <li class="ui-state-default">
                    <div class="checkbox">
                        <label>
                            <input type="checkbox" value=""/>Take out the trash</label>
                    </div>
                </li>
                <li class="ui-state-default">
                    <div class="checkbox">
                        <label>
                            <input type="checkbox" value=""/>Buy bread</label>
                    </div>
                </li>
                <li class="ui-state-default">
                    <div class="checkbox">
                        <label>
                            <input type="checkbox" value=""/>Teach penguins to fly</label>
                    </div>
                </li>
            </ul>
            <div id="already_done" class="collapse">
                <ul id="done-items" class="list-unstyled">
                    <li> <input type="checkbox" checked value=""/><strike>Teach penguins to fly</strike></label>
                        <button class="remove-item btn btn-default btn-xs pull-right"><span
                                class="glyphicon glyphicon-remove"></span></button>
                    </li>

                </ul>

            </div>
            <div class="row-fluid">
            <div class="span12">
                <strong><span class="count-todos"></span></strong>XX Items Left
                <button type="button" class="btn btn-link span1 col-md-offset-7" data-toggle="collapse" data-target="#already_done">Done</button>
            </div>
    </div>
