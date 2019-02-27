package LIC.UC04v1.controllers;

public enum Specialty {
    Neurology ("Neurology"), FamilyMedicine("Family Medicine"), InternalMedicine("Internal Medicine"),
    Surgery("Surgery"), OBGYN("OBGYN"), Pediatrics("Pediatrics"), Psychiatry("Psychiatry");
    private String name;
    private Specialty(String s) {
        name = s;
    }
    public String name2(){
        return name;
    }
}
