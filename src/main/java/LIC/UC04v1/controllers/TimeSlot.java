package LIC.UC04v1.controllers;

public enum TimeSlot {
    MonM1("Monday Morning 1"), MonA1("Monday Afternoon 1"),
    TueM1("Tuesday Morning 1"), TueA1("Tuesday Afternoon 1"),
    WedM1("Wednesday Morning 1"), WedA1("Wednesday Afternoon 1"),
    ThuM1("Thursday Morning 1"), ThuA1("Thursday Afternoon 1"),
    FriM1("Friday Morning 1"), FriA1("Friday Afternoon 1"),
    SatM1("Saturday  Morning 1"), SatA1("Saturday Afternoon 1"),
    MonM2("Monday Morning 2"), MonA2("Monday Afternoon 2"),
    TueM2("Tuesday Morning 2"), TueA2("Tuesday Afternoon 2"),
    WedM2("Wednesday Morning 2"), WedA2("Wednesday Afternoon 2"),
    ThuM2("Thursday Morning 2"), ThuA2("Thursday Afternoon 2"),
    FriM2("Friday Morning 2"), FriA2("Friday Afternoon 2"),
    SatM2("Saturday  Morning 2"), SatA2("Saturday Afternoon 2");
    private final String name;
    private TimeSlot(String s) {
        name = s;
    }
}
