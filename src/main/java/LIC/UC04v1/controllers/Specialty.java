package LIC.UC04v1.controllers;

public enum Specialty {
    Neurology {
        public String toString(){
            return ("Neurology");
        }
    },
    FamilyMedicine{
        public String toString() {
            return ("Family Medicine");
        }
    },
    InternalMedicine{
        public String toString(){
            return ("Internal Medicine");
        }
    },
    Surgery{
        public String toString(){
            return "Surgery";
        }
    },
    OBGYN{
        public String toString(){
            return ("OBGYN");
        }
    },
    Pediatrics{
        public String toString(){
            return ("Pediatrics");
        }
    },
    Psychiatry{
        public String toString(){
            return ("Psychiatry");
        }
    };



}
