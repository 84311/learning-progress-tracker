package tracker;

import tracker.courses.Course;
import tracker.student.Student;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class CoursesStatistics {

    private CoursesStatistics() {
    }

    public static String getCourseParticipantsAndTheirStats(Map<Integer, Student> studentsList, String courseName) {
        if (!App.allCoursesNames.contains(courseName)) {
            return "Unknown course.";
        }

        List<Student> sortedStudents = getCourseStudentsAndSortByCompletionProgress(studentsList, courseName);

        StringBuilder sb = new StringBuilder().append(courseName).append(System.lineSeparator());
        sb.append(String.format("%-10.10s %-8.8s %-10.10s", "id ", "points ", "completed"))
                .append(System.lineSeparator());

        for (Student student : sortedStudents) {
            Course course = student.getCourses().get(courseName);
            int points = course.getPoints();
            double completedPercent = getCompletedPercent(course, points);
            sb.append(String.format("%-10.10s ", student.getID()))
                    .append(String.format("%-8.8s ", points))
                    .append(String.format("%-10.10s", completedPercent + "%"))
                    .append(System.lineSeparator());
        }
        return sb.toString();
    }

    private static List<Student> getCourseStudentsAndSortByCompletionProgress(Map<Integer,
            Student> studentsList, String courseName) {
        List<Student> courseStudentsList = new ArrayList<>();

        for (Student student : studentsList.values()) {
            Map<String, Course> studentCourses = student.getCourses();
            if (studentCourses.containsKey(courseName) && studentCourses.get(courseName).getPoints() > 0) {
                courseStudentsList.add(student);
            }
        }
        courseStudentsList.sort(Comparator.comparingInt((Student student) ->
                        student.getCourses().get(courseName).getPoints())
                .reversed());

        return courseStudentsList;
    }

    private static double getCompletedPercent(Course course, int points) {
        return BigDecimal.valueOf((double) points / course.MAX_POINTS * 100)
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public static Set<String> getMostPopularCoursesNames(Map<Integer, Student> studentsList) {
        Map<String, Integer> pairs = getCoursesNamesAndTheirParticipantsAmount(studentsList);
        return getKeysWithMaxValues(pairs);
    }

    private static Map<String, Integer> getCoursesNamesAndTheirParticipantsAmount
            (Map<Integer, Student> studentsList) {
        Map<String, Integer> pairs = new HashMap<>();
        for (Student student : studentsList.values()) {
            for (Course course : student.getCourses().values()) {
                if (course.getPoints() != 0) {
                    pairs.merge(course.NAME, 1, Integer::sum);
                }
            }
        }
        return pairs;
    }

    private static <V extends Number> Set<String> getKeysWithMaxValues(Map<String, V> pairs) {
        Set<String> maxNames = new HashSet<>();
        double max = -1;

        for (Map.Entry<String, V> pair : pairs.entrySet()) {
            double value = pair.getValue().doubleValue();
            if (value > max) {
                maxNames = new HashSet<>();
                maxNames.add(pair.getKey());
                max = value;
            } else if (value == max) {
                maxNames.add(pair.getKey());
            }
        }
        return maxNames;
    }

    public static Set<String> getLeastPopularCoursesNames(Map<Integer, Student> studentsList) {
        Map<String, Integer> pairs = getCoursesNamesAndTheirParticipantsAmount(studentsList);
        return getKeysWithMinValues(pairs);
    }

    private static <V extends Number> Set<String> getKeysWithMinValues(Map<String, V> pairs) {
        Set<String> minNames = new HashSet<>();
        double min = Integer.MAX_VALUE;

        for (Map.Entry<String, V> pair : pairs.entrySet()) {
            double value = pair.getValue().doubleValue();
            if (value < min) {
                minNames = new HashSet<>();
                minNames.add(pair.getKey());
                min = value;
            } else if (value == min) {
                minNames.add(pair.getKey());
            }
        }
        return minNames;
    }

    public static Set<String> getHighestActivityCoursesNames(Map<Integer, Student> studentsList) {
        Map<String, Integer> pairs = getCoursesNamesAndTheirCompletedTaskAmount(studentsList);
        return getKeysWithMaxValues(pairs);
    }

    private static Map<String, Integer> getCoursesNamesAndTheirCompletedTaskAmount
            (Map<Integer, Student> studentsList) {
        Map<String, Integer> pairs = new HashMap<>();
        for (Student student : studentsList.values()) {
            for (Course course : student.getCourses().values()) {
                if (course.getCompletedTasksAmount() != 0) {
                    pairs.merge(course.NAME, course.getCompletedTasksAmount(), Integer::sum);
                }
            }
        }
        return pairs;
    }

    public static Set<String> getLowestActivityCoursesNames(Map<Integer, Student> studentsList) {
        Map<String, Integer> pairs = getCoursesNamesAndTheirCompletedTaskAmount(studentsList);
        return getKeysWithMinValues(pairs);
    }

    public static Set<String> getEasiestCoursesNames(Map<Integer, Student> studentsList) {
        Map<String, Double> coursesAndTheirAveragePts = getCoursesAveragePts(studentsList);
        return getKeysWithMaxValues(coursesAndTheirAveragePts);

    }

    private static Map<String, Double> getCoursesAveragePts(Map<Integer, Student> studentsList) {
        Map<String, Double> coursesAndTheirAveragePoints = new HashMap<>(App.allCoursesNames.size());

        for (String courseName : App.allCoursesNames) {
            long pointsSum = 0;
            int studentsAmount = 0;
            for (Student student : studentsList.values()) {
                if (student.getCourses().containsKey(courseName)
                        && student.getCourses().get(courseName).getPoints() > 0) {
                    studentsAmount++;
                    pointsSum += student.getCourses().get(courseName).getPoints();
                }
            }
            double averagePoints = studentsAmount == 0 ? 0 : (double) pointsSum / studentsAmount;
            if (averagePoints != 0) {
                coursesAndTheirAveragePoints.put(courseName, averagePoints);
            }
        }
        return coursesAndTheirAveragePoints;
    }

    public static Set<String> getHardestCoursesNames(Map<Integer, Student> studentsList) {
        Map<String, Double> coursesAndTheirAveragePts = getCoursesAveragePts(studentsList);
        return getKeysWithMinValues(coursesAndTheirAveragePts);
    }
}