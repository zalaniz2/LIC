package LIC.UC04v1.controllers;

import LIC.UC04v1.model.Admin;
import LIC.UC04v1.model.Doctor;
import LIC.UC04v1.model.Student;
import LIC.UC04v1.repositories.AdminRepository;
import LIC.UC04v1.repositories.ClerkshipRepository;
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

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
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
@RequestMapping("/admin")
@Controller
public class ImportController {

    private DoctorRepository doctorRepository;
    private StudentRepository studentRepository;
    private ClerkshipRepository clerkshipRepository;
    private AdminRepository adminRepository;
    private String currentDocFile = null;
    private String currentStuFile = null;
    private MiscMethods misc;
    private Admin fileAdmin;

    public ImportController(DoctorRepository doctorRepository, StudentRepository studentRepository,
                            ClerkshipRepository clerkshipRepository, AdminRepository adminRepository){
        this.doctorRepository = doctorRepository;
        this.studentRepository = studentRepository;
        this.clerkshipRepository = clerkshipRepository;
        this.adminRepository = adminRepository;
        misc = new MiscMethods();
    }

    @GetMapping(path = "/import-Data")
    public String getImports(Model model){
        //Get previous file names
        if (adminRepository.count() == 0)
            adminRepository.save(new Admin());
        fileAdmin = adminRepository.findAll().iterator().next();
        updateThymeleaf(model,fileAdmin.getDocFile(),fileAdmin.getStuFile());

        return "ImportData1";
    }

    @RequestMapping(path = "/import-Data/{type}/{file}")
    public String docImport(Model model, @PathVariable String type, MultipartFile file) throws IOException {
        String fileLocation;
        System.out.println(file);
        InputStream in = file.getInputStream();
        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        String[] fileName;
        String errorMsg="";

//        //Get previous file names
//        if (adminRepository.count() == 0)
//            adminRepository.save(new Admin());
//
//        fileAdmin = adminRepository.findAll().iterator().next();

        //Some browsers use the absolute filepath and some use just the filename.
        //Make sure we have the absolute filepath.
        fileName = file.getOriginalFilename().split("/|\\\\");
        fileLocation = path.substring(0, path.length() - 1) + fileName[fileName.length-1];

        if (!(fileLocation.endsWith(".xlsx")||fileLocation.endsWith(".xls")||fileLocation.endsWith(".csv"))){
           model.addAttribute(type+"Error", "Incorrect file format. Please upload a .xlsx, .xls, or .cvs file.");
           updateThymeleaf(model,fileAdmin.getDocFile(),fileAdmin.getStuFile());
           return "ImportData1";
        }

        FileOutputStream f = new FileOutputStream(fileLocation);
        int ch = 0;
        while ((ch = in.read()) != -1) {
            f.write(ch);
        }
        f.flush();
        f.close();

        //Clear the appropriate table(s)
        if (type.equals("doctors")){
            studentRepository.deleteAll();
            doctorRepository.deleteAll();
            currentDocFile = null;
            fileAdmin.setDocFile("");
            fileAdmin.setStuFile("");
        }
        else {
            studentRepository.deleteAll();
            currentStuFile = null;
            fileAdmin.setStuFile("");
        }
        //Clear out clerkships
        clerkshipRepository.deleteAll();

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
            if (type.equals("doctors"))
                model.addAttribute("doctorsError", errorMsg);
            else
                model.addAttribute("studentsError", errorMsg);
            updateThymeleaf(model,fileAdmin.getDocFile(),fileAdmin.getStuFile());
            return "ImportData1";
        }

        //Update the stored file names
        if (type.equals("doctors")) {
            fileAdmin.setDocFile(file.getOriginalFilename());
            currentDocFile = file.getOriginalFilename();
        }
        else {
            fileAdmin.setStuFile(file.getOriginalFilename());
            currentStuFile = file.getOriginalFilename();
        }
        adminRepository.save(fileAdmin);
        updateThymeleaf(model,currentDocFile,currentStuFile);
        return "ImportData1";
    }

    /*******************************************************************************************************************
     * public void updateThymeleaf
     * Send data to the frontend
     ******************************************************************************************************************/
    public void updateThymeleaf(Model model, String currentDocFile, String currentStuFile){
        if (currentDocFile != null) {
            model.addAttribute("docImportMsg", "File: " + currentDocFile
                    + " has been uploaded.");
        }
        if(currentStuFile !=null) {
            model.addAttribute("stuImportMsg", "File: " + currentStuFile
                    + " has been uploaded.");
        }
        return;
    }

    /*******************************************************************************************************************
     * public String checkEmail
     * Validate each email to make sure it's a valid email address
     ******************************************************************************************************************/
    public String checkEmail(String email, String name){
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            return "WARNING: Invalid email for " + name + " : " + email + "\n\n";
        }
        return "";
    }

    /*******************************************************************************************************************
     * public String getPhase1Doc
     * Match each student to their phase one doctor
     * Return a warning if a valid phase 1 doctor was not found
     ******************************************************************************************************************/
    public String getPhase1Doc(String email, Student stu){
        for (Doctor doc : doctorRepository.findAll()) {
            if (doc.getEmail().equals(email)) {
                stu.setPhase1Doc(doc);
                return "";
            }
            //Set phase 1 doctor for Student
            //Set phase 1 student for Doctor
            //Set phase 1 flag to true for Doctor
            //Doctor information is set later in the code - the student must be saved first
        }
        return "WARNING: No doctor found with email " + email + " for student " + stu.getName() + "\n\n";
    }

    /*******************************************************************************************************************
     * public void xlsxFile
     * Reads in a modern excel file with .xlsx extension. The first row is assumed to be a header row and is ignored.
     * The first column holds the student or doctor name. The second column holds the student or doctor email.
     * In the doctor information file, the third column holds the doctor's profession.
     ******************************************************************************************************************/
    public String xlsxFile(String type, MultipartFile file, Model model) throws IOException{
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet worksheet = workbook.getSheetAt(0);
        String errorStr = "";

        //Start at i=1 to skip the header row
        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {
            XSSFRow row = worksheet.getRow(i);
            if(type.equals("doctors")) {
                Doctor doc = new Doctor();
                doc.setName(row.getCell(0).getStringCellValue());
                errorStr = errorStr + checkEmail(row.getCell(1).getStringCellValue(), doc.getName());
                doc.setEmail(row.getCell(1).getStringCellValue());
                doc.setSpecialty(misc.convertSpecialty(row.getCell(2).getStringCellValue()));
                doctorRepository.save(doc);
            }
            else if(type.equals("students")) {
                Student stu = new Student();
                stu.setName(row.getCell(0).getStringCellValue());
                errorStr = errorStr + checkEmail(row.getCell(1).getStringCellValue(), stu.getName());
                stu.setEmail(row.getCell(1).getStringCellValue());
                errorStr = errorStr + getPhase1Doc(row.getCell(2).getStringCellValue(),stu);
                studentRepository.save(stu);
                if (stu.getPhase1Doc()!=null) {
                    stu.getPhase1Doc().setHasPhase1(true);
                    stu.getPhase1Doc().setPhase1Stu(stu);
                    doctorRepository.save(stu.getPhase1Doc());
                }
            }
        }
        return errorStr;
    }

    /*******************************************************************************************************************
     * public void xlsFile
     * Reads in an older excel file with .xls extension. The first row is assumed to be a header row and is ignored.
     * The first column holds the student or doctor name. The second column holds the student or doctor email.
     * In the doctor information file, the third column holds the doctor's profession.
     ******************************************************************************************************************/
    public String xlsFile(String type, MultipartFile file, Model model) throws IOException{
        HSSFWorkbook workbookXLS = new HSSFWorkbook(file.getInputStream());
        HSSFSheet worksheetXLS = workbookXLS.getSheetAt(0);
        String errorStr = "";

        //Start at i=1 to skip the header row
        for (int i = 1; i < worksheetXLS.getPhysicalNumberOfRows(); i++) {

            HSSFRow rowXLS = worksheetXLS.getRow(i);
            if(type.equals("doctors")) {
                Doctor docXLS = new Doctor();
                docXLS.setName(rowXLS.getCell(0).getStringCellValue());
                errorStr = errorStr + checkEmail(rowXLS.getCell(1).getStringCellValue(), docXLS.getName());
                docXLS.setEmail(rowXLS.getCell(1).getStringCellValue());
                docXLS.setSpecialty(misc.convertSpecialty(rowXLS.getCell(2).getStringCellValue()));
                doctorRepository.save(docXLS);
            }
            else if(type.equals("students")) {
                Student stuXLS = new Student();
                stuXLS.setName(rowXLS.getCell(0).getStringCellValue());
                errorStr = errorStr + checkEmail(rowXLS.getCell(1).getStringCellValue(), stuXLS.getName());
                stuXLS.setEmail(rowXLS.getCell(1).getStringCellValue());
                errorStr = errorStr + getPhase1Doc(rowXLS.getCell(2).getStringCellValue(),stuXLS);
                studentRepository.save(stuXLS);
                if (stuXLS.getPhase1Doc()!=null) {
                    stuXLS.getPhase1Doc().setHasPhase1(true);
                    stuXLS.getPhase1Doc().setPhase1Stu(stuXLS);
                    doctorRepository.save(stuXLS.getPhase1Doc());
                }
            }
        }
        return errorStr;
    }

    /*******************************************************************************************************************
     * public void csvFile
     * Reads in an csv file (data file) with .csv extension. The first row is assumed to be a header row and is ignored.
     * The first column holds the student or doctor name. The second column holds the student or doctor email.
     * In the doctor information file, the third column holds the doctor's profession.
     ******************************************************************************************************************/
    public String csvFile(String type, MultipartFile file, Model model) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));//read file
        String errorStr = "";
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
                errorStr = errorStr + checkEmail(values[1], doc.getName());
                doc.setEmail(values[1]);
                //!!!!!!!! FOR THE DEMO - DELETE AFTER
                doc.setAvailabilities(values[3]);
                doc.setSpecialty(misc.convertSpecialty(values[2]));
                doc.setLocation(misc.convertLocation(values[4]));
                doc.setNumStu(Integer.parseInt(values[5]));
                doctorRepository.save(doc);
            }
        }
        else if (type.equals("students")) {
            String st;
            while ((st = br.readLine()) != null){
                values = st.split(",");
                Student stu = new Student();

                stu.setName(values[0]);
                errorStr = errorStr + checkEmail(values[1], stu.getName());
                stu.setEmail(values[1]);
                errorStr = errorStr + getPhase1Doc(values[2],stu);
                studentRepository.save(stu);
                if (stu.getPhase1Doc()!=null) {
                    stu.getPhase1Doc().setHasPhase1(true);
                    stu.getPhase1Doc().setPhase1Stu(stu);
                    doctorRepository.save(stu.getPhase1Doc());
                }
            }
//            while ((line = br.readLine()) != null) {
//                values = line.split(",");
//                if (values.length < 3){
//                    return "CSV File incorrectly formatted. Not enough columns.";
//                }
//                Student stu = new Student();
//                stu.setName(values[0] +" "+ values[1]);
//                stu.setEmail(values[2]);
//                errorStr = errorStr + getPhase1Doc(values[3],stu);
//                studentRepository.save(stu);
//            }
        }
        return errorStr;
    }

}