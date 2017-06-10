package ccdemon.evaluation.test;

import java.io.File;
import jxl.*;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class TestJXL{
    public static void main(String[] args) {
        //标题行
        String title[]={"角色","编号","功能名称","功能描述"};
        //内容
        String context[][]={{"UC11","设置课程","创建课程"},
                            {"UC12","设置学生名单","给出与课程关联的学生名单"},
                            {"UC21","查看学生名单",""},
                            {"UC22","查看小组信息","显示助教所负责的小组列表信息"}
                            };
        //操作执行
        try { 
            //t.xls为要新建的文件名
            WritableWorkbook book= Workbook.createWorkbook(new File("t.xls")); 
            //生成名为“第一页”的工作表，参数0表示这是第一页 
            WritableSheet sheet=book.createSheet("第一页",0); 
            
            //写入内容
            for(int i=0;i<4;i++)    //title
                sheet.addCell(new Label(i,0,title[i])); 
            for(int i=0;i<4;i++)    //context
            {
                for(int j=0;j<3;j++)
                {
                    sheet.addCell(new Label(j+1,i+1,context[i][j])); 
                }
            }
            sheet.addCell(new Label(0,1,"教师"));
            sheet.addCell(new Label(0,3,"助教"));
            
            /*合并单元格.合并既可以是横向的，也可以是纵向的
             *WritableSheet.mergeCells(int m,int n,int p,int q);   表示由(m,n)到(p,q)的单元格组成的矩形区域合并
             * */
            sheet.mergeCells(0,1,0,2);
            sheet.mergeCells(0,3,0,4);
            
            //写入数据
            book.write(); 
            //关闭文件
            book.close(); 
        }
        catch(Exception e) { } 
    }
}
