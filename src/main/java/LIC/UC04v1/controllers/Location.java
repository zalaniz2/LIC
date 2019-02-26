package LIC.UC04v1.controllers;

public enum Location {
    FortWorth("Fort Worth"), Denton("Denton"), Dallas("Dallas"), KellerSouthLakeAlliance("Keller/South Lake/Alliance"), Arlington("Arlington"), Mansfield("Mansfield");
    private final String name;
    private Location(String s) {
        name = s;
    }
}
