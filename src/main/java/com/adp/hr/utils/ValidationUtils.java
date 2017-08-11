package com.adp.hr.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.util.StringUtils;

/**
 * This class will have all the validation logic for the input data type in meta data files
 * 
 * @author rayudura
 *
 */
public class ValidationUtils {

   final static String DATE_FORMAT = "dd/MM/yyyy";

   /**
    * Check the input string is valid or not
    * 
    * @param input
    * @return
    */
   public static boolean isValidString(String input) {
      return input != null && !StringUtils.isEmpty(input);
   }

   /**
    * Checks the ionput String is valid Integer or not
    * 
    */
   public static boolean isValidInteger(String input) {
      try {
         Integer.parseInt(input);
      }
      catch (NumberFormatException ex) {
         return false;
      }
      return true;
   }

   /**
    * Checks the input string is valid date or not
    * 
    * @param date
    * @return
    */
   public static String convertDateToStr(Date date) {
      DateFormat df = new SimpleDateFormat(DATE_FORMAT);
      return df.format(date);
   }

}
