package util;

public class DateTime implements Comparable<DateTime>{
    private Date date;
    private int hour,
            minute,
            second;
    private boolean am;//true if time is before noon and false otherwise

    public DateTime(int year, int month, int day,
                    int hour, int minute, int second, boolean am) {
        this.date = new Date(day, month, year);
        this.hour = (hour == 12)? 0: hour;
        this.minute = minute;
        this.second = second;
        this.am = am;
    }
    public static boolean isLegal(DateTime dateTime){
        if(!Date.isLegal(dateTime.date))
            return false;
        if(dateTime.second < 0 || dateTime.second > 59)
            return false;
        if(dateTime.minute < 0 || dateTime.minute > 59)
            return false;
        if(dateTime.hour < 0 || dateTime.hour > 11)
            return false;
        return true;
    }

    @Override
    public int compareTo(DateTime other) {
        if(this.date.compareTo(other.date) != 0)
            return this.date.compareTo(other.date);
        if(this.am != other.am) {
            if (!this.am && other.am)//this is pm and other is am
                return 1;
            else //this is am and other is pm
                return -1;
        }
        if(this.hour != other.hour)
            return this.hour - other.hour;
        if(this.minute != other.minute)
            return this.minute - other.minute;
        return this.second - other.second;
    }

    @Override
    public String toString(){//MM/DD/YYYY hh:mm:ss am/pm
        return String.format("%s %02d:%02d:%02d %s",
                date, (hour == 0)? 12: hour, minute, second,
                am?"am":"pm");
    }
    private class Date implements Comparable<Date>{
        public int day, month, year;
        @Override
        public String toString(){
            return String.format("%02d/%02d/%04d", month, day, year);
        }
        public Date(int day, int month, int year){
            this.day = day;
            this.month = month;
            this.year = year;
        }
        @Override
        public int compareTo(Date other){
            if(this.year != other.year)
                return this.year - other.year;
            if(this.month != other.month)
                return this.month - other.month;
            return this.day - other.day;
        }
        public static boolean isLegal(Date date){
            int monthLengh = 31;
            switch (date.month){
                case 4: case 6: case 9: case 11:
                    monthLengh = 30;
                    break;
                case 2://Feb
                    if(date.year % 4 == 0 && date.year % 100 != 0 ||
                    date.year % 400 == 0)
                        monthLengh = 29;
                    else
                        monthLengh = 28;
            }
            if(date.day < 1 || date.day > monthLengh)
                return false;
            if(date.month < 1 || date.month > 12)
                return false;
            if(date.year < 1 || date.year > 9999)
                return false;
            return true;
        }
    }
}
