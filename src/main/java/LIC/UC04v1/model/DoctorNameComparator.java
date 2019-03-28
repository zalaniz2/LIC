package LIC.UC04v1.model;

import java.util.Comparator;

public class DoctorNameComparator implements Comparator<Doctor> {
    @Override
    public int compare(Doctor doc1, Doctor doc2){
        return (doc1.getName().compareTo(doc2.getName()));
    }
}
