package com.wonderlist.controller;

import com.wonderlist.utility.*;
import com.google.appengine.api.datastore.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
public class MainRestController {
    @RequestMapping("/exception")
    @ResponseBody
    public String exception(HttpServletRequest request, HttpServletResponse response) {
        if(!Objects.equals(request.getParameter("e"), "abc"))throw new IllegalArgumentException("test exception");
        return "guestbook";
    }

    @RequestMapping("/my_visit")
    @ResponseBody
    public String myVisit(HttpServletRequest request, HttpServletResponse response) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query query = new Query("visit")
                .setFilter(new Query.FilterPredicate("user_ip", Query.FilterOperator.EQUAL, request.getRemoteAddr()))
                .addSort("timestamp", Query.SortDirection.DESCENDING);
        PreparedQuery preQuery = datastore.prepare(query);
        Iterator<Entity> entityList = preQuery.asIterator(FetchOptions.Builder.withLimit(10));
        StringBuilder result_str = new StringBuilder("<p>Your visit:</p>");
        while(entityList.hasNext()){
            Entity entity = entityList.next();
            result_str.append("<p>Time: ")
                    .append(entity.getProperty("timestamp"))
                    .append(" Addr: ")
                    .append(entity.getProperty("user_ip"))
                    .append("entity: ")
                    .append(KeyFactory.keyToString(entity.getKey()))
                    .append("</p>");
        }
        return result_str.toString();
    }

    @RequestMapping(value = "/ajax/todo-list/login", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public String login(HttpServletRequest request, HttpServletResponse response,
                        @RequestBody String body){
        String idToken = null;
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonBody = (JSONObject) parser.parse(body);
            idToken = (String) jsonBody.get("IDtoken");
        } catch (ParseException | ClassCastException e) {
            Logging.exception(e);
            return ResponseTools.plainError("json parsing error");
        }
        GoogleUserInfo user = GoogleAuth.googleVerification(idToken);
        if(user != null){
            return ResponseTools.plainSuccess();
        }else{
            return ResponseTools.plainError("google authentication error");
        }
    }
}
