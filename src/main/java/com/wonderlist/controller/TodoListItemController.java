package com.wonderlist.controller;


import com.wonderlist.utility.*;
import com.google.appengine.api.datastore.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Iterator;
import java.util.Objects;

@Controller
public class TodoListItemController {
    @RequestMapping(value = "/ajax/todo-list/todo-list-read", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String todoRead(HttpServletRequest request, HttpServletResponse response,
                           @RequestBody String body){
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
        // check permission
        if(!Objects.equals(todoList.getProperty("owner"), user.id) &&
                !Objects.equals(todoList.getProperty("ownershipType"), "public"))
            return ResponseTools.plainError("permission denied");

        // read all items and write to json
        JSONArray listJson = new JSONArray();
        Query query = new Query("todo-list-item", todoListKey);
        query.addSort("endDate", Query.SortDirection.DESCENDING);
        PreparedQuery preQuery = datastore.prepare(query);
        Iterator<Entity> entityList = preQuery.asIterator();
        while(entityList.hasNext()){
            Entity entity = entityList.next();
            JSONObject itemJson = new JSONObject();
            itemJson.put("category", entity.getProperty("category"));
            itemJson.put("description", entity.getProperty("description"));
            itemJson.put("startDate", miscTools.date2ISOString((Date) entity.getProperty("startDate")));
            itemJson.put("endDate", miscTools.date2ISOString((Date) entity.getProperty("endDate")));
            itemJson.put("completed", entity.getProperty("completed"));
            listJson.add(itemJson);
        }
        JSONObject result = new JSONObject();
        result.put("result", true);
        result.put("list", listJson);

        return result.toString();
    }

    @RequestMapping(value = "/ajax/todo-list/todo-list-write", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String todoWrite(HttpServletRequest request, HttpServletResponse response,
                            @RequestBody String body){
        String idToken = null;
        Key todoListKey = null;
        JSONArray listJson = null;
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonBody = (JSONObject) parser.parse(body);
            idToken = (String) jsonBody.get("IDtoken");
            listJson = (JSONArray) jsonBody.get("list");
            if(listJson == null) return ResponseTools.plainError("json parsing error");
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
        // check permission
        if(!Objects.equals(todoList.getProperty("owner"), user.id))
            return ResponseTools.plainError("permission denied");
        // deleting old items
        miscTools.deleteItems(datastore, todoListKey);
        // adding new items
        int totalCount = 0;
        for(Object o : listJson){
            if(totalCount >= 300) break;
            try{
                JSONObject itemJson = (JSONObject) o;
                Entity item = new Entity("todo-list-item", todoList.getKey());
                item.setProperty("category", itemJson.get("category"));
                item.setProperty("description", itemJson.get("description"));
                Date startDate = miscTools.ISOString2Date((String) itemJson.get("startDate"));
                if(startDate == null) continue;
                item.setProperty("startDate", startDate);
                Date endDate = miscTools.ISOString2Date((String) itemJson.get("endDate"));
                if(endDate == null) continue;
                item.setProperty("endDate", endDate);
                item.setProperty("completed", itemJson.get("completed"));
                datastore.put(item);
            } catch (ClassCastException ignored) {}
            totalCount++;
        }
        // change last modified
        todoList.setProperty("lastModifiedAt", new Date());
        datastore.put(todoList);

        return ResponseTools.plainSuccess();
    }
}
