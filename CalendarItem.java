package util;

public abstract class CalendarItem implements Comparable<CalendarItem>{
    private String title;
    private DateTime start;
    private DateTime end;
    private String owner;
    private String location;
    public abstract String getType();
    public abstract String getDetails();

    //Constructor
    public CalendarItem(String title, DateTime start, DateTime end, String owner, String location){
        //implement this to initialize all its private instance fields!
        this.title = title;
        this.start = start;
        this.end = end;
        this.owner = owner;
        this.location = location;
    }

    public String getTitle(){
        return title;
    }

    public DateTime getStart(){
        return start;
    }

    public DateTime getEnd(){
        return end;
    }

    public String getOwner(){
        return owner;
    }

    public String getLocation(){
        return location;
    }
    @Override
    public int compareTo(CalendarItem other){
        /*Calendar items must be sorted by:
            Earliest start time first
            If start times are equal → Longer duration first
            If both equal → Alphabetical by title (case-insensitive)
         */
        //Checks start times and goes by first time
        int startComparison = this.getStart().compareTo(other.getStart());
        if (startComparison != 0) {
            return startComparison;
        }
        //checks duration and goes by longest
        int endComparison = other.getEnd().compareTo(this.getEnd());
        if(endComparison != 0){
            return endComparison;
        }

        //prints alphabetically
        return this.title.compareToIgnoreCase(other.title);
    }

}
