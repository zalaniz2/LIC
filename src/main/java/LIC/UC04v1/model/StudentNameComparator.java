package LIC.UC04v1.model;

import java.util.Comparator;

public class StudentNameComparator implements Comparator<Student> {
    @Override
    public int compare(Student stu1, Student stu2){
        return (stu1.getName().compareTo(stu2.getName()));
    }
}
