package LIC.UC04v1.controllers;

public enum Location {
    FortWorth("Fort Worth"), NorthFortWorth("North Fort Worth"), Dallas("Dallas"), KellerSouthLake("Keller/South Lake"), Arlington("Arlington"), Mansfield("Mansfield");
    private final String name;
    private Location(String s) {
        name = s;
    }
}
