package LIC.UC04v1.services;

import LIC.UC04v1.repositories.StudentRepository;
import LIC.UC04v1.model.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class StudentServiceImpl implements StudentService{
    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }




    @Override
    public Student findById(String l){
        Optional<Student> studentOptional = studentRepository.findById(l);

        if(!studentOptional.isPresent()){
            throw new RuntimeException("Student Not Found!");
        }


        return studentOptional.get();
    }


}
