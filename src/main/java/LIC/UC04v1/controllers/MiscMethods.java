package LIC.UC04v1.controllers;

import java.util.ArrayList;

public class MiscMethods {
    public TimeSlot toTimeSlot(int i){

        switch (i) {
            case 0: return (TimeSlot.MonM1);
            case 1: return (TimeSlot.MonA1);
            case 2: return (TimeSlot.TueM1);
            case 3: return (TimeSlot.TueA1);
            case 4: return (TimeSlot.WedM1);
            case 5: return (TimeSlot.WedA1);
            case 6: return (TimeSlot.ThuM1);
            case 7: return (TimeSlot.ThuA1);
            case 8: return (TimeSlot.FriM1);
            case 9: return (TimeSlot.FriA1);
            case 10: return (TimeSlot.SatM1);
            case 11: return (TimeSlot.SatA1);
            case 12: return (TimeSlot.MonM2);
            case 13: return (TimeSlot.MonA2);
            case 14: return (TimeSlot.TueM2);
            case 15: return (TimeSlot.TueA2);
            case 16: return (TimeSlot.WedM2);
            case 17: return (TimeSlot.WedA2);
            case 18: return (TimeSlot.ThuM2);
            case 19: return (TimeSlot.ThuA2);
            case 20: return (TimeSlot.FriM2);
            case 21: return (TimeSlot.FriA2);
            case 22: return (TimeSlot.SatM2);
            case 23: return (TimeSlot.SatA2);

        }
        return null;
    }
    public Specialty toSpecialty(String a) {
        if (a.equalsIgnoreCase("Neurology")){
            return Specialty.Neurology;
        } else if (a.equalsIgnoreCase("Family Medicine")){
            return Specialty.FamilyMedicine;
        } else if (a.equalsIgnoreCase("Internal Medicine")){
            return Specialty.InternalMedicine;
        } else if (a.equalsIgnoreCase("Surgery")){
            return Specialty.Surgery;
        } else if (a.equalsIgnoreCase("OBGYN")){
            return Specialty.OBGYN;
        } else if (a.equalsIgnoreCase("Pediatrics")){
            return Specialty.Pediatrics;
        } else return Specialty.Psychiatry;
    }
    public TimeSlot getOtherTime(TimeSlot time) {
        switch (time) {
            case FriA1: return TimeSlot.FriA2;
            case FriA2: return TimeSlot.FriA1;
            case ThuA1: return TimeSlot.ThuA2;
            case ThuA2: return TimeSlot.ThuA1;
            case FriM1: return TimeSlot.FriM2;
            case FriM2: return TimeSlot.FriM1;
            case MonA1: return TimeSlot.MonA2;
            case MonA2: return TimeSlot.MonA1;
            case MonM1: return TimeSlot.MonM2;
            case MonM2: return TimeSlot.MonM1;
            case ThuM1: return TimeSlot.ThuM2;
            case ThuM2: return TimeSlot.ThuM1;
            case TueA1: return TimeSlot.TueA2;
            case TueA2: return TimeSlot.TueA1;
            case TueM1: return TimeSlot.TueM2;
            case TueM2: return TimeSlot.TueM1;
            case WedA1: return TimeSlot.WedA2;
            case WedA2: return TimeSlot.WedA1;
            case WedM1: return TimeSlot.WedM2;
            case WedM2: return TimeSlot.WedM1;
            case SatA1: return TimeSlot.SatA2;
            case SatA2: return TimeSlot.SatA1;
            case SatM1: return TimeSlot.SatM2;
            case SatM2: return TimeSlot.SatM1;
        }
        return null;
    }

    public ArrayList<TimeSlot> convertAvailabilities(String avail){
        ArrayList<TimeSlot> returnVal = new ArrayList<TimeSlot>();
        for (int i =0; i <avail.length();i++) {
            if (avail.charAt(i)=='1') {
                switch (i) {
                    case 0: returnVal.add(TimeSlot.MonM1); break;
                    case 1: returnVal.add(TimeSlot.MonA1); break;
                    case 2: returnVal.add(TimeSlot.TueM1); break;
                    case 3: returnVal.add(TimeSlot.TueA1); break;
                    case 4: returnVal.add(TimeSlot.WedM1); break;
                    case 5: returnVal.add(TimeSlot.WedA1); break;
                    case 6: returnVal.add(TimeSlot.ThuM1); break;
                    case 7: returnVal.add(TimeSlot.ThuA1); break;
                    case 8: returnVal.add(TimeSlot.FriM1); break;
                    case 9: returnVal.add(TimeSlot.FriA1); break;
                    case 10: returnVal.add(TimeSlot.SatM1); break;
                    case 11: returnVal.add(TimeSlot.SatA1); break;
                    case 12: returnVal.add(TimeSlot.MonM2); break;
                    case 13: returnVal.add(TimeSlot.MonA2); break;
                    case 14: returnVal.add(TimeSlot.TueM2); break;
                    case 15: returnVal.add(TimeSlot.TueA2); break;
                    case 16: returnVal.add(TimeSlot.WedM2); break;
                    case 17: returnVal.add(TimeSlot.WedA2); break;
                    case 18: returnVal.add(TimeSlot.ThuM2); break;
                    case 19: returnVal.add(TimeSlot.ThuA2); break;
                    case 20: returnVal.add(TimeSlot.FriM2); break;
                    case 21: returnVal.add(TimeSlot.FriA2); break;
                    case 22: returnVal.add(TimeSlot.SatM2); break;
                    case 23: returnVal.add(TimeSlot.SatA2); break;

                }
            }
        }
        return returnVal;
    }
//    public ArrayList<Integer[]> clerkshipsInOneDay () {
//
//    }
}
