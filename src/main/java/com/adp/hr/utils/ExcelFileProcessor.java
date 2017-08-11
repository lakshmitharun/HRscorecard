package com.adp.hr.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.adp.hr.HRScoreCardConfiguration;
import com.adp.hr.HRScoreCardConfiguration.CellMetaDataVO;
import com.adp.hr.HRScoreCardConfiguration.TabMetaDataVO;

/**
 * This class ExcelFileProcessor will accept the file path as input and do the required validations
 * 
 * @author rayudura
 *
 */
public class ExcelFileProcessor {

   private static final Logger LOGGER = LoggerFactory.getLogger(ExcelFileProcessor.class);

   // Workflow for the uploaded file input
   private Workbook workbook;

   private HRScoreCardConfiguration hrScoreCardConfiguration;

   // This will contains all the errors in which needs to be corrected
   private Map<String, List<List<ErrorCellVO>>> uiErrorCells = new ConcurrentHashMap<>();

   public ExcelFileProcessor(String filePath, HRScoreCardConfiguration hrScoreCardConfiguration) throws IOException {
      this.hrScoreCardConfiguration = hrScoreCardConfiguration;
      if (filePath != null && !StringUtils.isEmpty(filePath)) {
         FileInputStream fis = new FileInputStream(new File(filePath)); // Finds the workbook
         workbook = new XSSFWorkbook(fis);
      }

   }

   /**
    * Process the file if the file has multiple tabs it will validate all the tabs
    * 
    * @throws BiffException
    * @throws IOException
    */
   public void processFile() {
      List<TabMetaDataVO> customTabs = hrScoreCardConfiguration.getTabs();
      for (int i = 0; i < 2; i++) {
         TabMetaDataVO customTab = customTabs.get(i);
         LOGGER.info("Start Processing Tab =[" + customTab.getName() + "]");
         // Create Error Entry in HashMap
         uiErrorCells.put(customTab.getName(), new ArrayList<>());
         this.validateTab(customTab);
         // check for any Validation remove the map entry if there are no errors
         if (uiErrorCells.get(customTab.getName()).isEmpty()) {
            uiErrorCells.remove(customTab.getName());
         }

         LOGGER.info("End Processing Tab =[" + customTab.getName() + "]");
      }
   }

   /**
    * Validate tab based on the meta data in tab
    * 
    * @param tabMetaData
    * @throws BiffException
    * @throws IOException
    */
   private void validateTab(TabMetaDataVO tabMetaData) {

      List<CellMetaDataVO> cellsMetaDataList = tabMetaData.getCells();
      String sheetName = tabMetaData.getName();

      Sheet sheet = getSheet(sheetName);
      Iterator<Row> rowItr = sheet.iterator();
      while (rowItr.hasNext()) {
         Row row = rowItr.next();
         if (row.getRowNum() == 0) {
            continue; // just skip the rows if row number is 0 or 1
         }
         validateRow(sheetName, row, cellsMetaDataList);
      }

   }

   /**
    * validate the cell based on the List<Cell> represents the row of the given sheet
    * 
    * @param asList
    * @param cellsMetaDataMap
    */
   private void validateRow(String sheetName, Row row, List<CellMetaDataVO> cellsMetaDataList) {

      boolean hasValidationErrors = false;
      List<ErrorCellVO> errorCellsList = new ArrayList<>();
      Iterator<Cell> cellItr = row.cellIterator();
      Map<Integer, Boolean> columnStatusMap = new HashMap<>();
      while (cellItr.hasNext()) {
         Cell cell = cellItr.next();
         CellMetaDataVO cellMetaDataVO = cellsMetaDataList.get(cell.getColumnIndex());

         SupportedDataType dataType = SupportedDataType.getDateType(cellMetaDataVO.getDataType());
         LOGGER.info("Validating data of row = [" + row.getRowNum() + "] column=[" + cellMetaDataVO.getName() + "]");
         ErrorCellVO errorCell = new ErrorCellVO();
         errorCell.setData(this.getCellValue(cell));
         errorCell.setDataType(dataType.getCode());

         switch (dataType) {
            case STRING:
               try {
                  errorCell.setData(String.valueOf(cell.getStringCellValue()));

                  hasValidationErrors = false;
               }
               catch (Exception e) {
                  errorCell.setErrorMessage("Invalid String");
                  hasValidationErrors = true;
               }
               break;
            case INTEGER:
               try {
                  errorCell.setData(String.valueOf(cell.getNumericCellValue()));
                  hasValidationErrors = false;
               }
               catch (Exception e) {
                  errorCell.setErrorMessage("Invalid Integer");
                  hasValidationErrors = true;
               }
               break;
            case DATE:
               try {
                  if (DateUtil.isValidExcelDate(cell.getNumericCellValue())) {
                     errorCell.setData(ValidationUtils.convertDateToStr(cell.getDateCellValue()));
                     hasValidationErrors = false;
                  }
               }
               catch (Exception e) {
                  errorCell.setErrorMessage("Invalid Date");
                  hasValidationErrors = true;
               }
               break;
            default:
               hasValidationErrors = true;
               break;
         }
         errorCellsList.add(errorCell);
         columnStatusMap.put(cell.getColumnIndex(), hasValidationErrors);
      }
      LOGGER.info("Row Validation Status =", hasValidationErrors);
      Set<Integer> keys = columnStatusMap.keySet();
      for (Integer key : keys) {
         if (columnStatusMap.get(key)) {
            uiErrorCells.get(sheetName).add(errorCellsList);
            break;
         }
      }
      LOGGER.info("End Processing Row =[" + row.getRowNum() + "]");

   }

   private String getCellValue(Cell cell) {
      LOGGER.info("Cell Type = [" + cell.getCellType() + "]");
      switch (cell.getCellType()) {
         case Cell.CELL_TYPE_STRING:
            return cell.getStringCellValue();
         case Cell.CELL_TYPE_NUMERIC:
            return String.valueOf(cell.getNumericCellValue());
         case Cell.CELL_TYPE_BOOLEAN:
            return String.valueOf(cell.getBooleanCellValue());
         default:
            return null;
      }
   }

   /**
    * Get the Sheet Object form the Workbook
    * 
    * @param name
    * @return
    * @throws BiffException
    * @throws IOException
    */
   private Sheet getSheet(String name) {
      return workbook.getSheet(name);
   }

   /**
    * Return all the errors
    * 
    * @return
    */
   public Map<String, List<List<ErrorCellVO>>> getUiErrorCells() {
      return uiErrorCells;
   }

   private boolean isCellDateFormatted(Cell cell) {
      return DateUtil.isCellDateFormatted(cell);

   }

}
