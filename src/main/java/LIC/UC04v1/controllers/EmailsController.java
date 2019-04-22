package LIC.UC04v1.controllers;

import LIC.UC04v1.model.Doctor;
import LIC.UC04v1.model.Student;
import LIC.UC04v1.repositories.DoctorRepository;
import LIC.UC04v1.repositories.StudentRepository;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.io.*;
import java.util.Properties;

/*
    SOME NOTES ON THIS CLASS
        This class sends emails to all the doctors and students in the database. Doctors receive a
        unique link to the availabilities form, and students receive a unique link to build their
        schedule. Emails are currently sent from our no.reply.lic.tcu@gmail.com address.
    If you have questions about anything, let me (Katie) know. I'll come back and clean up/comment better soon.
 */

@RequestMapping("/admin")
@Controller
public class EmailsController {

    private DoctorRepository doctorRepository;
    private StudentRepository studentRepository;
    private JavaMailSender mailSender;

    public EmailsController(DoctorRepository doctorRepository, StudentRepository studentRepository){
        this.doctorRepository = doctorRepository;
        this.studentRepository = studentRepository;
    }

    @GetMapping(path = "/send-emails")
    public String getImports(Model model){
        return "sendEmails1";
    }

    @RequestMapping(path = "/send-emails/{type}")
    public String docEmails(Model model, @PathVariable String type) throws Exception{

//        JavaMailSenderImpl sender = new JavaMailSenderImpl();
//
//        sender.setHost("email-smtp.us-east-1.amazonaws.com");
//        sender.setUsername("AKIA5DFOVNYYM4QZ7RML");
//        sender.setPassword("BPSHBFaQi/8nt1Fn1wYsdC6lFoAEzqgbXDPlGwcrXgA5");
//        sender.setPort(587);
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.port", 587);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        Session session = Session.getDefaultInstance(props);
//        sender.setJavaMailProperties(props);

        //Loop through all the doctors and send an email
        if(type.equals("doctors")) {
            for (Doctor doc : doctorRepository.findAll()) {
                System.out.println("One doctor...");
//                MimeMessage message = sender.createMimeMessage();
//                MimeMessageHelper helper = new MimeMessageHelper(message);
                MimeMessage msg = new MimeMessage(session);

                msg.setFrom(new InternetAddress( "MEDClerkship@tcu.edu","Kayla Beeler"));
                msg.setRecipient(Message.RecipientType.TO, new InternetAddress("no.reply.lic.tcu@gmail.com"));
                msg.setSubject("TCU/UNTHSC Med School Availabillity Questionair");
                msg.setContent(doc.getName() + ", \n " +
                        "Thank you for offering to teach a medical school student. The next step is to indicate " +
                        "your availabilities. Please follow the unique link below and complete the form. Do not " +
                        "share the link with others. \n" +
                        "localhost:8080/doctor/" + doc.getId() +
                        "\n\nThank you!\nTCU/UNTHSC Medical School","text/html");

                //msg.setHeader("X-SES-CONFIGURATION-SET", "ConfigSet");

                Transport transport = session.getTransport();

                try
                {
                    System.out.println("Sending...");

                    // Connect to Amazon SES using the SMTP username and password you specified above.
                    transport.connect("email-smtp.us-east-1.amazonaws.com", "AKIA5DFOVNYYM4QZ7RML", "BPSHBFaQi/8nt1Fn1wYsdC6lFoAEzqgbXDPlGwcrXgA5");

                    // Send the email.
                    transport.sendMessage(msg, msg.getAllRecipients());
                    System.out.println("Email sent!");
                }
                catch (Exception ex) {
                    System.out.println("The email was not sent.");
                    System.out.println("Error message: " + ex.getMessage());
                }
                finally
                {
                    // Close and terminate the connection.
                    transport.close();
                }

                //We will un-comment this. I don't want to accidentally send emails to all the doctors!!
                //helper.setTo(doc.getEmail());
                //For now, send emails to a junk account
//                helper.setTo("no.reply.lic.tcu@gmail.com");
              
//                helper.setText(doc.getName() + ", \n " +
//                        "Thank you for offering to teach a medical school student. The next step is to indicate " +
//                        "your availabilities. Please follow the unique link below and complete the form. Do not " +
//                        "share the link with others. \n" +
//                        "localhost:8080/doctor/" + doc.getId() +
//                        "\n\nThank you!\nTCU/UNTHSC Medical School"
//                );
//                helper.setSubject("Information Required");
//
//                try {
//                    sender.send(message);
//                }
//                catch(Exception e){
//
//                }
            }
        }
        else{
            for (Student stu: studentRepository.findAll()) {
                MimeMessage msg = new MimeMessage(session);

                msg.setFrom(new InternetAddress( "MEDClerkship@tcu.edu","Kayla Beeler"));
                msg.setRecipient(Message.RecipientType.TO, new InternetAddress("no.reply.lic.tcu@gmail.com"));
                msg.setSubject("TCU/UNTHSC Student Clerkship Schedule");
                msg.setContent(stu.getName() + ", \n " +
                        "It's time to request your clerkships. Please follow the unique link below to complete " +
                        "the process. Please do not share the link with others, as it is linked to your name. \n" +
                        "localhost:8080/" + stu.getId() +
                        "\n\nThank you!\nTCU/UNTHSC Medical School","text/html");

                //msg.setHeader("X-SES-CONFIGURATION-SET", "ConfigSet");

                Transport transport = session.getTransport();

                try
                {
                    System.out.println("Sending...");

                    // Connect to Amazon SES using the SMTP username and password you specified above.
                    transport.connect("email-smtp.us-east-1.amazonaws.com", "AKIA5DFOVNYYM4QZ7RML", "BPSHBFaQi/8nt1Fn1wYsdC6lFoAEzqgbXDPlGwcrXgA5");

                    // Send the email.
                    transport.sendMessage(msg, msg.getAllRecipients());
                    System.out.println("Email sent!");
                }
                catch (Exception ex) {
                    System.out.println("The email was not sent.");
                    System.out.println("Error message: " + ex.getMessage());
                }
                finally
                {
                    // Close and terminate the connection.
                    transport.close();
                }
//                MimeMessage message = sender.createMimeMessage();
//                MimeMessageHelper helper = new MimeMessageHelper(message);
//
//                //We will un-comment this. I don't want to accidentally send emails to all the students!!
//                //helper.setTo(doc.getEmail());
//                //For now, send emails to a junk account
//                helper.setTo("no.reply.lic.tcu@gmail.com");
//                helper.setText(stu.getName() + ", \n " +
//                        "It's time to request your clerkships. Please follow the unique link below to complete " +
//                        "the process. Please do not share the link with others, as it is linked to your name. \n" +
//                        "localhost:8080/" + stu.getId() +
//                        "\n\nThank you!\nTCU/UNTHSC Medical School"
//                );
//                helper.setSubject("Request Clerkship Schedule");
//
//                sender.send(message);
            }
        }
        return "sendemails1";
    }
}
