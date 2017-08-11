package com.adp.hr.utils;

import java.util.List;

/**
 * This call is responsible for the sending errors to UI layer
 * 
 * @author rayudura
 *
 */
public class ErrorCellVO {

   private String data;

   private String dataType;

   private String errorMessage;

   private List<String> possibleValues;

   private boolean isValid;

   public String getDate() {
      return data;
   }

   public void setData(String data) {
      this.data = data;
   }

   public String getDataType() {
      return dataType;
   }

   public void setDataType(String dataType) {
      this.dataType = dataType;
   }

   public List<String> getPossibleValues() {
      return possibleValues;
   }

   public void setPossibleValues(List<String> possibleValues) {
      this.possibleValues = possibleValues;
   }

   public boolean isValid() {
      return isValid;
   }

   public void setValid(boolean isValid) {
      this.isValid = isValid;
   }

   public String getErrorMessage() {
      return errorMessage;
   }

   public void setErrorMessage(String errorMessage) {
      this.errorMessage = errorMessage;
   }

}
