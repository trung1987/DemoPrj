package untils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtils {

	
	public static Workbook newWorkbook(String filePath, String fileName) throws IOException {
		File file = new File(filePath + "//" + fileName);
		FileInputStream inputStream = new FileInputStream(file);
		Workbook wb = null;
		String fileExtensionName = fileName.substring(fileName.indexOf("."));
		if (fileExtensionName.equalsIgnoreCase(".xlsx")) {
			wb = new XSSFWorkbook(inputStream);
		} else if (fileExtensionName.equalsIgnoreCase(".xls")) {
			wb = new HSSFWorkbook(inputStream);
		}
		return wb;
	}
	
	public static ArrayList<String> readExcelFileAtColumn(String filePath, String fileName, String sheetName,
			int column)  {
		ArrayList<String> columnData = new ArrayList<String>();
		try {
			Workbook wb = newWorkbook(filePath, fileName);
			Sheet sheet = wb.getSheet(sheetName);
			
			//lay khoang row co data
			int rowCount = sheet.getLastRowNum() - sheet.getFirstRowNum();
			for (int i = 0; i < rowCount + 1; i++) {
				try {
					Row row = sheet.getRow(i);
					Cell cell = row.getCell(column);
					cell.setCellType(CellType.STRING);
					columnData.add(cell.getStringCellValue());
				} catch (Exception e) {
					// if row(i) = null / empty
					columnData.add("");
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Cannot read data at column:" + column);
		}
		
		return columnData;
	}
	
	
	public static ArrayList<String> readExcelFileAtRow(String filePath, String fileName, String sheetName, int row,
			int startColumn, int endColumn) {
		ArrayList<String> rowData = new ArrayList<String>();
		try {
			Workbook wb = newWorkbook(filePath, fileName);
			Sheet sheet = wb.getSheet(sheetName);
			Row rowExcel = null;
			rowExcel = sheet.getRow(row);
			for (int i = startColumn; i <= endColumn; i++) {
				try {
					Cell cell = rowExcel.getCell(i);
					cell.setCellType(CellType.STRING);
					rowData.add(cell.getStringCellValue());
				} catch (Exception e) {
					// if row(i) = null / empty
					rowData.add("");
				}
			}
		} catch (Exception e) {
			System.out.println("Cannot read data at row:" + row);
		}
		return rowData;
	}


	public static String getDataAtCell(String filePath, String fileName, String sheetName, int row, int column)			 {
		String CellData;
		try {
			Workbook wb = newWorkbook(filePath, fileName);
			
			Sheet sheet = wb.getSheet(sheetName);
			// in excel row index from 0 -> n
			Row rowExcel = sheet.getRow(row );
			Cell cell = rowExcel.getCell(column);
			cell.setCellType(CellType.STRING);
			CellData = cell.getStringCellValue();
		} catch (Exception e) {
			CellData = "";
		}
		return CellData;
	}


}
