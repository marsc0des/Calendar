package util;

import java.util.*;
import java.io.*;

public class Main {

    // Parse DateTime from string
    public static DateTime parseDateTime(String input) throws InvalidCalendarItemException {
        try {
            String[] split = input.split(" @ ");
            if (split.length != 2) throw new InvalidCalendarItemException("Invalid date-time format");

            String[] dateParts = split[0].split("/");
            if (dateParts.length != 3) throw new InvalidCalendarItemException("Invalid date format");

            int month = Integer.parseInt(dateParts[0]);
            int day = Integer.parseInt(dateParts[1]);
            int year = Integer.parseInt(dateParts[2]);

            String[] timeParts = split[1].split(" ");
            if (timeParts.length != 2) throw new InvalidCalendarItemException("Invalid time format");

            String[] hms = timeParts[0].split(":");
            if (hms.length != 3) throw new InvalidCalendarItemException("Invalid time format");

            int hour = Integer.parseInt(hms[0]);
            int minute = Integer.parseInt(hms[1]);
            int second = Integer.parseInt(hms[2]);

            boolean am;
            if (timeParts[1].equalsIgnoreCase("am")) am = true;
            else if (timeParts[1].equalsIgnoreCase("pm")) am = false;
            else throw new InvalidCalendarItemException("Invalid am/pm");

            DateTime dt = new DateTime(year, month, day, hour, minute, second, am);
            if (!DateTime.isLegal(dt)) throw new InvalidCalendarItemException("Illegal DateTime");

            return dt;

        } catch (NumberFormatException e) {
            throw new InvalidCalendarItemException("Number format error in DateTime");
        }
    }

    // Print item in console
    public static void printItem(CalendarItem item) {
        System.out.println(item.getType() + ", " +
                item.getTitle() + ", " +
                item.getStart() + ", " +
                item.getEnd() + ", " +
                item.getOwner() + ", " +
                item.getLocation() + ", " +
                item.getDetails());
    }

    // Main
    public static void main(String[] args) {
        ArrayList<CalendarItem> items = new ArrayList<>();
        Scanner scnr = new Scanner(System.in);

        System.out.println("Hello :) Welcome to your calendar...");
        System.out.println("Enter your calendar items in the following formats:");
        System.out.println("MEETING, title, startDT, endDT, owner, location, invitees, agenda");
        System.out.println("ONLINE, title, startDT, endDT, owner, location, invitees, agenda, platform, meetingLink");
        System.out.println("APPOINTMENT, title, startDT, endDT, owner, location, withWhom");
        System.out.println("TASK, title, startDT, endDT, owner, location, priority, completed");
        System.out.println("Date format: MM/DD/YYYY @ hh:mm:ss am/pm");
        System.out.println("Type 'Done' to finish");

        // Input phase
        while (true) {
            String line = scnr.nextLine();
            if (line.equalsIgnoreCase("Done")) break;

            String[] parts = line.split(", ");

            try {
                CalendarItem item = parseInput(parts);
                items.add(item);

            } catch (InvalidCalendarItemException e) {
                System.out.println("Invalid input: " + e.getMessage());
                System.out.println("Please re-enter the item.");
            }
        }

        // Command phase
        while (true) {
            System.out.print("Enter command: ");
            String command = scnr.nextLine();

            if (command.equalsIgnoreCase("quit")) break;

            else if (command.equalsIgnoreCase("print")) {
                Collections.sort(items);
                for (CalendarItem ci : items) printItem(ci);
            }

            else if (command.toLowerCase().startsWith("owned by")) {
                String owner = command.substring(9).trim();
                for (CalendarItem ci : items)
                    if (ci.getOwner().equalsIgnoreCase(owner)) printItem(ci);
            }

            else if (command.toLowerCase().startsWith("happening on")) {
                String dateStr = command.substring(13).trim();
                try {
                    DateTime dt = parseDateTime(dateStr);
                    for (CalendarItem ci : items)
                        if (ci.getStart().compareTo(dt) <= 0 && ci.getEnd().compareTo(dt) >= 0)
                            printItem(ci);
                } catch (InvalidCalendarItemException e) {
                    System.out.println("Invalid date-time.");
                }
            }

            else if (command.toLowerCase().startsWith("type is")) {
                String typeFilter = command.substring(8).trim().toUpperCase();
                for (CalendarItem ci : items)
                    if (ci.getType().equalsIgnoreCase(typeFilter)) printItem(ci);
            }

            else if (command.toLowerCase().startsWith("export")) {
                String filename = command.substring(6).trim();
                if (filename.isEmpty()) {
                    System.out.println("Export failed.");
                    continue;
                }
                try (PrintWriter pw = new PrintWriter(filename)) {
                    Collections.sort(items);
                    for (CalendarItem ci : items) {
                        String lineOut = ci.getType() + "\t" +
                                ci.getTitle() + "\t" +
                                ci.getStart() + "\t" +
                                ci.getEnd() + "\t" +
                                ci.getOwner() + "\t" +
                                ci.getLocation() + "\t";

                        // Details to tab
                        if (ci instanceof Meeting && !(ci instanceof OnlineMeeting)) {
                            Meeting m = (Meeting) ci;
                            lineOut += m.getInvitees() + "\t" + m.getAgenda();
                        } else if (ci instanceof OnlineMeeting) {
                            OnlineMeeting o = (OnlineMeeting) ci;
                            lineOut += o.getInvitees() + "\t" + o.getAgenda() + "\t" + o.getPlatform() + "\t" + o.getMeetingLink();
                        } else if (ci instanceof Appointment) {
                            Appointment a = (Appointment) ci;
                            lineOut += a.getWithWhom();
                        } else if (ci instanceof Task) {
                            Task t = (Task) ci;
                            lineOut += t.getPriority() + "\t" + t.getCompleted();
                        }

                        pw.println(lineOut);
                    }
                    System.out.println("Export completed.");
                } catch (Exception e) {
                    System.out.println("Export failed.");
                }
            }

            else if (command.toLowerCase().startsWith("import")) {
                String filename = command.substring(6).trim();
                if (filename.isEmpty()) {
                    System.out.println("Import failed.");
                    continue;
                }
                try (Scanner file = new Scanner(new File(filename))) {
                    while (file.hasNextLine()) {
                        String row = file.nextLine();
                        String[] parts = row.split("\t");
                        try {
                            CalendarItem importedItem = parseImported(parts);
                            items.add(importedItem);
                        } catch (InvalidCalendarItemException e) {
                            System.out.println("Skipping invalid file row: " + e.getMessage());
                        }
                    }
                    System.out.println("Import completed.");
                } catch (Exception e) {
                    System.out.println("Import failed.");
                }
            }

            else if (command.equalsIgnoreCase("help")) {
                System.out.println("print");
                System.out.println("happening on <MM/DD/YYYY @ hh:mm:ss am/pm>");
                System.out.println("owned by <ownerName>");
                System.out.println("type is <TYPE>");
                System.out.println("import <filename>");
                System.out.println("export <filename>");
                System.out.println("help");
                System.out.println("quit");
            }

            else {
                System.out.println("Unknown command. Type 'help' for commands.");
            }
        }

        scnr.close();
    }

    // Parse user input line (comma-separated)
    public static CalendarItem parseInput(String[] parts) throws InvalidCalendarItemException {
        String type = parts[0].toUpperCase();
        switch (type) {
            case "MEETING":
                if (parts.length != 8) throw new InvalidCalendarItemException("Invalid number of fields");
                int inviteesM = Integer.parseInt(parts[6]);
                if (inviteesM < 0) throw new InvalidCalendarItemException("Invitees cannot be negative");
                DateTime startM = parseDateTime(parts[2]);
                DateTime endM = parseDateTime(parts[3]);
                if (endM.compareTo(startM) <= 0) throw new InvalidCalendarItemException("End time must be after start time");
                return new Meeting(parts[1], startM, endM, parts[4], parts[5], inviteesM, parts[7]);

            case "ONLINE":
                if (parts.length != 10) throw new InvalidCalendarItemException("Invalid number of fields");
                int inviteesO = Integer.parseInt(parts[6]);
                if (inviteesO < 0) throw new InvalidCalendarItemException("Invitees cannot be negative");
                DateTime startO = parseDateTime(parts[2]);
                DateTime endO = parseDateTime(parts[3]);
                if (endO.compareTo(startO) <= 0) throw new InvalidCalendarItemException("End time must be after start time");
                return new OnlineMeeting(parts[1], startO, endO, parts[4], parts[5], inviteesO, parts[7], parts[8], parts[9]);

            case "APPOINTMENT":
                if (parts.length != 7) throw new InvalidCalendarItemException("Invalid number of fields");
                DateTime startA = parseDateTime(parts[2]);
                DateTime endA = parseDateTime(parts[3]);
                if (endA.compareTo(startA) <= 0) throw new InvalidCalendarItemException("End time must be after start time");
                return new Appointment(parts[1], startA, endA, parts[4], parts[5], parts[6]);

            case "TASK":
                if (parts.length != 8) throw new InvalidCalendarItemException("Invalid number of fields");
                DateTime startT = parseDateTime(parts[2]);
                DateTime endT = parseDateTime(parts[3]);
                if (endT.compareTo(startT) <= 0) throw new InvalidCalendarItemException("End time must be after start time");
                int priority = Integer.parseInt(parts[6]);
                if (priority < 1 || priority > 5) throw new InvalidCalendarItemException("Priority must be 1–5");
                boolean completed = Boolean.parseBoolean(parts[7]);
                return new Task(parts[1], startT, endT, parts[4], parts[5], priority, completed);

            default:
                throw new InvalidCalendarItemException("Unknown type");
        }
    }

    // Parse tab-delimited import row
    public static CalendarItem parseImported(String[] parts) throws InvalidCalendarItemException {
        String type = parts[0];
        switch (type) {
            case "MEETING":
                if (parts.length != 8) throw new InvalidCalendarItemException("Invalid number of fields");
                int inviteesM = Integer.parseInt(parts[6]);
                DateTime startM = parseDateTime(parts[2]);
                DateTime endM = parseDateTime(parts[3]);
                return new Meeting(parts[1], startM, endM, parts[4], parts[5], inviteesM, parts[7]);

            case "ONLINE":
                if (parts.length != 10) throw new InvalidCalendarItemException("Invalid number of fields");
                int inviteesO = Integer.parseInt(parts[6]);
                DateTime startO = parseDateTime(parts[2]);
                DateTime endO = parseDateTime(parts[3]);
                return new OnlineMeeting(parts[1], startO, endO, parts[4], parts[5], inviteesO, parts[7], parts[8], parts[9]);

            case "APPOINTMENT":
                if (parts.length != 7) throw new InvalidCalendarItemException("Invalid number of fields");
                DateTime startA = parseDateTime(parts[2]);
                DateTime endA = parseDateTime(parts[3]);
                return new Appointment(parts[1], startA, endA, parts[4], parts[5], parts[6]);

            case "TASK":
                if (parts.length != 8) throw new InvalidCalendarItemException("Invalid number of fields");
                DateTime startT = parseDateTime(parts[2]);
                DateTime endT = parseDateTime(parts[3]);
                int priority = Integer.parseInt(parts[6]);
                boolean completed = Boolean.parseBoolean(parts[7]);
                return new Task(parts[1], startT, endT, parts[4], parts[5], priority, completed);

            default:
                throw new InvalidCalendarItemException("Unknown type");
        }
    }
}