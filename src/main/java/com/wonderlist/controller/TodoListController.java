package com.wonderlist.controller;

import com.wonderlist.utility.*;
import com.google.appengine.api.datastore.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Objects;


@Controller
public class TodoListController {
    @RequestMapping(value = "/ajax/todo-list/add-todo-list", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String addList(HttpServletRequest request, HttpServletResponse response, @RequestBody String body) {
        String idToken = null;
        String name = null;
        String ownershipType = null;
        //parse json
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonBody = (JSONObject) parser.parse(body);
            idToken = (String) jsonBody.get("IDtoken");
            name = (String) jsonBody.get("name");
            ownershipType = (String) jsonBody.get("ownershipType");
        } catch (ParseException | ClassCastException e) {
            Logging.exception(e);
            return ResponseTools.plainError("json parsing error");
        }
        GoogleUserInfo user = GoogleAuth.googleVerification(idToken);

        if (user == null)
            return ResponseTools.plainError("google authentication error");
        if (name == null)
            return ResponseTools.plainError("name is null");
        if (ownershipType == null)
            return ResponseTools.plainError("ownershipType is null");

        //check owner ship
        if (!Objects.equals(ownershipType, "private") && !Objects.equals(ownershipType, "public"))
            return ResponseTools.plainError("ownershipType should be private or public");
        //input data
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity entity = new Entity("todo-list");
        entity.setProperty("name", name);
        entity.setProperty("owner", user.id);
        entity.setProperty("ownerName", user.name);
        entity.setProperty("ownershipType", ownershipType);
        entity.setProperty("createdAt", new Date());
        entity.setProperty("lastModifiedAt", new Date());
        datastore.put(entity);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result", true);
        jsonObject.put("key", KeyFactory.keyToString(entity.getKey()));
        return jsonObject.toString();
    }

    @RequestMapping(value = "/ajax/todo-list/remove-todo-list", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String removeTodoList(HttpServletRequest request, HttpServletResponse response, @RequestBody String body) {
        String idToken = null;
        Key todoListKey = null;
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonBody = (JSONObject) parser.parse(body);
            idToken = (String) jsonBody.get("IDtoken");
            if(jsonBody.get("key") == null)
                return ResponseTools.plainError("json parsing error");
            todoListKey = KeyFactory.stringToKey((String) jsonBody.get("key"));
        } catch (ParseException | ClassCastException | IllegalArgumentException e) {
            Logging.exception(e);
            return ResponseTools.plainError("json parsing error");
        }
        GoogleUserInfo user = GoogleAuth.googleVerification(idToken);
        if(user == null){
            return ResponseTools.plainError("google authentication error");
        }

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity todoList;
        try {
            todoList = datastore.get(todoListKey);
        } catch (EntityNotFoundException e) {
            Logging.exception(e);
            return ResponseTools.plainError("todo list not found");
        }
        // delete items
        miscTools.deleteItems(datastore, todoListKey);
        // check permission
        if(!Objects.equals(todoList.getProperty("owner"), user.id))
            return ResponseTools.plainError("permission denied");

        datastore.delete(todoListKey);

        return ResponseTools.plainSuccess();
    }

    @RequestMapping(value = "/ajax/todo-list/list-todo-list", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String listTodo(HttpServletRequest request, HttpServletResponse response,
                           @RequestBody String body){
        String idToken = null;
        String ownershipType = null;
        Cursor startCursor = null;
        int listNum = -1;
        int itemPreviewNum = 0;
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonBody = (JSONObject) parser.parse(body);
            idToken = (String) jsonBody.get("IDtoken");
            ownershipType = (String) jsonBody.get("ownershipType");
            if(!Objects.equals(ownershipType, "public") &&
                    !Objects.equals(ownershipType, "private") &&
                    !Objects.equals(ownershipType, "my-list"))
                return ResponseTools.plainError("format error");
            if(jsonBody.get("startCursor") != null)
                startCursor = Cursor.fromWebSafeString((String) jsonBody.get("startCursor"));
            if(jsonBody.get("listNum") != null)
                listNum = (int) (long) jsonBody.get("listNum");
            if(listNum > 30 || listNum < 0)
                listNum = 10;
            if(jsonBody.get("itemPreviewNum") != null)
                itemPreviewNum = (int) (long) jsonBody.get("itemPreviewNum");
            if(itemPreviewNum > 10 || itemPreviewNum < 0)
                itemPreviewNum = 0;
        } catch (ParseException | ClassCastException | IllegalArgumentException e) {
            Logging.exception(e);
            return ResponseTools.plainError("json parsing error");
        }
        // bypass user auth if the request is public
        if(Objects.equals(ownershipType, "public") && idToken==null) idToken = GoogleAuth.DUMMY_USER_TOKEN;
        GoogleUserInfo user = GoogleAuth.googleVerification(idToken);
        if(user == null){
            return ResponseTools.plainError("google authentication error");
        }

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query query = new Query("todo-list");
        FetchOptions fetchOptions = FetchOptions.Builder.withLimit(listNum);
        query.addSort("lastModifiedAt", Query.SortDirection.DESCENDING);
        if(Objects.equals(ownershipType, "private")){
            query.setFilter(new Query.CompositeFilter(Query.CompositeFilterOperator.AND, Arrays.<Query.Filter>asList(
                    new Query.FilterPredicate("owner", Query.FilterOperator.EQUAL, user.id),
                    new Query.FilterPredicate("ownershipType", Query.FilterOperator.EQUAL, "private"))));
        }else if(Objects.equals(ownershipType, "my-list")){
            query.setFilter(new Query.FilterPredicate("owner", Query.FilterOperator.EQUAL, user.id));
        }else{
            query.setFilter(new Query.FilterPredicate("ownershipType", Query.FilterOperator.EQUAL, "public"));
        }
        if(startCursor != null){
            fetchOptions.startCursor(startCursor);
        }
        QueryResultList<Entity> results = datastore.prepare(query).asQueryResultList(fetchOptions);
        JSONArray list = new JSONArray();
        for (Entity entity : results) {
            JSONObject todoList = new JSONObject();
            todoList.put("name", entity.getProperty("name"));
            todoList.put("ownerID", entity.getProperty("owner"));
            todoList.put("ownershipType", entity.getProperty("ownershipType"));
            todoList.put("ownerName", entity.getProperty("ownerName"));
            todoList.put("key", KeyFactory.keyToString(entity.getKey()));
            list.add(todoList);
            if(itemPreviewNum <= 0) continue;
            // retrieve items
            JSONArray listJson = new JSONArray();
            Query itemsQuery = new Query("todo-list-item", entity.getKey());
            query.addSort("endDate", Query.SortDirection.DESCENDING);
            PreparedQuery preQuery = datastore.prepare(itemsQuery);
            Iterator<Entity> itemsEntityList = preQuery.asIterator(FetchOptions.Builder.withLimit(itemPreviewNum));
            while(itemsEntityList.hasNext()){
                Entity itemEntity = itemsEntityList.next();
                JSONObject itemJson = new JSONObject();
                itemJson.put("description", itemEntity.getProperty("description"));
                itemJson.put("completed", itemEntity.getProperty("completed"));
                listJson.add(itemJson);
            }
            todoList.put("itemPreview", listJson);
            // retrieve completed
            Query itemsNumQuery = new Query("todo-list-item", entity.getKey());
            int totalItemsNum = datastore.prepare(itemsNumQuery).countEntities(FetchOptions.Builder.withDefaults());
            itemsNumQuery.setFilter(new Query.FilterPredicate("completed", Query.FilterOperator.EQUAL, true));
            int completedNum = datastore.prepare(itemsNumQuery).countEntities(FetchOptions.Builder.withDefaults());
            todoList.put("totalItemsNum", totalItemsNum);
            todoList.put("completedNum", completedNum);
        }
        JSONObject resultJson = new JSONObject();
        resultJson.put("endCursor", results.getCursor().toWebSafeString());
        resultJson.put("result", true);
        resultJson.put("list", list);
        return resultJson.toString();
    }
}
