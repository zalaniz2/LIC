package LIC.UC04v1.controllers;


import LIC.UC04v1.model.Clerkship;
import LIC.UC04v1.model.Doctor;
import LIC.UC04v1.model.Student;
import LIC.UC04v1.repositories.ClerkshipRepository;
import LIC.UC04v1.repositories.DoctorRepository;
import LIC.UC04v1.repositories.StudentRepository;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;
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

        String excelFileName = "excelSchedule.xlsx";

        response2.setHeader("Content-Disposition", "attachment; filename="+excelFileName); //set content type of the response so that jQuery knows what it can expect
        //response2.setHeader("charset", "iso-8859-1");
        response2.setContentType("application/vnd.ms-excel");

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