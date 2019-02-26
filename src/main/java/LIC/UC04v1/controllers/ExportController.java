package LIC.UC04v1.controllers;


import LIC.UC04v1.model.Clerkship;
import LIC.UC04v1.model.Doctor;
import LIC.UC04v1.model.Student;
import LIC.UC04v1.repositories.ClerkshipRepository;
import LIC.UC04v1.repositories.DoctorRepository;
import LIC.UC04v1.repositories.StudentRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
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

            String[] header = {"Student Name", "Title", "Description", "Day", "Week", "Start Time", "End Time", "Location", "Event Type"};
            String[] fieldHead = {"studentName", "Title", "Description", "Time", "timeInt1", "startTime", "endTime",  "Location", "eventType"}; //must match fields in Clerkship model
            String[] fieldHead2 = {"studentName", "Title","Description", "Time", "timeInt2", "startTime", "endTime",  "Location", "eventType"};
            final CellProcessor[] processors = getProcessors();

            csvWriter.writeHeader(header);

            for (Clerkship clerk : clerkList) {
                String s = clerk.getTitle();
                if(s.equals("Surgery") || s.equals("Pediatrics") || s.equals("FamilyMedicine") || s.equals("InternalMedicine")){
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
            Row stuNameRow = sheet.createRow(2);
            Cell stuNameCell = stuNameRow.createCell(0);
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

            //creating cells for week 1 days (where clerkships will go)
            for(int i = 4; i < 6; i++){
                Row row = sheet.createRow(i);
                //row.setHeightInPoints((2*sheet.getDefaultRowHeightInPoints()));
                for(int t = 2; t<9; t++){
                    Cell cell = row.createCell(t);
                    cell.setCellStyle(dataStyle);
                }
            }

            //creating cells for week 2 days (where clerkships will go)
            for(int i = 11; i < 13; i++){
                Row row = sheet.createRow(i);
                //row.setHeightInPoints((2*sheet.getDefaultRowHeightInPoints()));
                for(int t = 2; t<9; t++){
                    Cell cell = row.createCell(t);
                    cell.setCellStyle(dataStyle);
                }
            }


            //Week 1 Header
            Row row2 = sheet.getRow(2);
            Cell week1Cell = row2.createCell(1);
            week1Cell.setCellValue("WEEK 1:");
            week1Cell.setCellStyle(weekHeaderStyle);

            //Week 2 Header
            Row row9 = sheet.createRow(9);
            Cell week2Cell = row9.createCell(1);
            week2Cell.setCellValue("WEEK 2:");
            week2Cell.setCellStyle(weekHeaderStyle);

            //Morning and Afternoon header cells (week 1)
            Row row4 = sheet.getRow(4);
            Cell mornCell1 = row4.createCell(1);
            mornCell1.setCellValue("Morning:");
            mornCell1.setCellStyle(style);

            //Morning and Afternoon header cells (week 2)
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
            CellReference SunAM = new CellReference("I5");
            CellReference SunPM = new CellReference("I6");

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

            //Iterates through all clerkships a student has and place info in correct cell 
            for(String key: clerkships.keySet()){
                Clerkship clerk = clerkships.get(key);
                String s = clerk.getTitle();
                switch(clerk.getDay()) {
                    case 0:
                        Row r1 = sheet.getRow(MonAM.getRow());
                        Cell c1 = r1.getCell(MonAM.getCol());
                        c1.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation() + "\nPhysician: " + clerk.getDoctor().getName());
                        c1.setCellStyle(dataStyle);
                        break;
                    case 1:
                        Row r2 = sheet.getRow(MonPM.getRow());
                        Cell c2 = r2.getCell(MonPM.getCol());
                        c2.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                        c2.setCellStyle(dataStyle);
                        break;
                    case 2:
                        Row r3 = sheet.getRow(TuesAM.getRow());
                        Cell c3 = r3.getCell(TuesAM.getCol());
                        c3.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                        c3.setCellStyle(dataStyle);
                        break;
                    case 3:
                        Row r4 = sheet.getRow(TuesPM.getRow());
                        Cell c4 = r4.getCell(TuesPM.getCol());
                        c4.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                        c4.setCellStyle(dataStyle);
                        break;
                    case 4:
                        Row r5 = sheet.getRow(WedAM.getRow());
                        Cell c5 = r5.getCell(WedAM.getCol());
                        c5.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                        c5.setCellStyle(dataStyle);
                        break;
                    case 5:
                        Row r6 = sheet.getRow(WedPM.getRow());
                        Cell c6 = r6.getCell(WedPM.getCol());
                        c6.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                        c6.setCellStyle(dataStyle);
                        break;
                    case 6:
                        Row r7 = sheet.getRow(ThursAM.getRow());
                        Cell c7 = r7.getCell(ThursAM.getCol());
                        c7.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                        c7.setCellStyle(dataStyle);
                        break;
                    case 7:
                        Row r8 = sheet.getRow(ThursPM.getRow());
                        Cell c8 = r8.getCell(ThursPM.getCol());
                        c8.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                        c8.setCellStyle(dataStyle);
                        break;
                    case 8:
                        Row r9 = sheet.getRow(FriAM.getRow());
                        Cell c9 = r9.getCell(FriAM.getCol());
                        c9.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                        c9.setCellStyle(dataStyle);
                        break;
                    case 9:
                        Row r10 = sheet.getRow(FriPM.getRow());
                        Cell c10 = r10.getCell(FriPM.getCol());
                        c10.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                        c10.setCellStyle(dataStyle);
                        break;
                    case 10:
                        Row r11 = sheet.getRow(SatAM.getRow());
                        Cell c11 = r11.getCell(SatAM.getCol());
                        c11.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                        c11.setCellStyle(dataStyle);
                        break;
                    case 11:
                        Row r12 = sheet.getRow(SatPM.getRow());
                        Cell c12 = r12.getCell(SatPM.getCol());
                        c12.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                        c12.setCellStyle(dataStyle);
                        break;
                    case 12:
                        Row r13 = sheet.getRow(MonAM2.getRow());
                        Cell c13 = r13.getCell(MonAM2.getCol());
                        c13.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                        c13.setCellStyle(dataStyle);
                        break;
                    case 13:
                        Row r14 = sheet.getRow(MonPM2.getRow());
                        Cell c14 = r14.getCell(MonPM2.getCol());
                        c14.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                        c14.setCellStyle(dataStyle);
                        break;
                    case 14:
                        Row r15 = sheet.getRow(TuesAM2.getRow());
                        Cell c15 = r15.getCell(TuesAM2.getCol());
                        c15.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                        c15.setCellStyle(dataStyle);
                        break;
                    case 15:
                        Row r16 = sheet.getRow(TuesPM2.getRow());
                        Cell c16 = r16.getCell(TuesPM2.getCol());
                        c16.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                        c16.setCellStyle(dataStyle);
                        break;
                    case 16:
                        Row r17 = sheet.getRow(WedAM2.getRow());
                        Cell c17 = r17.getCell(WedAM2.getCol());
                        c17.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                        c17.setCellStyle(dataStyle);
                        break;
                    case 17:
                        Row r18 = sheet.getRow(WedPM2.getRow());
                        Cell c18 = r18.getCell(WedPM2.getCol());
                        c18.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                        c18.setCellStyle(dataStyle);
                        break;
                    case 18:
                        Row r19 = sheet.getRow(ThursAM2.getRow());
                        Cell c19 = r19.getCell(ThursAM2.getCol());
                        c19.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                        c19.setCellStyle(dataStyle);
                        break;
                    case 19:
                        Row r20 = sheet.getRow(ThursPM2.getRow());
                        Cell c20 = r20.getCell(ThursPM2.getCol());
                        c20.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                        c20.setCellStyle(dataStyle);
                        break;
                    case 20:
                        Row r21 = sheet.getRow(FriAM2.getRow());
                        Cell c21 = r21.getCell(FriAM2.getCol());
                        c21.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                        c21.setCellStyle(dataStyle);
                        break;
                    case 21:
                        Row r22 = sheet.getRow(FriPM2.getRow());
                        Cell c22 = r22.getCell(FriPM2.getCol());
                        c22.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                        c22.setCellStyle(dataStyle);
                        break;
                    case 22:
                        Row r23 = sheet.getRow(SatAM2.getRow());
                        Cell c23 = r23.getCell(SatAM2.getCol());
                        c23.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                        c23.setCellStyle(dataStyle);
                        break;
                    case 23:
                        Row r24 = sheet.getRow(SatPM2.getRow());
                        Cell c24 = r24.getCell(SatPM2.getCol());
                        c24.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                        c24.setCellStyle(dataStyle);
                        break;
                        default:
                            System.out.println("Did not write to cell");
                            System.out.println(clerk.getDay());
                            break;
                }
                if(s.equals("Surgery") || s.equals("Pediatrics") || s.equals("FamilyMedicine") || s.equals("InternalMedicine")){
                    int week2 = clerk.getDay() + 12;
                    switch(week2){
                        case 12:
                            Row r13 = sheet.getRow(MonAM2.getRow());
                            Cell c13 = r13.getCell(MonAM2.getCol());
                            c13.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                            c13.setCellStyle(dataStyle);
                            break;
                        case 13:
                            Row r14 = sheet.getRow(MonPM2.getRow());
                            Cell c14 = r14.getCell(MonPM2.getCol());
                            c14.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                            c14.setCellStyle(dataStyle);
                            break;
                        case 14:
                            Row r15 = sheet.getRow(TuesAM2.getRow());
                            Cell c15 = r15.getCell(TuesAM2.getCol());
                            c15.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                            c15.setCellStyle(dataStyle);
                            break;
                        case 15:
                            Row r16 = sheet.getRow(TuesPM2.getRow());
                            Cell c16 = r16.getCell(TuesPM2.getCol());
                            c16.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                            c16.setCellStyle(dataStyle);
                            break;
                        case 16:
                            Row r17 = sheet.getRow(WedAM2.getRow());
                            Cell c17 = r17.getCell(WedAM2.getCol());
                            c17.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                            c17.setCellStyle(dataStyle);
                            break;
                        case 17:
                            Row r18 = sheet.getRow(WedPM2.getRow());
                            Cell c18 = r18.getCell(WedPM2.getCol());
                            c18.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                            c18.setCellStyle(dataStyle);
                            break;
                        case 18:
                            Row r19 = sheet.getRow(ThursAM2.getRow());
                            Cell c19 = r19.getCell(ThursAM2.getCol());
                            c19.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                            c19.setCellStyle(dataStyle);
                            break;
                        case 19:
                            Row r20 = sheet.getRow(ThursPM2.getRow());
                            Cell c20 = r20.getCell(ThursPM2.getCol());
                            c20.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                            c20.setCellStyle(dataStyle);
                            break;
                        case 20:
                            Row r21 = sheet.getRow(FriAM2.getRow());
                            Cell c21 = r21.getCell(FriAM2.getCol());
                            c21.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                            c21.setCellStyle(dataStyle);
                            break;
                        case 21:
                            Row r22 = sheet.getRow(FriPM2.getRow());
                            Cell c22 = r22.getCell(FriPM2.getCol());
                            c22.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                            c22.setCellStyle(dataStyle);
                            break;
                        case 22:
                            Row r23 = sheet.getRow(SatAM2.getRow());
                            Cell c23 = r23.getCell(SatAM2.getCol());
                            c23.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                            c23.setCellStyle(dataStyle);
                            break;
                        case 23:
                            Row r24 = sheet.getRow(SatPM2.getRow());
                            Cell c24 = r24.getCell(SatPM2.getCol());
                            c24.setCellValue("Title: " + clerk.getTitle() + "\nLocation: " + clerk.getLocation());
                            c24.setCellStyle(dataStyle);
                            break;
                        default:
                            break;
                }
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


    public static CellProcessor[] getProcessors(){

        final CellProcessor[] PROCESSORS = new CellProcessor[]{
                new NotNull(), //Student Name
                new Optional(), //Clerkship Title
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



}