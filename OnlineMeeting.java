package util;

public class OnlineMeeting extends Meeting{
    String platform;
    String meetingLink;

    //Constructor
    public OnlineMeeting(String title, DateTime start, DateTime end, String owner, String location, int invitees, String agenda, String platform, String meetingLink) {
        super(title, start, end, owner, location, invitees, agenda);
        this.platform = platform;
        this.meetingLink = meetingLink;
    }

    public String getPlatform(){
        return platform;
    }

    public String getMeetingLink(){
        return meetingLink;
    }

    //Overrides for getType and getDetails for online meeting
    @Override
    public String getType() {
      return "ONLINE";
    }

    @Override
    public String getDetails() {
        return getPlatform() + ", " + getMeetingLink();
    }
}
