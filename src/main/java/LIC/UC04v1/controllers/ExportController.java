package LIC.UC04v1.controllers;


import LIC.UC04v1.model.Clerkship;
import LIC.UC04v1.model.Doctor;
import LIC.UC04v1.model.Student;
import LIC.UC04v1.repositories.ClerkshipRepository;
import LIC.UC04v1.repositories.DoctorRepository;
import LIC.UC04v1.repositories.StudentRepository;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.supercsv.cellprocessor.FmtDate;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import java.util.*;
import java.awt.Color;


@Controller
public class ExportController{

    private DoctorRepository doctorRepository;
    private StudentRepository studentRepository;
    private ClerkshipRepository clerkshipRepository;
    private List<Object> studentList;

    public ExportController(DoctorRepository doctorRepository, StudentRepository studentRepository, ClerkshipRepository clerkshipRepository){
        this.doctorRepository = doctorRepository;
        this.studentRepository = studentRepository;
        this.clerkshipRepository = clerkshipRepository;

    }

    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public String home(){
        return "export";
    }


    public void downloadDoctorCSV(HttpServletResponse response1) throws IOException {

        String csvFileName = "doctorsSchedule.csv";

        response1.setContentType("text/csv"); //set content type of the response so that jQuery knows what it can expect


        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
        response1.setHeader(headerKey, headerValue);


        ArrayList<Doctor> listDoctors = new ArrayList<>();

        for(Doctor doc: doctorRepository.findAll()) {
            listDoctors.add(doc);
        }


        // uses the Super CSV API to generate CSV data from the model data
        try(ICsvBeanWriter csvWriter = new CsvBeanWriter(response1.getWriter(), CsvPreference.STANDARD_PREFERENCE);){ //try-with-resource management (writer closes automatically)

            String[] header = {"ID", "Clerkship", "Name", "Email", "Profession", "Available"}; //must match field names in model (model must have getters for field)

            csvWriter.writeHeader(header);

            for (Doctor aDoc : listDoctors) {
                csvWriter.write(aDoc, header);
            }
        }

    }

    @RequestMapping(value = "/downloadStudentScheduleCSV")
    public void downloadStudentCSV(HttpServletResponse response2) throws IOException {

        String csvFileName = "studentsSchedule.csv";

        response2.setContentType("text/csv");


        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"",
                csvFileName);
        response2.setHeader(headerKey, headerValue);


        ArrayList<Clerkship> clerkList = new ArrayList<>();

        for(Student stu: studentRepository.findAll()) {

            Map<String, Clerkship> clerkships = stu.getClerkships(); //students list of clerkships
            for(String key: clerkships.keySet()){
                clerkList.add(clerkships.get(key)); //add stu's clerkships to list of all clerkships
            }
        }


        // uses the Super CSV API to generate CSV data from the model data
        try(ICsvBeanWriter csvWriter = new CsvBeanWriter(response2.getWriter(), CsvPreference.STANDARD_PREFERENCE);){ //try-with-resource management (writer closes automatically)

            String[] header = {"Student Name", "Title", "Time", "Date", "Start Time", "End Time", "Location", "Description"};
            String[] fieldHead = {"studentName", "Title", "Time", "date", "startTime", "endTime",  "Location", "Description"}; //must match fields in Clerkship model
            final CellProcessor[] processors = getProcessors();

            csvWriter.writeHeader(header);

            for (Clerkship clerk : clerkList) {
                csvWriter.write(clerk, fieldHead, processors); //write all clerkships to csv file
            }
        }

    }

    @RequestMapping(value = "/downloadExcel")
    public void downloadExcel(HttpServletResponse response2) throws IOException{

        int yr = Calendar.getInstance().get(Calendar.YEAR);
        String year = Integer.toString(yr);

        String excelFileName = "StudentSchedules-" + year + ".xlsx"; //file name

        response2.setHeader("Content-Disposition", "attachment; filename="+excelFileName); //set content type of the response so that jQuery knows what it can expect
        //response2.setHeader("charset", "iso-8859-1");
        response2.setContentType("application/vnd.ms-excel");

        String[] columns = {"Time/Period","Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"}; //weekly calendar header

        Workbook workbook = new XSSFWorkbook(); //New excel workbook


        //Header Cell style formatting
        XSSFFont headerFont = (XSSFFont) workbook.createFont();
        headerFont.setBold(true);
        headerFont.setUnderline(XSSFFont.U_DOUBLE);
        headerFont.setFontHeightInPoints((short)30);
        headerFont.setColor(new XSSFColor(new java.awt.Color(77,25,121)));
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        //Header Cell style formatting
        XSSFFont headerFont1 = (XSSFFont) workbook.createFont();
        headerFont1.setBold(true);
        headerFont1.setFontHeightInPoints((short)25);
        headerFont1.setColor(new XSSFColor(new java.awt.Color(77,25,121)));
        CellStyle headerCellStyle1 = workbook.createCellStyle();
        headerCellStyle1.setFont(headerFont1);

        //2nd Header Cell Style formatting
        XSSFFont headerFont2 = (XSSFFont) workbook.createFont();
        headerFont2.setBold(true);
        headerFont2.setFontHeightInPoints((short)17);
        XSSFCellStyle headerCellStyle2 = (XSSFCellStyle) workbook.createCellStyle();
        headerCellStyle2.setFont(headerFont2);
       // headerFont2.setColor(new XSSFColor(new java.awt.Color(47,86,41)));
        headerCellStyle2.setFillForegroundColor(new XSSFColor(new java.awt.Color(47,86,41)));
        headerCellStyle2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerCellStyle2.setBorderBottom(BorderStyle.MEDIUM);
        headerCellStyle2.setBorderTop(BorderStyle.MEDIUM);
        headerCellStyle2.setBorderRight(BorderStyle.MEDIUM);
        headerCellStyle2.setBorderLeft(BorderStyle.MEDIUM);

        //Style for "Morning" and "Afternoon" header cells
        XSSFFont font = (XSSFFont) workbook.createFont();
        font.setBold(false);
        font.setItalic(true);
        font.setFontHeightInPoints((short)17);
        //font.setColor(new XSSFColor(new java.awt.Color(72,130,63)));
        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
        style.setFont(font);
        style.setFillForegroundColor(new XSSFColor(new java.awt.Color(72,130,63)));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);


        //Style for data cells
        XSSFFont dataFont = (XSSFFont) workbook.createFont();
        dataFont.setFontHeightInPoints((short)12);
        XSSFCellStyle dataStyle = (XSSFCellStyle) workbook.createCellStyle();
        dataStyle.setFont(dataFont);
        dataStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(193,224,184)));
        dataStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setWrapText(true);

        //Style for WEEK header cell
        XSSFFont weekHeaderFont = (XSSFFont) workbook.createFont();
        weekHeaderFont.setFontHeightInPoints((short)20);
        weekHeaderFont.setBold(true);
        CellStyle weekHeaderStyle = workbook.createCellStyle();
        weekHeaderStyle.setFont(weekHeaderFont);


        //Style for Stu Info Cell
        XSSFFont stuInfoFont = (XSSFFont) workbook.createFont();
        stuInfoFont.setFontHeightInPoints((short)15);
        stuInfoFont.setBold(true);
        CellStyle stuInfoStyle = workbook.createCellStyle();
        stuInfoStyle.setFont(stuInfoFont);
        stuInfoStyle.setWrapText(true);

        //CreationHelper creationHelper = workbook.getCreationHelper();

        //Creates a new sheet in the workbook for every student in repository
        for(Student stu: studentRepository.findAll()){

            Map<String, Clerkship> clerkships = stu.getClerkships(); //map of the student's clerkships

            String stuName = stu.getName() + "'s Schedule-"+year; //sheet name
            Sheet sheet = workbook.createSheet(stuName); //create new sheet
            sheet.setDefaultColumnWidth(20); //set default column width

            sheet.addMergedRegion(new CellRangeAddress(0,0,3,6));
            String head = "TCU/UNT Medical School Scheduling";
            Cell cellMerge = CellUtil.createCell(CellUtil.getRow(0,sheet),3,head,headerCellStyle);
            CellUtil.setAlignment(cellMerge,HorizontalAlignment.CENTER);

            //Creating Header cell
            Row headerRow = sheet.createRow(1);
            Cell headerCell = headerRow.createCell(0);
            headerCell.setCellValue("Weekly Schedule");
            headerCell.setCellStyle(headerCellStyle1);
            int indexCell = headerCell.getColumnIndex();
            sheet.setColumnWidth(indexCell,10000);


            //Creating cell for student info
            Row stuNameRow = sheet.createRow(2);
            Cell stuNameCell = stuNameRow.createCell(0);
            stuNameCell.setCellValue("Student: " + stu.getName() + "\n" + "Email: " + stu.getEmail());
            stuNameCell.setCellStyle(stuInfoStyle);



            //creating week day header cells
            Row row3 = sheet.createRow(3);
            for(int i = 0; i<columns.length;i++){
                    Cell cell = row3.createCell(i+1);
                    cell.setCellValue(columns[i]);
                    cell.setCellStyle(headerCellStyle2);
            }

            //creating cells for week days (where clerkships will go)
            for(int i = 4; i < 6; i++){
                Row row = sheet.createRow(i);
                //row.setHeightInPoints((2*sheet.getDefaultRowHeightInPoints()));
                for(int t = 2; t<9; t++){
                    Cell cell = row.createCell(t);
                    cell.setCellStyle(dataStyle);
                }
            }



            Row row2 = sheet.getRow(2);
            Cell weekCell = row2.createCell(1);
            weekCell.setCellValue("WEEK 1:");
            weekCell.setCellStyle(weekHeaderStyle);

            //Morning and Afternoon header cells
            Row row4 = sheet.getRow(4);
            Cell mornCell = row4.createCell(1);
            mornCell.setCellValue("Morning:");
            mornCell.setCellStyle(style);


            Row row5 = sheet.getRow(5);
            Cell afternoonCell = row5.createCell(1);
            afternoonCell.setCellStyle(headerCellStyle2);
            afternoonCell.setCellValue("Afternoon:");
            afternoonCell.setCellStyle(style);


            //Creating references for all cells where clerkships will need to go
            CellReference MonAM = new CellReference("C5");
            CellReference MonPM = new CellReference("C6");
            CellReference TuesAM = new CellReference("D5");
            CellReference TuesPM = new CellReference("D6");
            CellReference WedAM = new CellReference("E5");
            CellReference WedPM = new CellReference("E6");
            CellReference ThursAM = new CellReference("F5");
            CellReference ThursPM = new CellReference("F6");
            CellReference FriAM = new CellReference("G5");
            CellReference FriPM = new CellReference("G6");
            CellReference SatAM = new CellReference("H5");
            CellReference SatPM = new CellReference("H6");
            CellReference SunAM = new CellReference("I5");
            CellReference SunPM = new CellReference("I6");

            //Iterates through all clerkships a student has and place info in correct cell 
            for(String key: clerkships.keySet()){
                Clerkship clerk = clerkships.get(key);
                switch(clerk.getTime()){
                    case "MonAM":
                        Row r1 = sheet.getRow(MonAM.getRow());
                        Cell c1 = r1.getCell(MonAM.getCol());
                        c1.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                        c1.setCellStyle(dataStyle);
                        break;
                    case "MonPM":
                        Row r2 = sheet.getRow(MonPM.getRow());
                        Cell c2 = r2.getCell(MonPM.getCol());
                        c2.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                        c2.setCellStyle(dataStyle);
                        break;
                    case "TuesAM":
                        Row r3 = sheet.getRow(TuesAM.getRow());
                        Cell c3 = r3.getCell(TuesAM.getCol());
                        c3.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                        c3.setCellStyle(dataStyle);
                        break;
                    case "TuesPM":
                        Row r4 = sheet.getRow(TuesPM.getRow());
                        Cell c4 = r4.getCell(TuesPM.getCol());
                        c4.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                        c4.setCellStyle(dataStyle);
                        break;
                    case "WedAM":
                        Row r5 = sheet.getRow(WedAM.getRow());
                        Cell c5 = r5.getCell(WedAM.getCol());
                        c5.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                        c5.setCellStyle(dataStyle);
                        break;
                    case "WedPM":
                        Row r6 = sheet.getRow(WedPM.getRow());
                        Cell c6 = r6.getCell(WedPM.getCol());
                        c6.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                        c6.setCellStyle(dataStyle);
                        break;
                    case "ThursAM":
                        Row r7 = sheet.getRow(ThursAM.getRow());
                        Cell c7 = r7.getCell(ThursAM.getCol());
                        c7.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                        c7.setCellStyle(dataStyle);
                        break;
                    case "ThursPM":
                        Row r8 = sheet.getRow(ThursPM.getRow());
                        Cell c8 = r8.getCell(ThursPM.getCol());
                        c8.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                        c8.setCellStyle(dataStyle);
                        break;
                    case "FriAM":
                        Row r9 = sheet.getRow(FriAM.getRow());
                        Cell c9 = r9.getCell(FriAM.getCol());
                        c9.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                        c9.setCellStyle(dataStyle);
                        break;
                    case "FriPM":
                        Row r10 = sheet.getRow(FriPM.getRow());
                        Cell c10 = r10.getCell(FriPM.getCol());
                        c10.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                        c10.setCellStyle(dataStyle);
                        break;
                    case "SatAM":
                        Row r11 = sheet.getRow(SatAM.getRow());
                        Cell c11 = r11.getCell(SatAM.getCol());
                        c11.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                        c11.setCellStyle(dataStyle);
                        break;
                    case "SatPM":
                        Row r12 = sheet.getRow(SatPM.getRow());
                        Cell c12 = r12.getCell(SatPM.getCol());
                        c12.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                        c12.setCellStyle(dataStyle);
                        break;
                    case "SunAM":
                        Row r13 = sheet.getRow(SunAM.getRow());
                        Cell c13 = r13.getCell(SunAM.getCol());
                        c13.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                        c13.setCellStyle(dataStyle);
                        break;
                    case "SunPM":
                        Row r14 = sheet.getRow(SunPM.getRow());
                        Cell c14 = r14.getCell(SunPM.getCol());
                        c14.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                        c14.setCellStyle(dataStyle);
                        break;
                }

            }

       //autoSizeColumns(workbook);

        }

        OutputStream outputStream = null;
        try {
            outputStream = response2.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
            response2.flushBuffer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        workbook.close();

    }

    public void autoSizeColumns(Workbook workbook) {
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            if (sheet.getPhysicalNumberOfRows() > 0) {
                Row row = sheet.getRow(sheet.getFirstRowNum());
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    int columnIndex = cell.getColumnIndex();
                    sheet.autoSizeColumn(columnIndex);
                }
            }
        }
    }


    public static CellProcessor[] getProcessors() {

        final CellProcessor[] PROCESSORS = new CellProcessor[]{
                new NotNull(), //Student Name
                new NotNull(), //Clerkship Title
                new NotNull(), //"Time" (day) remove
                new Optional(new FmtDate("MM/dd/yyyy")), //Date
                new Optional(), //Start Time (need to format)
                new Optional(), //End Time (need to format)
                new Optional(), //location
                new Optional(), //Description


        };
        return PROCESSORS;
    }



}