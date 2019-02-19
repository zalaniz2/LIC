package LIC.UC04v1.controllers;

import LIC.UC04v1.model.Doctor;
import LIC.UC04v1.model.Student;
import LIC.UC04v1.repositories.DoctorRepository;
import LIC.UC04v1.repositories.StudentRepository;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.io.*;

/*
    SOME NOTES ON THIS CLASS
        This class pulls in data from an excel file. It saves the first column as a doctor/student name
        and the second column as the doctor/student email. There are currently no checks in place to
        make sure the excel file is formatted correctly. Additionally, blank rows may be added to the
        database. We'll have to fix this and do extensive testing, but it works at a low fidelity!
    If you have questions about anything, let me (Katie) know. I'll come back and clean up/comment better soon.
 */

@Controller
public class ImportController {

    private DoctorRepository doctorRepository;
    private StudentRepository studentRepository;
    private String currentDocFile = null;
    private String currentStuFile = null;

    public ImportController(DoctorRepository doctorRepository, StudentRepository studentRepository){
        this.doctorRepository = doctorRepository;
        this.studentRepository = studentRepository;
    }

    @GetMapping(path = "/import-Data")
    public String getImports(Model model){
        return "ImportData";
    }

    @RequestMapping(path = "/import-Data/{type}/{file}")
    public String docImport(Model model, @PathVariable String type, MultipartFile file) throws IOException {
        String fileLocation;
        InputStream in = file.getInputStream();
        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        String[] fileName;
        String errorMsg="";

        //Some browsers use the absolute filepath and some use just the filename.
        //Make sure we have the absolute filepath.
        fileName = file.getOriginalFilename().split("/|\\\\");
        fileLocation = path.substring(0, path.length() - 1) + fileName[fileName.length-1];

        if (!(fileLocation.endsWith(".xlsx")||fileLocation.endsWith(".xls")||fileLocation.endsWith(".csv"))){
           model.addAttribute(type+"Error", "Incorrect file format. Please upload a .xlsx, .xls, or .cvs file.");
           updateThymeleaf(model,currentDocFile,currentStuFile);
           return "ImportData";
        }

        FileOutputStream f = new FileOutputStream(fileLocation);
        int ch = 0;
        while ((ch = in.read()) != -1) {
            f.write(ch);
        }
        f.flush();
        f.close();

        //Clear the appropriate table
        //Not currently working!!
        if (type == "doctors"){
            doctorRepository.deleteAll();
            currentDocFile = null;
        }
        else {
            studentRepository.deleteAll();
            currentStuFile = null;
        }

        //Read in a modern .xlsx Excel file
        if (fileLocation.endsWith(".xlsx")) {
           errorMsg = xlsxFile(type,file,model);
        }
        //Read in an older .xls Excel file
        else if (fileLocation.endsWith(".xls")) {
            errorMsg = xlsFile(type,file,model);
        }
        //Read in a .csv file
        else if (fileLocation.endsWith(".csv")) {
           errorMsg = csvFile(type,file,model);
        }

        //Check for any errors
        if (!errorMsg.equals("")) {
            model.addAttribute(type+"Error", errorMsg);
            updateThymeleaf(model,currentDocFile,currentStuFile);
            return "ImportData";
        }

        //Update the stored file names
        if (type.equals("doctors"))
            currentDocFile = file.getOriginalFilename();
        else
            currentStuFile = file.getOriginalFilename();

        updateThymeleaf(model,currentDocFile,currentStuFile);
        return "ImportData";
    }

    /*******************************************
     * public void updateThymeleaf
     * Sends success messages to Thymeleaf frontend.
     */
    public void updateThymeleaf(Model model, String currentDocFile, String currentStuFile){
        if (currentDocFile != null) {
            model.addAttribute("docImportMsg", "File: " + currentDocFile
                    + " has been uploaded successfully!");
        }
        if(currentStuFile !=null) {
            model.addAttribute("stuImportMsg", "File: " + currentStuFile
                    + " has been uploaded successfully!");
        }
        return;
    }

    /*******************************************************************************************************************
     * public void xlsxFile
     * Reads in a modern excel file with .xlsx extension. The first row is assumed to be a header row and is ignored.
     * The first column holds the student or doctor name. The second column holds the student or doctor email.
     * In the doctor information file, the third column holds the doctor's profession.
     * @param type
     * @param file
     * @throws IOException
     ******************************************************************************************************************/

    public String xlsxFile(String type, MultipartFile file, Model model) throws IOException{
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);

        //Start at i=1 to skip the header row
        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {
            XSSFRow row = worksheet.getRow(i);
            if(type.equals("doctors")) {
                Doctor doc = new Doctor();
                doc.setName(row.getCell(0).getStringCellValue());
                doc.setEmail(row.getCell(1).getStringCellValue());
                doc.setSpecialty(row.getCell(2).getStringCellValue());
                doctorRepository.save(doc);
            }
            else if(type.equals("students")) {
                Student stu = new Student();
                stu.setName(row.getCell(0).getStringCellValue());
                stu.setEmail(row.getCell(1).getStringCellValue());
                studentRepository.save(stu);
            }
        }
        return "";
    }

    /*******************************************************************************************************************
     * public void xlsFile
     * Reads in an older excel file with .xls extension. The first row is assumed to be a header row and is ignored.
     * The first column holds the student or doctor name. The second column holds the student or doctor email.
     * In the doctor information file, the third column holds the doctor's profession.
     * @param type
     * @param file
     * @throws IOException
     ******************************************************************************************************************/
    public String xlsFile(String type, MultipartFile file, Model model) throws IOException{
        HSSFWorkbook workbookXLS = new HSSFWorkbook(file.getInputStream());
        HSSFSheet worksheetXLS = workbookXLS.getSheetAt(0);

        //Start at i=1 to skip the header row
        for (int i = 1; i < worksheetXLS.getPhysicalNumberOfRows(); i++) {

            HSSFRow rowXLS = worksheetXLS.getRow(i);
            if(type.equals("doctors")) {
                Doctor docXLS = new Doctor();
                docXLS.setName(rowXLS.getCell(0).getStringCellValue());
                docXLS.setEmail(rowXLS.getCell(1).getStringCellValue());
                docXLS.setSpecialty(rowXLS.getCell(2).getStringCellValue());
                doctorRepository.save(docXLS);
            }
            else if(type.equals("students")) {
                Student stuXLS = new Student();
                stuXLS.setName(rowXLS.getCell(0).getStringCellValue());
                stuXLS.setEmail(rowXLS.getCell(1).getStringCellValue());
                studentRepository.save(stuXLS);
            }
        }
        return "";
    }

    /*******************************************************************************************************************
     * public void csvFile
     * Reads in an csv file (data file) with .csv extension. The first row is assumed to be a header row and is ignored.
     * The first column holds the student or doctor name. The second column holds the student or doctor email.
     * In the doctor information file, the third column holds the doctor's profession.
     * @param type
     * @param file
     * @throws IOException
     ******************************************************************************************************************/
    public String csvFile(String type, MultipartFile file, Model model) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));//read file

        String line;//read file into database on instance of doctor at a time.
        br.readLine(); //Don't read in the header
        String[] values;
        if (type.equals("doctors")) {
            while ((line = br.readLine()) != null) {
                values = line.split(",");
                if (values.length < 3){
                    return "CSV File incorrectly formatted. Not enough columns.";
                }
                Doctor doc = new Doctor();
                doc.setName(values[0]);
                doc.setEmail(values[1]);
                doc.setSpecialty(values[2]);
                doctorRepository.save(doc);
            }
        }
        else if (type.equals("students")) {
            while ((line = br.readLine()) != null) {
                values = line.split(",");
                if (values.length < 2){
                    return "CSV File incorrectly formatted. Not enough columns.";
                }
                Student stu = new Student();
                stu.setName(values[0]);
                stu.setEmail(values[1]);
                studentRepository.save(stu);
            }
        }
        return "";
    }
}
