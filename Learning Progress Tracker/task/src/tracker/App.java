package tracker;

import tracker.courses.Course;
import tracker.student.Student;
import tracker.student.StudentFactory;

import java.util.*;

public class App {
    public static final Set<String> allCoursesNames = Set.of("Java", "DSA", "Databases", "Spring");

    private final Map<Integer, Student> studentsList = new LinkedHashMap<>();
    private final StudentFactory studentFactory = new StudentFactory();

    private final Scanner scanner = new Scanner(System.in);

    private boolean exit = false;

    public void init() {
        System.out.println("Learning Progress Tracker");

        while (!exit) {
            String command = scanInput();
            switch (command) {
                case "":
                    System.out.println("no input.");
                    break;
                case "add students":
                    addStudentsSession();
                    break;
                case "back":
                    System.out.println("Enter 'exit' to exit the program.");
                    break;
                case "exit":
                    exit = true;
                    break;
                case "list":
                    printStudentsList();
                    break;
                case "add points":
                    addPointsSession();
                    break;
                case "find":
                    printStudentDetailsSession();
                    break;
                case "statistics":
                    statisticsSession();
                    break;
                case "notify":
                    notifyStudentsWhoFinishedCourses();
                    break;
                default:
                    System.out.println("Unknown command");
                    break;
            }
        }
        System.out.println("Bye!");
    }

    private void addStudentsSession() {
        int addedStudents = 0;

        System.out.println("Enter student credentials or 'back' to return:");

        while (true) {
            String command = scanInput();
            if (command.equals("back")) {
                System.out.printf("Total %d students have been added.%n", addedStudents);
                return;
            } else if (tryToAddStudent(command)) {
                addedStudents++;
            }
        }
    }

    private boolean tryToAddStudent(String studentCredentials) {
        try {
            validateStudentCredentials(studentCredentials);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return false;
        }

        String name = studentCredentials.substring(0, studentCredentials.indexOf(" "));
        String lastName = studentCredentials.substring(studentCredentials.indexOf(" ") + 1, studentCredentials.lastIndexOf(" "));
        String email = studentCredentials.substring(studentCredentials.lastIndexOf(" ") + 1);

        Student student = studentFactory.getStudent(name, lastName, email);
        studentsList.put(student.getID(), student);
        System.out.println("The student has been added");

        return true;
    }

    private void validateStudentCredentials(String studentCredentials) throws IllegalArgumentException {
        if (studentCredentials.split(" ").length < 3) {
            throw new IllegalArgumentException("Incorrect credentials.");
        }

        String name = studentCredentials.substring(0, studentCredentials.indexOf(" "));
        String lastName = studentCredentials.substring(studentCredentials.indexOf(" ") + 1, studentCredentials.lastIndexOf(" "));
        String email = studentCredentials.substring(studentCredentials.lastIndexOf(" ") + 1);

        if (!isNameCorrect(name)) {
            throw new IllegalArgumentException("Incorrect first name.");
        } else if (!isNameCorrect(lastName)) {
            throw new IllegalArgumentException("Incorrect last name.");
        } else if (!isEmailCorrect(email)) {
            throw new IllegalArgumentException("Incorrect email.");
        } else if (isEmailTaken(email)) {
            throw new IllegalArgumentException("This email is already taken.");
        }
    }

    private boolean isNameCorrect(String name) {
        name = name.replace(" ", "");

        if (!name.matches("\\b[A-Za-z]+[A-Za-z-']+") || name.matches(".*[-'][-'].*")) {
            return false;
        }

        return Character.isLetter(name.charAt(name.length() - 1));
    }

    private boolean isEmailCorrect(String email) {
        return email.matches("([^@])+@([^@.])+\\.([^@.])+");
    }

    private boolean isEmailTaken(String email) {
        for (Student student : studentsList.values()) {
            if (student.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    private void printStudentsList() {
        if (studentsList.isEmpty()) {
            System.out.println("No students found");
            return;
        }

        System.out.println("Students:");
        for (Student student : studentsList.values()) {
            System.out.println(student.getID());
        }
    }

    private void addPointsSession() {
        System.out.println("Enter an id and points or 'back' to return:");

        while (true) {
            String command = scanInput();
            if (command.equals("back")) {
                return;
            } else {
                tryToAddPoints(command);
            }
        }
    }

    private void tryToAddPoints(String command) {
        String[] input = command.split(" ");
        String candidateForID = input[0];
        Student targetStudent = findStudent(candidateForID);

        if (targetStudent == null) {
            System.out.printf("No student is found for id=%s %n", candidateForID);
            return;
        }

        String[] singlePoints = Arrays.copyOfRange(input, 1, input.length);

        if (isPointsFormatValid(singlePoints)) {
            addPoints(singlePoints, targetStudent);
            System.out.println("Points updated");
        } else {
            System.out.println("Incorrect points format");
        }
    }

    private Student findStudent(String candidateForID) {
        if (isInteger(candidateForID)) {
            return studentsList.get(Integer.parseInt(candidateForID));
        }
        return null;
    }

    private boolean isPointsFormatValid(String[] singlePoints) {

        if (singlePoints.length != 4) {
            return false;
        }

        for (String singlePoint : singlePoints) {
            if (!isInteger(singlePoint) || Integer.parseInt(singlePoint) < 0) {
                return false;
            }
        }
        return true;
    }

    private boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void addPoints(String[] singlePoints, Student targetStudent) {
        targetStudent.addPoints(
                Integer.parseInt(singlePoints[0]),
                Integer.parseInt(singlePoints[1]),
                Integer.parseInt(singlePoints[2]),
                Integer.parseInt(singlePoints[3])
        );
    }

    private void printStudentDetailsSession() {
        System.out.println("Enter an id or 'back' to return:");

        while (true) {
            String command = scanInput();
            if (command.equals("back")) {
                return;
            } else {
                Student targetStudent = findStudent(command);
                if (targetStudent != null) {
                    printStudentPoints(targetStudent);
                } else {
                    System.out.println("No student is found for id=" + command);
                }
            }
        }
    }

    private void printStudentPoints(Student student) {
        StringBuilder sb = new StringBuilder(student.getID() + " points: ");
        for (Course course : student.getCourses().values()) {
            sb.append(course.NAME).append("=").append(course.getPoints()).append(" ");
        }
        System.out.println(sb.toString().trim());
    }

    private void statisticsSession() {
        System.out.println("Type the name of a course to see details or 'back' to quit");
        printStats();

        while (true) {
            String command = scanInput();
            if (command.equals("back")) {
                return;
            } else {
                System.out.print(CoursesStatistics.getCourseParticipantsAndTheirStats(studentsList, command));
            }
        }
    }

    private void printStats() {
        Set<String> mostPopularCoursesNames = CoursesStatistics.getMostPopularCoursesNames(studentsList);
        Set<String> leastPopularCoursesNames = CoursesStatistics.getLeastPopularCoursesNames(studentsList);
        Set<String> highestActivityCoursesNames = CoursesStatistics.getHighestActivityCoursesNames(studentsList);
        Set<String> lowestActivityCoursesNames = CoursesStatistics.getLowestActivityCoursesNames(studentsList);
        Set<String> easiesCoursesNames = CoursesStatistics.getEasiestCoursesNames(studentsList);
        Set<String> hardestCoursesNames = CoursesStatistics.getHardestCoursesNames(studentsList);

        String mostPopularCourses = String.join(", ", mostPopularCoursesNames);
        System.out.println("Most popular: " + (mostPopularCourses.isEmpty() ? "n/a" : mostPopularCourses));

        leastPopularCoursesNames.removeAll(mostPopularCoursesNames);
        String leastPopularCourses = String.join(", ", leastPopularCoursesNames);
        System.out.println("Least popular: " + (leastPopularCourses.isEmpty() ? "n/a" : leastPopularCoursesNames));

        String highestActivityCourses = String.join(", ", highestActivityCoursesNames);
        System.out.println("Highest activity: " + (highestActivityCourses.isEmpty() ? "n/a" : highestActivityCourses));

        lowestActivityCoursesNames.removeAll(highestActivityCoursesNames);
        String lowestActivityCourses = String.join(", ", lowestActivityCoursesNames);
        System.out.println("Lowest activity: " + (lowestActivityCourses.isEmpty() ? "n/a" : lowestActivityCourses));

        String easiestCourses = String.join(", ", easiesCoursesNames);
        System.out.println("Easiest course: " + (easiestCourses.isEmpty() ? "n/a" : easiestCourses));

        hardestCoursesNames.removeAll(easiesCoursesNames);
        String hardestCourses = String.join(", ", hardestCoursesNames);
        System.out.println("Hardest course: " + (hardestCourses.isEmpty() ? "n/a" : hardestCourses));
    }

    private void notifyStudentsWhoFinishedCourses() {
        int notifiedStudentsCounter = 0;

        for (Student student : studentsList.values()) {
            boolean notify = false;

            for (Course course : student.getCourses().values()) {
                if (course.isFinished() && !course.isNotified()) {
                    printNotify(student, course);
                    course.setNotified(true);
                    notify = true;
                }
            }

            notifiedStudentsCounter += notify ? 1 : 0;
        }
        System.out.printf("Total %d students have been notified.%n", notifiedStudentsCounter);
    }

    private void printNotify(Student student, Course course) {
        System.out.println("To: " + student.getEmail());
        System.out.println("Re: Your Learning Progress");
        System.out.printf("Hello, %s %s! You have accomplished our %s course!%n",
                student.getName(), student.getLastName(), course);
    }

    private String scanInput() {
        return scanner.nextLine().trim();
    }
}