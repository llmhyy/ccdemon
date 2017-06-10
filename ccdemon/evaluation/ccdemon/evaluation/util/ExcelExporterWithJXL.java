package ccdemon.evaluation.util;

import java.io.File;
import java.util.ArrayList;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class ExcelExporterWithJXL {
	WritableWorkbook book;
	WritableSheet sheet;

	public void start(String fileName){
		//title
        String titles[] = {"cloneSetID","instanceNum","avgLineNum","typeIIorIII","type1to7","recall","precision","configurationEffort","savedEditingEffort","trialTime","cloneInstance"};

        try {
        	book = Workbook.createWorkbook(new File(fileName + ".xls")); 
        	sheet = book.createSheet("data", 0); 
	        for(int i = 0; i < titles.length; i++){
	            sheet.addCell(new Label(i,0,titles[i])); 
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void export(ArrayList<String> datas, int lineNum){
		
        for(int j = 0; j < datas.size(); j++){
            try {
				sheet.addCell(new Label(j, lineNum+1, datas.get(j)));
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
        }
	}
	
	public void end(){
        try {
			book.write();
			book.close(); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
