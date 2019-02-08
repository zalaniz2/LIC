package LIC.UC04v1.View;

import LIC.UC04v1.model.Student;
import LIC.UC04v1.repositories.StudentRepository;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.servlet.view.document.AbstractXlsView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

public class ExcelView extends AbstractXlsView {

    private StudentRepository studentRepository;

    @Override
    protected void buildExcelDocument(Map<String, Object> model,
                                      Workbook workbook1,
                                      HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        response.setHeader("Content-Disposition", "attachment; filename=\"my-xls-file.xls\""); //set content type of the response so that jQuery knows what it can expect

        String[] columns = {"Mon", "Tues", "Weds", "Thurs", "Fri", "Sat", "Sun"};

        Workbook workbook = new XSSFWorkbook();

        CreationHelper creationHelper = workbook.getCreationHelper();

        String stu = "Tom Tom";
        String name = String.format("%s's Weekly Schedule", stu);

        Sheet sheet = workbook.createSheet(name);

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short)17);
        headerFont.setColor(new XSSFColor(new java.awt.Color(77,25,121)).getIndex());


        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);

        for(int i = 0; i < columns.length; i++){
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for(Student stu1: studentRepository.findAll()){
            Row row = sheet.createRow(rowNum++);

            row.createCell(0)
                    .setCellValue(stu1.getName());

            row.createCell(1)
                    .setCellValue(stu1.getId());

        }

        for (int i = 0; i < columns.length; i++){
            sheet.autoSizeColumn(i);
        }

        FileOutputStream fileOut = new FileOutputStream("Test-schedule-file.xlsx");
        workbook.write(fileOut);
        fileOut.close();

        workbook.close();

    }
}