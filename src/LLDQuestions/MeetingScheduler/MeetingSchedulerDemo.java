package LLDQuestions.MeetingScheduler;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main class to demonstrate the Meeting Scheduler system.
 */
public class MeetingSchedulerDemo {
    public static void main(String[] args) {
        Scheduler scheduler = new Scheduler();

        // --- Setup Data ---
        User user1 = new User("U1", "Tushit");
        User user2 = new User("U2", "Ankit");
        User user3 = new User("U3", "Deepak");

        Room room1 = new Room("R1", "Conference Room A");
        Room room2 = new Room("R2", "Conference Room B");

        scheduler.addUser(user1);
        scheduler.addUser(user2);
        scheduler.addUser(user3);
        scheduler.addRoom(room1);
        scheduler.addRoom(room2);

        // --- SCENARIO 1: Successful Booking ---
        System.out.println("----- SCENARIO 1: Tushit books Room A with Ankit -----");
        Date startTime1 = new Date(System.currentTimeMillis() + 1000 * 60 * 60); // 1 hour from now
        Date endTime1 = new Date(System.currentTimeMillis() + 1000 * 60 * 120); // 2 hours from now
        List<User> attendees1 = List.of(user1, user2);
        scheduler.bookMeeting("M1", user1, attendees1, room1, startTime1, endTime1);
        System.out.println();

        // --- SCENARIO 2: Booking conflict with the Room ---
        System.out.println("----- SCENARIO 2: Deepak tries to book the same Room A at an overlapping time -----");
        Date startTime2 = new Date(System.currentTimeMillis() + 1000 * 60 * 90); // 1.5 hours from now
        Date endTime2 = new Date(System.currentTimeMillis() + 1000 * 60 * 150); // 2.5 hours from now
        List<User> attendees2 = List.of(user3);
        scheduler.bookMeeting("M2", user3, attendees2, room1, startTime2, endTime2);
        System.out.println();

        // --- SCENARIO 3: Booking conflict with an Attendee ---
        System.out.println("----- SCENARIO 3: Deepak tries to book Room B but with Ankit, who is already busy -----");
        List<User> attendees3 = List.of(user2, user3); // Ankit (user2) is in the first meeting
        scheduler.bookMeeting("M3", user3, attendees3, room2, startTime2, endTime2);
        System.out.println();
        
        // --- SCENARIO 4: Successful booking in the other room ---
        System.out.println("----- SCENARIO 4: Deepak books Room B with only himself -----");
        List<User> attendees4 = List.of(user3);
        scheduler.bookMeeting("M4", user3, attendees4, room2, startTime2, endTime2);
    }
}

// --- Models ---
class User {
    String id;
    String name;
    public User(String id, String name) { this.id = id; this.name = name; }
    public String getId() { return id; }
    public String getName() { return name; }
}

class Room {
    String id;
    String name;
    public Room(String id, String name) { this.id = id; this.name = name; }
    public String getId() { return id; }
}

class Meeting {
    String id;
    User organizer;
    List<User> attendees;
    Room room;
    Date startTime;
    Date endTime;

    public Meeting(String id, User organizer, List<User> attendees, Room room, Date startTime, Date endTime) {
        this.id = id;
        this.organizer = organizer;
        this.attendees = attendees;
        this.room = room;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Checks if this meeting overlaps with a given time range.
     */
    public boolean hasOverlap(Date otherStart, Date otherEnd) {
        // Overlap exists if (start1 < end2) and (start2 < end1)
        return this.startTime.before(otherEnd) && otherStart.before(this.endTime);
    }
}

// --- The Main Scheduler Service ---
class Scheduler {
    private Map<String, User> users;
    private Map<String, Room> rooms;
    private List<Meeting> meetings;

    public Scheduler() {
        this.users = new HashMap<>();
        this.rooms = new HashMap<>();
        this.meetings = new ArrayList<>();
    }

    public void addUser(User user) { users.put(user.getId(), user); }
    public void addRoom(Room room) { rooms.put(room.getId(), room); }

    public void bookMeeting(String meetingId, User organizer, List<User> attendees, Room room, Date startTime, Date endTime) {
        System.out.println("Attempting to book meeting '" + meetingId + "' from " + startTime + " to " + endTime);

        // 1. Validate inputs
        if (startTime.after(endTime)) {
            System.out.println("Booking failed: Start time must be before end time.");
            return;
        }

        // 2. Check for Room conflicts
        for (Meeting existingMeeting : meetings) {
            if (existingMeeting.room.getId().equals(room.getId()) && existingMeeting.hasOverlap(startTime, endTime)) {
                System.out.println("Booking failed: Room '" + room.name + "' is already booked at this time.");
                return;
            }
        }

        // 3. Check for Attendee conflicts
        for (User attendee : attendees) {
            for (Meeting existingMeeting : meetings) {
                // Check if the attendee is in any other meeting that overlaps
                if (existingMeeting.attendees.stream().anyMatch(a -> a.getId().equals(attendee.getId())) && existingMeeting.hasOverlap(startTime, endTime)) {
                    System.out.println("Booking failed: Attendee '" + attendee.getName() + "' is unavailable at this time.");
                    return;
                }
            }
        }

        // 4. If no conflicts, create and add the meeting
        Meeting newMeeting = new Meeting(meetingId, organizer, attendees, room, startTime, endTime);
        meetings.add(newMeeting);
        System.out.println("Success! Meeting '" + meetingId + "' booked in Room '" + room.name + "'.");
        
        // This is where you would notify observers (attendees)
        notifyAttendees(newMeeting);
    }
    
    // Observer Pattern hook
    private void notifyAttendees(Meeting meeting) {
        System.out.print("  Notifying attendees: ");
        for(User attendee : meeting.attendees) {
            System.out.print(attendee.getName() + " ");
        }
        System.out.println();
    }
}

