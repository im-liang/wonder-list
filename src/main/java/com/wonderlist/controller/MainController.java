package com.wonderlist.controller;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Controller
public class MainController {
    @RequestMapping("/")
    public String home(HttpServletRequest request, HttpServletResponse response) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity entity = new Entity("visit");
        entity.setProperty("user_ip", request.getRemoteAddr());
        entity.setProperty("timestamp", new Date());
        datastore.put(entity);

        return "home";
    }
}
