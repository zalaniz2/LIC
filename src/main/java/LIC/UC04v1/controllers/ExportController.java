package LIC.UC04v1.controllers;


import LIC.UC04v1.model.Clerkship;
import LIC.UC04v1.model.Doctor;
import LIC.UC04v1.model.Student;
import LIC.UC04v1.repositories.ClerkshipRepository;
import LIC.UC04v1.repositories.DoctorRepository;
import LIC.UC04v1.repositories.StudentRepository;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
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

        // creates mock data
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

        Student temp = null;

        for(Student stu: studentRepository.findAll()){
            temp = stu;
            break;
        }

        String excelFileName = "StudentSchedules.xlsx";

        response2.setHeader("Content-Disposition", "attachment; filename="+excelFileName); //set content type of the response so that jQuery knows what it can expect
        //response2.setHeader("charset", "iso-8859-1");
        response2.setContentType("application/vnd.ms-excel");

        String[] columns = {"Time/Period","Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

        Workbook workbook = new XSSFWorkbook();

        XSSFFont headerFont1 = (XSSFFont) workbook.createFont();
        headerFont1.setBold(true);
        headerFont1.setFontHeightInPoints((short)25);
        headerFont1.setColor(IndexedColors.LAVENDER.getIndex());
        CellStyle headerCellStyle1 = workbook.createCellStyle();
        headerCellStyle1.setFont(headerFont1);

        XSSFFont headerFont2 = (XSSFFont) workbook.createFont();
        headerFont2.setBold(true);
        headerFont2.setFontHeightInPoints((short)17);
        headerFont1.setColor(new XSSFColor(new java.awt.Color(77,25,121)));
        CellStyle headerCellStyle2 = workbook.createCellStyle();
        headerCellStyle2.setFont(headerFont2);

        CreationHelper creationHelper = workbook.getCreationHelper();

        for(Student stu: studentRepository.findAll()){

            Map<String, Clerkship> clerkships = stu.getClerkships();

            String stuName = stu.getName() + "s Weekly Schedule";
            Sheet sheet = workbook.createSheet(stuName);
           // sheet.setDefaultColumnWidth(256);


            Row headerRow = sheet.createRow(0);
            Cell headerCell = headerRow.createCell(0);
            headerCell.setCellValue("Weekly Schedule");
            headerCell.setCellStyle(headerCellStyle1);


            Row stuNameRow = sheet.createRow(1);
            Cell stuNameCell = stuNameRow.createCell(0);
            stuNameCell.setCellValue("Student: " + stu.getName());






            Row weekDay = sheet.createRow(2);
            for(int i = 0; i<columns.length;i++){
                    Cell cell = weekDay.createCell(i+1);
                    cell.setCellValue(columns[i]);
                    cell.setCellStyle(headerCellStyle2);

            }

            for(int i = 3; i < 6; i++){
                Row row = sheet.createRow(i);
                for(int t = 2; t<9; t++){
                    Cell cell = row.createCell(t);
                    sheet.setColumnWidth(i+1,4000);
                }
            }

            Row row = sheet.getRow(3);
            Cell mornCell = row.createCell(1);
            mornCell.setCellValue("Morning");
            mornCell.setCellStyle(headerCellStyle2);

            Row row1 = sheet.getRow(4);
            Cell afternoonCell = row1.createCell(1);
            afternoonCell.setCellStyle(headerCellStyle2);
            afternoonCell.setCellValue("Afternoon");


            CellReference MonAM = new CellReference("C4");
            CellReference MonPM = new CellReference("C5");
            CellReference TuesAM = new CellReference("D4");
            CellReference TuesPM = new CellReference("D5");
            CellReference WedAM = new CellReference("E4");
            CellReference WedPM = new CellReference("E5");
            CellReference ThursAM = new CellReference("F4");
            CellReference ThursPM = new CellReference("F5");
            CellReference FriAM = new CellReference("G4");
            CellReference FriPM = new CellReference("G5");
            CellReference SatAM = new CellReference("H4");
            CellReference SatPM = new CellReference("H5");
            CellReference SunAM = new CellReference("I4");
            CellReference SunPM = new CellReference("I5");

            for(String key: clerkships.keySet()){
                Clerkship clerk = clerkships.get(key);
                switch(clerk.getTime()){
                    case "MonAM":
                        Row r1 = sheet.getRow(MonAM.getRow());
                        Cell c1 = r1.getCell(MonAM.getCol());
                        c1.setCellValue(clerk.getTitle());
                        break;
                    case "WedPM":
                        Row r2 = sheet.getRow(WedPM.getRow());
                        Cell c2 = r2.getCell(WedPM.getCol());
                        c2.setCellValue(clerk.getTitle());
                        break;
                    case "FriAM":
                        Row r3 = sheet.getRow(FriAM.getRow());
                        Cell c3 = r3.getCell(FriAM.getCol());
                        c3.setCellValue(clerk.getTitle());
                        break;

                }

            }

            for (int i = 0; i < columns.length; i++){
                sheet.autoSizeColumn(i);
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