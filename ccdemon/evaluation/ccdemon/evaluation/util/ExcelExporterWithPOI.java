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

public class ExcelExporterWithPOI {
	Workbook book; 
	Sheet sheet;

	public void start(){
		book = new XSSFWorkbook(); 
		sheet = book.createSheet("data");  
        Row row = sheet.createRow((short) 0); 
        //title
        String titles[] = {"cloneSetID","instanceNum","avgLineNum","typeIIorIII",
        		"type1to7-recall","type1to7-Fmeasure","recall","precision","Fmeasure",
        		"historyNum","environmentNum","ruleNum","configurationEffort","savedEditingEffort","partialSEE",
        				"totalFalsePositiveNum","goodCaseNum","isInfluencedByFP",
        				"trialTime","diffTime","APITime","cloneInstance"};
        for(int i = 0; i < titles.length; i++){
        	row.createCell(i).setCellValue(titles[i]); 
        }
	}
	
	public void startAgain(String projectName){
		try {
			InputStream inp = new FileInputStream(projectName + ".xlsx");
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
			FileOutputStream fileOut = new FileOutputStream(projectName + ".xlsx");
			book.write(fileOut); 
			fileOut.close(); 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

}
