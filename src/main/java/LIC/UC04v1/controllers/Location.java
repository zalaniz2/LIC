package LIC.UC04v1.controllers;
/*
    This enum is where the areas where the general LIC clerkship areas are stored and pulled from.
    Altering this list will change the locations available across the board.
 */
public enum Location {
    FortWorth("Fort Worth"), Denton("Denton"), Dallas("Dallas"), KellerSouthLakeAlliance("Keller/South Lake/Alliance"), Arlington("Arlington"), Mansfield("Mansfield");
    private final String name;
    private Location(String s) {
        name = s;
    }
}
