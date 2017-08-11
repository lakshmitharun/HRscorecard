package com.adp.hr.utils;

/**
 * 
 * List of dataTypes supported for validations
 * 
 * @author rayudura
 *
 */
public enum SupportedDataType {

   STRING("String"), INTEGER("Integer"), DATE("Date");

   private String code;

   SupportedDataType(String code) {
      this.code = code;
   }

   public static SupportedDataType getDateType(String code) {
      for (SupportedDataType dataType : values()) {
         if (dataType.code.equalsIgnoreCase(code)) {
            return dataType;
         }
      }
      throw new EnumConstantNotPresentException(SupportedDataType.class, code + "");
   }

   public String getCode() {
      return code;
   }

}
