/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author michael
 */
public class CommonMethods {
    
    // Converts a date to String with format "yyyy-MM-dd'T'h:m:ssZ"
    // if date is null return a null string
    protected static String convertDate(Date date) {
        
        if (date != null)
            return new SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(date);
        else
            //return new SimpleDateFormat("yyyy-MM-dd'T'h:m:ssZ").format(new Date(0L));
            return null;
    }
    
}
