package LIC.UC04v1.controllers;


import LIC.UC04v1.model.Clerkship;
import LIC.UC04v1.model.Doctor;
import LIC.UC04v1.model.Student;
import LIC.UC04v1.repositories.ClerkshipRepository;
import LIC.UC04v1.repositories.DoctorRepository;
import LIC.UC04v1.repositories.StudentRepository;
import com.opencsv.CSVWriter;
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
import java.io.IOException;
import java.io.OutputStream;
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

@RequestMapping("/admin")
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


    @RequestMapping(value = "/downloadDoctorScheduleCSV")
    public void downloadDoctorCSV(HttpServletResponse response1) throws IOException {

        String csvFileName = "doctorsSchedule.csv";
        response1.setContentType("text/csv"); //set content type of the response so that jQuery knows what it can expect


        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
        response1.setHeader(headerKey, headerValue);


        ArrayList<Doctor> listDoctors = new ArrayList<>();

        for(Doctor doc: doctorRepository.findAll()) { listDoctors.add(doc); } //list of doctors


        try{
            CSVWriter writer = new CSVWriter (response1.getWriter());

            String[] header = {"Doctor Name", "Title", "Student", "Description", "Day", "Week", "Start Time", "End Time", "Location", "Event Type"};
            writer.writeNext(header);

            for(Doctor doc: listDoctors) {
                List<Clerkship> clerkships = doc.getClerkship();
                for (Clerkship clerk : clerkships) {
                    String docName = doc.getName();
                    String title = clerk.getTitle();
                    String stuName = clerk.getStudent().getName();
                    String description = "";
                    String day = clerk.getTime();
                    String week = "";
                    String week2 = "week 2";
                    String startT = clerk.getStartTime();
                    String endT = clerk.getEndTime();
                    String loc = "";
                    String event = "Clinic";
                    if (clerk.getDay() < 12) {
                        week = "week 1";
                    } else {
                        week = "week 2";
                    }

                    String[] data = {docName, title, stuName, description, day, week, startT, endT, loc, event};
                    writer.writeNext(data);

                    if (title.equals("Surgery") || title.equals("Pediatrics") || title.equals("Family Medicine") || title.equals("Internal Medicine")) {
                        String[] data1 = {docName, title, stuName, description, day, week2, startT, endT, loc, event};
                        writer.writeNext(data1);
                    }
                }
            }
            writer.close();
        }catch (IOException e){
                e.printStackTrace();
            }
    }

    @RequestMapping(value = "/downloadStudentScheduleCSV")
    public void downloadStudentCSV(HttpServletResponse response2) throws IOException {

        String csvFileName = "studentsSchedule.csv";
        response2.setContentType("text/csv");


        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
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

            String[] header = {"Student Name", "Title", "Doctor", "Description", "Day", "Week", "Start Time", "End Time", "Location", "Event Type"}; //csv headers

            //timeWeek1/2 is not correct. (It will always output week 1 unless it is one of the four reoccuring clerkships which is not correct)
            String[] fieldHead = {"studentName", "Title", "doctorName", "Description", "Time", "timeWeek1", "startTime", "endTime",  "Location", "eventType"}; //getters in clerkship model
            String[] fieldHead2 = {"studentName", "Title", "doctorName", "Description", "Time", "timeWeek2", "startTime", "endTime",  "Location", "eventType"};
            final CellProcessor[] processors = getProcessors();

            csvWriter.writeHeader(header);

            for (Clerkship clerk : clerkList) {
                String s = clerk.getTitle();
                //clerkships that occur both weeks
                if(s.equals("Surgery") || s.equals("Pediatrics") || s.equals("Family Medicine") || s.equals("Internal Medicine")){
                    csvWriter.write(clerk, fieldHead, processors); //write all clerkships to csv file
                    csvWriter.write(clerk,fieldHead2,processors);
                }else{
                    csvWriter.write(clerk, fieldHead, processors);
                }

            }
        }

    }

    @RequestMapping(value = "/downloadExcel")
    public void downloadExcel(HttpServletResponse response2) throws IOException{

        int yr = Calendar.getInstance().get(Calendar.YEAR);
        String year = Integer.toString(yr);
        String excelFileName = "StudentSchedules-" + year + ".xlsx"; //file name

        response2.setHeader("Content-Disposition", "attachment; filename="+excelFileName); //set content type of the response so that jQuery knows what to expect
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
        headerFont1.setFontHeightInPoints((short)15);
        headerFont1.setColor(new XSSFColor(new java.awt.Color(77,25,121)));
        CellStyle headerCellStyle1 = workbook.createCellStyle();
        headerCellStyle1.setFont(headerFont1);

        //2nd Header Cell Style formatting
        XSSFFont headerFont2 = (XSSFFont) workbook.createFont();
        headerFont2.setBold(true);
        headerFont2.setFontHeightInPoints((short)17);
        XSSFCellStyle headerCellStyle2 = (XSSFCellStyle) workbook.createCellStyle();
        headerCellStyle2.setFont(headerFont2);
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

        //LEAPS cell style
        XSSFCellStyle leapsStyle = (XSSFCellStyle) workbook.createCellStyle();
        leapsStyle.setFont(font);
        leapsStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(136, 132, 132)));
        leapsStyle.setAlignment(CellStyle.ALIGN_CENTER);
        leapsStyle.setVerticalAlignment(CellStyle.VERTICAL_JUSTIFY);
        leapsStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        leapsStyle.setBorderBottom(BorderStyle.THIN);
        leapsStyle.setBorderTop(BorderStyle.THIN);
        leapsStyle.setBorderRight(BorderStyle.THIN);
        leapsStyle.setBorderLeft(BorderStyle.THIN);
        leapsStyle.setWrapText(true);

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


        //Creates a new sheet in the workbook for every student in repository
        for(Student stu: studentRepository.findAll()){

            Map<String, Clerkship> clerkships = stu.getClerkships(); //map of the student's clerkships

            String stuName = stu.getName() + "'s Schedule-"+year; //sheet name
            Sheet sheet = workbook.createSheet(stuName); //create new sheet
            sheet.setDefaultColumnWidth(20); //set default column width

            sheet.addMergedRegion(new CellRangeAddress(0,0,3,6));
            String head = "Weekly Schedule";
            Cell cellMerge = CellUtil.createCell(CellUtil.getRow(0,sheet),3,head,headerCellStyle);
            CellUtil.setAlignment(cellMerge,HorizontalAlignment.CENTER);

            //Creating Header cell
            Row headerRow = sheet.createRow(1);
            Cell headerCell = headerRow.createCell(0);
            headerCell.setCellValue("TCU/UNT Medical School Scheduling");
            headerCell.setCellStyle(headerCellStyle1);
            int indexCell = headerCell.getColumnIndex();
            sheet.setColumnWidth(indexCell,10000);

            //Creating cell for student info
            Row row2 = sheet.createRow(2);
            Cell stuNameCell = row2.createCell(0);
            stuNameCell.setCellValue("Student: " + stu.getName() + "\n" + "Email: " + stu.getEmail());
            stuNameCell.setCellStyle(stuInfoStyle);

            //creating weekday header cells (w1)
            Row row3 = sheet.createRow(3);
            for(int i = 0; i<columns.length;i++){
                    Cell cell = row3.createCell(i+1);
                    cell.setCellValue(columns[i]);
                    cell.setCellStyle(headerCellStyle2);
            }

            //creating weekday header cells (w2)
            Row row10 = sheet.createRow(10);
            for(int i = 0; i<columns.length;i++){
                Cell cell = row10.createCell(i+1);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerCellStyle2);
            }

            //create cells for week 1 days (where clerkships will go)
            for(int i = 4; i < 6; i++){
                Row row = sheet.createRow(i);
                for(int t = 2; t<9; t++){
                    Cell cell = row.createCell(t);
                    cell.setCellStyle(dataStyle);
                }
            }

            //create cells for week 2 days (where clerkships will go)
            for(int i = 11; i < 13; i++){
                Row row = sheet.createRow(i);
                for(int t = 2; t<9; t++){
                    Cell cell = row.createCell(t);
                    cell.setCellStyle(dataStyle);
                }
            }


            //Week 1 Header
            Cell week1Cell = row2.createCell(1);
            week1Cell.setCellValue("WEEK 1:");
            week1Cell.setCellStyle(weekHeaderStyle);

            //Week 2 Header
            Row row9 = sheet.createRow(9);
            Cell week2Cell = row9.createCell(1);
            week2Cell.setCellValue("WEEK 2:");
            week2Cell.setCellStyle(weekHeaderStyle);

            //Morning and Afternoon header cells
            Row row4 = sheet.getRow(4);
            Cell mornCell1 = row4.createCell(1);
            mornCell1.setCellValue("Morning:");
            mornCell1.setCellStyle(style);

            Row row11 = sheet.getRow(11);
            Cell mornCell2 = row11.createCell(1);
            mornCell2.setCellValue("Morning:");
            mornCell2.setCellStyle(style);

            Row row5 = sheet.getRow(5);
            Cell afternoonCell1 = row5.createCell(1);
            afternoonCell1.setCellStyle(headerCellStyle2);
            afternoonCell1.setCellValue("Afternoon:");
            afternoonCell1.setCellStyle(style);

            Row row12 = sheet.getRow(12);
            Cell afternoonCell2 = row12.createCell(1);
            afternoonCell2.setCellStyle(headerCellStyle2);
            afternoonCell2.setCellValue("Afternoon:");
            afternoonCell2.setCellStyle(style);



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
            //CellReference SunAM = new CellReference("I5");
            //CellReference SunPM = new CellReference("I6");

            CellReference MonAM2 = new CellReference("C12");
            CellReference MonPM2 = new CellReference("C13");
            CellReference TuesAM2 = new CellReference("D12");
            CellReference TuesPM2 = new CellReference("D13");
            CellReference WedAM2 = new CellReference("E12");
            CellReference WedPM2 = new CellReference("E13");
            CellReference ThursAM2 = new CellReference("F12");
            CellReference ThursPM2 = new CellReference("F13");
            CellReference FriAM2 = new CellReference("G12");
            CellReference FriPM2 = new CellReference("G13");
            CellReference SatAM2 = new CellReference("H12");
            CellReference SatPM2 = new CellReference("H13");

            Row r8 = sheet.getRow(ThursPM.getRow());
            Cell c8 = r8.getCell(ThursPM.getCol());
            c8.setCellValue("LEAPS");
            c8.setCellStyle(leapsStyle);

            Row r20 = sheet.getRow(ThursPM2.getRow());
            Cell c20 = r20.getCell(ThursPM2.getCol());
            c20.setCellValue("LEAPS");
            c20.setCellStyle(leapsStyle);

            //Iterates through all clerkships a student has and place info in correct cell 
            for(String key: clerkships.keySet()){
                Clerkship clerk = clerkships.get(key);
                String s = clerk.getSpecialty().name();
                Specialty specialty = clerk.getSpecialty();
                switch(clerk.getDay()) {
                    case 0:
                        writeToCell(sheet, MonAM,dataStyle,clerk);
                        break;
                    case 1:
                        writeToCell(sheet, MonPM,dataStyle,clerk);
                        break;
                    case 2:
                        writeToCell(sheet, TuesAM,dataStyle,clerk);
                        break;
                    case 3:
                        writeToCell(sheet, TuesPM,dataStyle,clerk);
                        break;
                    case 4:
                        writeToCell(sheet, WedAM,dataStyle,clerk);
                        break;
                    case 5:
                        writeToCell(sheet, WedPM,dataStyle,clerk);
                        break;
                    case 6:
                        writeToCell(sheet, ThursAM,dataStyle,clerk);
                        break;
                    /*case 7:
                        writeToCell(sheet, ThursPM,dataStyle,clerk);
                        break;*/
                    case 8:
                        writeToCell(sheet, FriAM,dataStyle,clerk);
                        break;
                    case 9:
                        writeToCell(sheet, FriPM,dataStyle,clerk);
                        break;
                    case 10:
                        writeToCell(sheet, SatAM,dataStyle,clerk);
                        break;
                    case 11:
                        writeToCell(sheet, SatPM,dataStyle,clerk);
                        break;
                    case 12:
                        writeToCell(sheet, MonAM2,dataStyle,clerk);
                        break;
                    case 13:
                        writeToCell(sheet, MonPM2,dataStyle,clerk);
                        break;
                    case 14:
                        writeToCell(sheet, TuesPM2,dataStyle,clerk);
                        break;
                    case 15:
                        writeToCell(sheet, TuesPM2,dataStyle,clerk);
                        break;
                    case 16:
                        writeToCell(sheet, WedAM2,dataStyle,clerk);
                        break;
                    case 17:
                        writeToCell(sheet, WedPM2,dataStyle,clerk);
                        break;
                    case 18:
                        writeToCell(sheet, ThursAM2,dataStyle,clerk);
                        break;
                    case 20:
                        writeToCell(sheet, FriAM2,dataStyle,clerk);
                        break;
                    case 21:
                        writeToCell(sheet, FriPM2,dataStyle,clerk);
                        break;
                    case 22:
                        writeToCell(sheet, SatAM2,dataStyle,clerk);
                        break;
                    case 23:
                        writeToCell(sheet, SatPM2,dataStyle,clerk);
                        break;
                        default:
                            System.out.println("Did not write to cell");
                            System.out.println(clerk.getDay());
                            break;
                }
                if(specialty==Specialty.FamilyMedicine||specialty==Specialty.Pediatrics||specialty==Specialty.Surgery||specialty==Specialty.InternalMedicine){
                    int week2 = clerk.getDay() + 12;
                    switch(week2){
                        case 12:
                            writeToCell(sheet, MonAM2,dataStyle,clerk);
                            break;
                        case 13:
                            writeToCell(sheet, MonPM2,dataStyle,clerk);
                            break;
                        case 14:
                            writeToCell(sheet, TuesAM2,dataStyle,clerk);
                            break;
                        case 15:
                            writeToCell(sheet, TuesPM2,dataStyle,clerk);
                            break;
                        case 16:
                            writeToCell(sheet, WedAM2,dataStyle,clerk);
                            break;
                        case 17:
                            writeToCell(sheet, WedPM2,dataStyle,clerk);
                            break;
                        case 18:
                            writeToCell(sheet, ThursAM2,dataStyle,clerk);
                            break;
                        case 20:
                            writeToCell(sheet, FriAM2,dataStyle,clerk);
                            break;
                        case 21:
                            writeToCell(sheet, FriPM2,dataStyle,clerk);
                            break;
                        case 22:
                            writeToCell(sheet, SatAM2,dataStyle,clerk);
                            break;
                        case 23:
                            writeToCell(sheet, SatPM2,dataStyle,clerk);
                            break;
                        default:
                            break;
                }
                }
            }
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

    public void writeToCell(Sheet sheet, CellReference weekDay, XSSFCellStyle dataStyle, Clerkship clerk){

        Row row = sheet.getRow(weekDay.getRow());
        Cell cell = row.getCell(weekDay.getCol());
        cell.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation() + "\nPhysician: " + clerk.getDoctor().getName());
        cell.setCellStyle(dataStyle);

    }


    public static CellProcessor[] getProcessors(){

        final CellProcessor[] PROCESSORS = new CellProcessor[]{
                new NotNull(), //Student Name
                new Optional(), //Clerkship Title
                new Optional(), //Doctor Name
                new Optional(), //Description
                new NotNull(), //"Time" (day) remove
                new Optional(), //Date
                new Optional(), //Start Time (need to format)
                new Optional(), //End Time (need to format)
                new Optional(), //location
                new Optional(), //Event Type
        };
        return PROCESSORS;
    }

    @RequestMapping(value = "/downloadDoctorExcel")
    public void downloadDoctorExcel(HttpServletResponse response1) throws IOException {

        int yr = Calendar.getInstance().get(Calendar.YEAR);
        String year = Integer.toString(yr);
        String excelFileName = "DoctorSchedules-" + year + ".xlsx"; //file name

        response1.setHeader("Content-Disposition", "attachment; filename=" + excelFileName); //set content type of the response so that jQuery knows what to expect
        response1.setContentType("application/vnd.ms-excel");

        String[] columns = {"Time/Period", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"}; //weekly calendar header

        Workbook workbook = new XSSFWorkbook(); //New excel workbook

        XSSFFont headerFont = (XSSFFont) workbook.createFont();
        CellStyle headerCellStyle = workbook.createCellStyle();

        XSSFFont headerFont1 = (XSSFFont) workbook.createFont();
        CellStyle headerCellStyle1 = workbook.createCellStyle();

        XSSFFont headerFont2 = (XSSFFont) workbook.createFont();
        XSSFCellStyle headerCellStyle2 = (XSSFCellStyle) workbook.createCellStyle();

        XSSFFont font = (XSSFFont) workbook.createFont();
        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();

        XSSFFont dataFont = (XSSFFont) workbook.createFont();
        XSSFCellStyle dataStyle = (XSSFCellStyle) workbook.createCellStyle();

        XSSFCellStyle leapsStyle = (XSSFCellStyle) workbook.createCellStyle();

        XSSFFont weekHeaderFont = (XSSFFont) workbook.createFont();
        CellStyle weekHeaderStyle = workbook.createCellStyle();

        XSSFFont stuInfoFont = (XSSFFont) workbook.createFont();
        CellStyle stuInfoStyle = workbook.createCellStyle();

        creatStyle(workbook,headerFont,headerCellStyle,headerFont1,headerCellStyle1,headerFont2,headerCellStyle2,
                font,style,dataFont,dataStyle,leapsStyle,weekHeaderFont,weekHeaderStyle,stuInfoFont,stuInfoStyle);


        //Creates a new sheet in the workbook for every student in repository
        for(Doctor doc: doctorRepository.findAll()) {

            if (doc.getHasStu() == 1) { //only the doctors that have students

                List<Clerkship> clerkships = doc.getClerkship(); //map of the student's clerkships

                String docName = doc.getName() + "'s Schedule-" + year; //sheet name
                Sheet sheet = workbook.createSheet(docName); //create new sheet
                sheet.setDefaultColumnWidth(20); //set default column width

                sheet.addMergedRegion(new CellRangeAddress(0, 0, 3, 6));
                String head = "Weekly Schedule";
                Cell cellMerge = CellUtil.createCell(CellUtil.getRow(0, sheet), 3, head, headerCellStyle);
                CellUtil.setAlignment(cellMerge, HorizontalAlignment.CENTER);

                //Creating Header cell
                Row headerRow = sheet.createRow(1);
                Cell headerCell = headerRow.createCell(0);
                headerCell.setCellValue("TCU/UNT Medical School Scheduling");
                headerCell.setCellStyle(headerCellStyle1);
                int indexCell = headerCell.getColumnIndex();
                sheet.setColumnWidth(indexCell, 10000);

                //Creating cell for student info
                Row row2 = sheet.createRow(2);
                Cell stuNameCell = row2.createCell(0);
                stuNameCell.setCellValue("Student: " + doc.getName() + "\n" + "Email: " + doc.getEmail());
                stuNameCell.setCellStyle(stuInfoStyle);

                //creating weekday header cells (w1)
                Row row3 = sheet.createRow(3);
                for (int i = 0; i < columns.length; i++) {
                    Cell cell = row3.createCell(i + 1);
                    cell.setCellValue(columns[i]);
                    cell.setCellStyle(headerCellStyle2);
                }

                //creating weekday header cells (w2)
                Row row10 = sheet.createRow(10);
                for (int i = 0; i < columns.length; i++) {
                    Cell cell = row10.createCell(i + 1);
                    cell.setCellValue(columns[i]);
                    cell.setCellStyle(headerCellStyle2);
                }

                //create cells for week 1 days (where clerkships will go)
                for (int i = 4; i < 6; i++) {
                    Row row = sheet.createRow(i);
                    for (int t = 2; t < 9; t++) {
                        Cell cell = row.createCell(t);
                        cell.setCellStyle(dataStyle);
                    }
                }

                //create cells for week 2 days (where clerkships will go)
                for (int i = 11; i < 13; i++) {
                    Row row = sheet.createRow(i);
                    for (int t = 2; t < 9; t++) {
                        Cell cell = row.createCell(t);
                        cell.setCellStyle(dataStyle);
                    }
                }


                //Week 1 Header
                Cell week1Cell = row2.createCell(1);
                week1Cell.setCellValue("WEEK 1:");
                week1Cell.setCellStyle(weekHeaderStyle);

                //Week 2 Header
                Row row9 = sheet.createRow(9);
                Cell week2Cell = row9.createCell(1);
                week2Cell.setCellValue("WEEK 2:");
                week2Cell.setCellStyle(weekHeaderStyle);

                //Morning and Afternoon header cells
                Row row4 = sheet.getRow(4);
                Cell mornCell1 = row4.createCell(1);
                mornCell1.setCellValue("Morning:");
                mornCell1.setCellStyle(style);

                Row row11 = sheet.getRow(11);
                Cell mornCell2 = row11.createCell(1);
                mornCell2.setCellValue("Morning:");
                mornCell2.setCellStyle(style);

                Row row5 = sheet.getRow(5);
                Cell afternoonCell1 = row5.createCell(1);
                afternoonCell1.setCellStyle(headerCellStyle2);
                afternoonCell1.setCellValue("Afternoon:");
                afternoonCell1.setCellStyle(style);

                Row row12 = sheet.getRow(12);
                Cell afternoonCell2 = row12.createCell(1);
                afternoonCell2.setCellStyle(headerCellStyle2);
                afternoonCell2.setCellValue("Afternoon:");
                afternoonCell2.setCellStyle(style);


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
                //CellReference SunAM = new CellReference("I5");
                //CellReference SunPM = new CellReference("I6");

                CellReference MonAM2 = new CellReference("C12");
                CellReference MonPM2 = new CellReference("C13");
                CellReference TuesAM2 = new CellReference("D12");
                CellReference TuesPM2 = new CellReference("D13");
                CellReference WedAM2 = new CellReference("E12");
                CellReference WedPM2 = new CellReference("E13");
                CellReference ThursAM2 = new CellReference("F12");
                CellReference ThursPM2 = new CellReference("F13");
                CellReference FriAM2 = new CellReference("G12");
                CellReference FriPM2 = new CellReference("G13");
                CellReference SatAM2 = new CellReference("H12");
                CellReference SatPM2 = new CellReference("H13");

                Row r8 = sheet.getRow(ThursPM.getRow());
                Cell c8 = r8.getCell(ThursPM.getCol());
                c8.setCellValue("LEAPS");
                c8.setCellStyle(leapsStyle);

                Row r20 = sheet.getRow(ThursPM2.getRow());
                Cell c20 = r20.getCell(ThursPM2.getCol());
                c20.setCellValue("LEAPS");
                c20.setCellStyle(leapsStyle);

                //Iterates through all clerkships a student has and place info in correct cell

                for (Clerkship clerk : clerkships) {

                    String s = clerk.getSpecialty().name();
                    Specialty specialty = clerk.getSpecialty();
                    switch (clerk.getDay()) {
                        case 0:
                            writeToCell(sheet, MonAM, dataStyle, clerk);
                            break;
                        case 1:
                            writeToCell(sheet, MonPM, dataStyle, clerk);
                            break;
                        case 2:
                            writeToCell(sheet, TuesAM, dataStyle, clerk);
                            break;
                        case 3:
                            writeToCell(sheet, TuesPM, dataStyle, clerk);
                            break;
                        case 4:
                            writeToCell(sheet, WedAM, dataStyle, clerk);
                            break;
                        case 5:
                            writeToCell(sheet, WedPM, dataStyle, clerk);
                            break;
                        case 6:
                            writeToCell(sheet, ThursAM, dataStyle, clerk);
                            break;
                    /*case 7:
                        writeToCell(sheet, ThursPM,dataStyle,clerk);
                        break;*/
                        case 8:
                            writeToCell(sheet, FriAM, dataStyle, clerk);
                            break;
                        case 9:
                            writeToCell(sheet, FriPM, dataStyle, clerk);
                            break;
                        case 10:
                            writeToCell(sheet, SatAM, dataStyle, clerk);
                            break;
                        case 11:
                            writeToCell(sheet, SatPM, dataStyle, clerk);
                            break;
                        case 12:
                            writeToCell(sheet, MonAM2, dataStyle, clerk);
                            break;
                        case 13:
                            writeToCell(sheet, MonPM2, dataStyle, clerk);
                            break;
                        case 14:
                            writeToCell(sheet, TuesPM2, dataStyle, clerk);
                            break;
                        case 15:
                            writeToCell(sheet, TuesPM2, dataStyle, clerk);
                            break;
                        case 16:
                            writeToCell(sheet, WedAM2, dataStyle, clerk);
                            break;
                        case 17:
                            writeToCell(sheet, WedPM2, dataStyle, clerk);
                            break;
                        case 18:
                            writeToCell(sheet, ThursAM2, dataStyle, clerk);
                            break;
                        case 20:
                            writeToCell(sheet, FriAM2, dataStyle, clerk);
                            break;
                        case 21:
                            writeToCell(sheet, FriPM2, dataStyle, clerk);
                            break;
                        case 22:
                            writeToCell(sheet, SatAM2, dataStyle, clerk);
                            break;
                        case 23:
                            writeToCell(sheet, SatPM2, dataStyle, clerk);
                            break;
                        default:
                            System.out.println("Did not write to cell");
                            System.out.println(clerk.getDay());
                            break;
                    }
                    if (specialty == Specialty.FamilyMedicine || specialty == Specialty.Pediatrics || specialty == Specialty.Surgery || specialty == Specialty.InternalMedicine) {
                        int week2 = clerk.getDay() + 12;
                        switch (week2) {
                            case 12:
                                writeToCell(sheet, MonAM2, dataStyle, clerk);
                                break;
                            case 13:
                                writeToCell(sheet, MonPM2, dataStyle, clerk);
                                break;
                            case 14:
                                writeToCell(sheet, TuesAM2, dataStyle, clerk);
                                break;
                            case 15:
                                writeToCell(sheet, TuesPM2, dataStyle, clerk);
                                break;
                            case 16:
                                writeToCell(sheet, WedAM2, dataStyle, clerk);
                                break;
                            case 17:
                                writeToCell(sheet, WedPM2, dataStyle, clerk);
                                break;
                            case 18:
                                writeToCell(sheet, ThursAM2, dataStyle, clerk);
                                break;
                            case 20:
                                writeToCell(sheet, FriAM2, dataStyle, clerk);
                                break;
                            case 21:
                                writeToCell(sheet, FriPM2, dataStyle, clerk);
                                break;
                            case 22:
                                writeToCell(sheet, SatAM2, dataStyle, clerk);
                                break;
                            case 23:
                                writeToCell(sheet, SatPM2, dataStyle, clerk);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }
        OutputStream outputStream = null;
        try {
            outputStream = response1.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
            response1.flushBuffer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        workbook.close();

    }


    public void creatStyle(Workbook workbook,XSSFFont headerFont,CellStyle headerCellStyle, XSSFFont headerFont1,
                            CellStyle headerCellStyle1,XSSFFont headerFont2,XSSFCellStyle headerCellStyle2,
                           XSSFFont font,XSSFCellStyle style,XSSFFont dataFont,XSSFCellStyle dataStyle,XSSFCellStyle leapsStyle,
                           XSSFFont weekHeaderFont,CellStyle weekHeaderStyle,XSSFFont stuInfoFont,CellStyle stuInfoStyle){

        //Header Cell style formatting
        headerFont.setBold(true);
        headerFont.setUnderline(XSSFFont.U_DOUBLE);
        headerFont.setFontHeightInPoints((short)30);
        headerFont.setColor(new XSSFColor(new java.awt.Color(77,25,121)));
        headerCellStyle.setFont(headerFont);

        //Header Cell style formatting
        headerFont1.setBold(true);
        headerFont1.setFontHeightInPoints((short)15);
        headerFont1.setColor(new XSSFColor(new java.awt.Color(77,25,121)));
        headerCellStyle1.setFont(headerFont1);

        //2nd Header Cell Style formatting
        headerFont2.setBold(true);
        headerFont2.setFontHeightInPoints((short)17);
        headerCellStyle2.setFont(headerFont2);
        headerCellStyle2.setFillForegroundColor(new XSSFColor(new java.awt.Color(47,86,41)));
        headerCellStyle2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerCellStyle2.setBorderBottom(BorderStyle.MEDIUM);
        headerCellStyle2.setBorderTop(BorderStyle.MEDIUM);
        headerCellStyle2.setBorderRight(BorderStyle.MEDIUM);
        headerCellStyle2.setBorderLeft(BorderStyle.MEDIUM);

        //Style for "Morning" and "Afternoon" header cells
        font.setBold(false);
        font.setItalic(true);
        font.setFontHeightInPoints((short)17);
        style.setFont(font);
        style.setFillForegroundColor(new XSSFColor(new java.awt.Color(72,130,63)));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);


        //Style for data cells
        dataFont.setFontHeightInPoints((short)12);
        dataStyle.setFont(dataFont);
        dataStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(193,224,184)));
        dataStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setWrapText(true);

        //LEAPS cell style
        leapsStyle.setFont(font);
        leapsStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(136, 132, 132)));
        leapsStyle.setAlignment(CellStyle.ALIGN_CENTER);
        leapsStyle.setVerticalAlignment(CellStyle.VERTICAL_JUSTIFY);
        leapsStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        leapsStyle.setBorderBottom(BorderStyle.THIN);
        leapsStyle.setBorderTop(BorderStyle.THIN);
        leapsStyle.setBorderRight(BorderStyle.THIN);
        leapsStyle.setBorderLeft(BorderStyle.THIN);
        leapsStyle.setWrapText(true);

        //Style for WEEK header cell
        weekHeaderFont.setFontHeightInPoints((short)20);
        weekHeaderFont.setBold(true);
        weekHeaderStyle.setFont(weekHeaderFont);

        //Style for Stu Info Cell
        stuInfoFont.setFontHeightInPoints((short)15);
        stuInfoFont.setBold(true);
        stuInfoStyle.setFont(stuInfoFont);
        stuInfoStyle.setWrapText(true);
    }

}