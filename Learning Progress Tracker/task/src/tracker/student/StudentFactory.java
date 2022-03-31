package tracker.student;

public class StudentFactory {
    private int nextID = 1_000_000;

    public Student getStudent(String name, String lastName, String email) {
        return new Student(name, lastName, email, this.nextID++);
    }
}