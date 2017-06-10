package ccdemon.evaluation.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class GenerateBubblePlotData {
	
	public class Data{
		double x = 0;
		double y = 0;
		
		public Data(double x, double y){
			this.x = x;
			this.y = y;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			long temp;
			temp = Double.doubleToLongBits(x);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			temp = Double.doubleToLongBits(y);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Data other = (Data) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
				return false;
			if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
				return false;
			return true;
		}

		private GenerateBubblePlotData getOuterType() {
			return GenerateBubblePlotData.this;
		}
		
	}
	
	public static void main(String[] args){
	    
		GenerateBubblePlotData generate = new GenerateBubblePlotData();
		try {
			generate.run("twe0");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("resource")
	public void run(String projectName) throws Exception{
		InputStream inp = new FileInputStream(projectName + ".xlsx");

	    Workbook wb = WorkbookFactory.create(inp);
	    Sheet sheet = wb.getSheetAt(0);
	    HashMap<Data, Integer> dataMap = new HashMap<Data, Integer>();
	    
	    for(Row row : sheet){
	    	if(row.getRowNum() == 0){
	    		continue;
	    	}
	    	
	    	Cell yCell = row.getCell(6); //recall
	    	Cell xCell = row.getCell(14); //psee
	    	
//	    	if(yCell.getCachedFormulaResultType() == Cell.CELL_TYPE_ERROR
	    	if(yCell.getCellType() == Cell.CELL_TYPE_STRING
	    			|| xCell.getCellType() == Cell.CELL_TYPE_STRING){
	    		//Fmeasure is NaN, or saved_edit is NaN/-Infinity
	    		continue;
	    	}else{
		    	double x = xCell.getNumericCellValue();
		    	if(x < 0){
		    		continue;
		    	}
		    	double y = yCell.getNumericCellValue();
		    	Data d = new Data(x, y);
		    	if(dataMap.containsKey(d)){
		    		dataMap.put(d, dataMap.get(d)+1);
		    	}else{
		    		dataMap.put(d, 1);
		    	}
	    	}
	    }
	    
	    // Write the output to a file
        Workbook outputWB = new XSSFWorkbook(); 
        Sheet outputSheet = outputWB.createSheet("output"); 
         
        Row titlerow = outputSheet.createRow((short) 0); 
        //title
        String titles[] = {"x","y","count"};
        for(int i = 0; i < titles.length; i++){
        	titlerow.createCell(i).setCellValue(titles[i]); 
        }
 
        Iterator<Entry<Data, Integer>> iter = dataMap.entrySet().iterator(); 
        int count = 0;
        while (iter.hasNext()) { 
            Entry<Data, Integer> entry = (Entry<Data, Integer>) iter.next(); 
            Data key = entry.getKey(); 
            int val = entry.getValue(); 
    		Row row = outputSheet.createRow(count+1);
    		row.createCell(0).setCellValue(key.x);
    		row.createCell(1).setCellValue(key.y);
    		row.createCell(2).setCellValue(val);
    		count++;
        } 
	    
	    FileOutputStream fileOut = new FileOutputStream(projectName + "-output.xlsx");
	    outputWB.write(fileOut);
	    fileOut.close();
	    
	    System.out.println(projectName + " done");
	}
}
