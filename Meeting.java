package util;

public class Meeting extends CalendarItem{
    private int invitees;
    private String agenda;

    //constructor
    public Meeting(String title, DateTime start, DateTime end, String owner, String location, int invitees, String agenda) {
        super(title, start, end, owner, location);
        this.invitees = invitees;
        this.agenda = agenda;
    }

    //getter and setters for private variables
    public int getInvitees() {
        return invitees;
    }

    public String getAgenda() {
        return agenda;
    }


    //Overrides for getType and getDetails for meeting
    @Override
    public String getType() {
        return "MEETING";
    }

    @Override
    public String getDetails() {
        return getInvitees() + ", " + getAgenda();
    }
}
