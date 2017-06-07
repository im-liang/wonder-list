package com.wonderlist.controller;

import com.wonderlist.utility.GoogleAuth;
import com.wonderlist.utility.GoogleUserInfo;
import com.wonderlist.utility.Logging;
import com.wonderlist.utility.ResponseTools;
import com.google.appengine.api.datastore.*;
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
import java.util.Objects;

@Controller
public class TodoListMetaController {
    @RequestMapping(value = "/ajax/todo-list/todo-list-meta-read", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String readTodoListMeta(HttpServletRequest request, HttpServletResponse response, @RequestBody String body) {
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

        JSONObject result = new JSONObject();
        result.put("result", true);
        result.put("name", todoList.getProperty("name"));
        result.put("ownershipType", todoList.getProperty("ownershipType"));
        result.put("owner", todoList.getProperty("owner"));
        result.put("ownerName", todoList.getProperty("ownerName"));

        return result.toString();
    }

    @RequestMapping(value = "/ajax/todo-list/todo-list-meta-write", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String writeTodoListMeta(HttpServletRequest request, HttpServletResponse response, @RequestBody String body) {
        String idToken = null;
        Key todoListKey = null;
        String name = null;
        String ownershipType = null;
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonBody = (JSONObject) parser.parse(body);
            idToken = (String) jsonBody.get("IDtoken");
            if(jsonBody.get("key") == null)
                return ResponseTools.plainError("json parsing error");
            todoListKey = KeyFactory.stringToKey((String) jsonBody.get("key"));
            name = (String) jsonBody.get("name");
            ownershipType = (String) jsonBody.get("ownershipType");
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

        if(name != null) todoList.setProperty("name", name);
        if(ownershipType != null) todoList.setProperty("ownershipType", ownershipType);

        // change last modified
        todoList.setProperty("lastModifiedAt", new Date());
        datastore.put(todoList);

        return ResponseTools.plainSuccess();
    }
}
