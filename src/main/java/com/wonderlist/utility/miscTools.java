package com.endlesslist.utility;

import com.google.appengine.api.datastore.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

public class miscTools {
    private static DateFormat dateFormat = initDate2ISOString();
    
    private static DateFormat initDate2ISOString(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat;
    }
    
    public static String date2ISOString(Date date){
        return dateFormat.format(date);
    }
    
    public static Date ISOString2Date(String dateStr){
        return javax.xml.bind.DatatypeConverter.parseDateTime(dateStr).getTime();
    }
    
    public static void deleteItems(DatastoreService datastore, Key listKey){
        Transaction txn = datastore.beginTransaction();
        try {
            Query query = new Query("todo-list-item", listKey);
            PreparedQuery preQuery = datastore.prepare(query);
            Iterator<Entity> entityList = preQuery.asIterator();
            while (entityList.hasNext()) {
                datastore.delete(txn, entityList.next().getKey());
            }
            txn.commit();
        } finally {
            if(txn.isActive()) txn.rollbackAsync();
        }
    }
}
