package util;

public class Appointment extends CalendarItem{
    String withWhom;

    //constructor
    public Appointment(String title, DateTime start, DateTime end, String owner, String location, String withWhom) {
        super(title, start, end, owner, location);
        this.withWhom = withWhom;
    }

    public String getWithWhom(){
        return withWhom;
    }

    @Override
    public String getType() {
        return "APPOINTMENT";
    }

    @Override
    public String getDetails() {
        return getWithWhom();
    }
}
