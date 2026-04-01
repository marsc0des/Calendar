package util;

public class Task extends CalendarItem{
    int priority;
    boolean completed;

    //Constructor
    public Task(String title, DateTime start, DateTime end, String owner, String location, int priority, boolean completed) {
        super(title, start, end, owner, location);
        this.priority = priority;
        this.completed = completed;
    }

    public int getPriority() {
        return priority;
    }

    public boolean getCompleted(){
        return completed;
    }

    @Override
    public String getType() {
        return "TASK";
    }

    @Override
    public String getDetails() {
        return getPriority() + ", " + getCompleted();
    }
}
