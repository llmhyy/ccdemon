package ccdemon.evaluation.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CPExcelExporterWithPOI {
	Workbook book; 
	Sheet sheet;

	public void start(){
		book = new XSSFWorkbook(); 
		sheet = book.createSheet("cp");  
        Row row = sheet.createRow((short) 0); 
        //title
        String titles[] = {"cp candidate rank","total candidate num","cloneInstance"};
        for(int i = 0; i < titles.length; i++){
        	row.createCell(i).setCellValue(titles[i]); 
        }
	}
	
	public void startAgain(String projectName){
		try {
			InputStream inp = new FileInputStream(projectName + "-cp.xlsx");
			book = WorkbookFactory.create(inp);
			sheet = book.getSheetAt(0);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void export(ArrayList<String> datas, int lineNum){
		Row row = sheet.createRow(lineNum+1);
        try {
        	for(int j = 0; j < datas.size(); j++){
        		row.createCell(j).setCellValue(datas.get(j));
        	}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void end(String projectName){
		try {
			FileOutputStream fileOut = new FileOutputStream(projectName + "-cp.xlsx");
			book.write(fileOut); 
			fileOut.close(); 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

}
