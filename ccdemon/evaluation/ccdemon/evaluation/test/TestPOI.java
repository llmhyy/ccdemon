package ccdemon.evaluation.test;

import java.io.FileInputStream; 
import java.io.FileOutputStream; 
import java.io.IOException; 
import java.io.InputStream; 
 



import org.apache.poi.ss.usermodel.Cell; 
import org.apache.poi.ss.usermodel.Row; 
import org.apache.poi.ss.usermodel.Sheet; 
import org.apache.poi.ss.usermodel.Workbook; 
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
 
public class TestPOI { 
    //使用POI创建excel工作簿 
    @SuppressWarnings("resource")
	public static void createWorkBook() throws IOException { 
        //创建excel工作簿 
        Workbook wb = new XSSFWorkbook(); 
        //创建第一个sheet（页），命名为 new sheet 
        Sheet sheet = wb.createSheet("new sheet"); 
        //Row 行 
        //Cell 方格 
        // Row 和 Cell 都是从0开始计数的 
         
        // 创建一行，在页sheet上 
        Row row = sheet.createRow((short) 0); 
        // 在row行上创建一个方格 
        Cell cell = row.createCell(0); 
        //设置方格的显示 
        cell.setCellValue(1); 
 
        // Or do it on one line. 
        row.createCell(1).setCellValue(1.2); 
        row.createCell(2).setCellValue("This is a string 速度反馈链接"); 
        row.createCell(3).setCellValue(true); 
 
        //创建一个文件 命名为workbook.xls 
        FileOutputStream fileOut = new FileOutputStream("workbook.xlsx"); 
        // 把上面创建的工作簿输出到文件中 
        wb.write(fileOut); 
        //关闭输出流 
        fileOut.close(); 
        System.out.println("done");
    } 
 
    //使用POI读入excel工作簿文件 
    public static void readWorkBook() throws Exception { 
        // poi读取excel 
        //创建要读入的文件的输入流 
        InputStream inp = new FileInputStream("workbook.xls"); 
         
        //根据上述创建的输入流 创建工作簿对象 
        Workbook wb = WorkbookFactory.create(inp); 
        //得到第一页 sheet 
        //页Sheet是从0开始索引的 
        Sheet sheet = wb.getSheetAt(0); 
        //利用foreach循环 遍历sheet中的所有行 
        for (Row row : sheet) { 
            //遍历row中的所有方格 
            for (Cell cell : row) { 
                //输出方格中的内容，以空格间隔 
                System.out.print(cell.toString() + "  "); 
            } 
            //每一个行输出之后换行 
            System.out.println(); 
        } 
        //关闭输入流 
        inp.close(); 
    } 
 
    public static void main(String[] args) throws Exception { 
    	TestPOI.createWorkBook(); 
//    	TestPOI.readWorkBook(); 
    } 
} 