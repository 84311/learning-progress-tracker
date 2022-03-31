package tracker.student;

import tracker.courses.*;

import java.util.LinkedHashMap;
import java.util.Map;

public class Student {

    private final int ID;
    private final String name;
    private final String lastName;
    private final String email;

    private final Map<String, Course> courses = new LinkedHashMap<>();

    Student(String name, String lastName, String email, int id) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.ID = id;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public int getID() {
        return ID;
    }

    public String getEmail() {
        return email;
    }

    public Map<String, Course> getCourses() {
        return courses;
    }

    public void addPoints(int java, int dsa, int databases, int spring) {
        Course[] newCourses = new Course[]{
                new JavaCourse(java),
                new DSACourse(dsa),
                new DatabasesCourse(databases),
                new SpringCourse(spring)
        };

        for (Course newCourse : newCourses) {
            addPointsOrCreateCourse(newCourse);
        }
    }

    private void addPointsOrCreateCourse(Course newCourse) {
        courses.merge(newCourse.NAME, newCourse, (oldValue, newValue) -> {
            if (newValue.getPoints() != 0) {
                oldValue.setPoints(newValue.getPoints() + oldValue.getPoints());
            }
            return oldValue;
        });
    }

    @Override
    public int hashCode() {
        int result = ID;
        result = 31 * result + name.hashCode();
        result = 31 * result + lastName.hashCode();
        result = 31 * result + email.hashCode();
        result = 31 * result + courses.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;

        Student student = (Student) o;

        if (ID != student.ID) return false;
        if (!name.equals(student.name)) return false;
        if (!lastName.equals(student.lastName)) return false;
        if (!email.equals(student.email)) return false;
        return courses.equals(student.courses);
    }

    @Override
    public String toString() {
        return String.valueOf(ID);
    }
}