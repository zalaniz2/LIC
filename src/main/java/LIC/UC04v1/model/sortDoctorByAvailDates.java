package LIC.UC04v1.model;

import java.util.Comparator;

public class sortDoctorByAvailDates implements Comparator<Doctor> {
    public int compare(Doctor a, Doctor b)
    {
        return a.getNumberOfDaysAvail() -b.getNumberOfDaysAvail();
    }
}
