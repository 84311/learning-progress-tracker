package tracker.mytests;

import manifold.ext.rt.api.Jailbreak;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tracker.App;
import tracker.student.Student;
import tracker.student.StudentFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class AppTest {
    private final StudentFactory studentFactory = new StudentFactory();
    @Jailbreak App app = new App();

    static List<String> createStudentsWithCorrectCredentials() {
        return List.of(
                "John Smith jsmith@hotmail.com", "Anny Doolittle anny.md@mail.edu",
                "Jean-Claude O'Connor jcda123@google.net", "Mary Emelianenko 125367at@zzz90.z9",
                "Al Owen u15da125@a1s2f4f7.a1c2c5s4", "Robert Jemison Van de Graaff robertvdgraaff@mit.edu",
                "Ed Eden a1@a1.a1", "na'me s-u ii@ii.ii", "n'a me su aa-b'b ab@ab.ab", "nA me 1@1.1"
        );
    }

    static List<Arguments> createStudentsWithIncorrectCredentialsAndExpectedMsg() {
        return List.of(
                arguments("", "Incorrect credentials."),
                arguments(" ", "Incorrect credentials."),
                arguments("Incorrect credentials", "Incorrect credentials."),
                arguments("John Smith", "Incorrect credentials."),
                arguments("- surname email@email.xyz", "Incorrect first name."),
                arguments("' surname email@email.xyz", "Incorrect first name."),
                arguments("n surname email@email.xyz", "Incorrect first name."),
                arguments("'n surname email@email.xyz", "Incorrect first name."),
                arguments("-n surname email@email.xyz", "Incorrect first name."),
                arguments("n- surname email@email.xyz", "Incorrect first name."),
                arguments("n' surname email@email.xyz", "Incorrect first name."),
                arguments("nam-'e surname email@email.xyz", "Incorrect first name."),
                arguments("nam'-e surname email@email.xyz", "Incorrect first name."),
                arguments("nam--e surname email@email.xyz", "Incorrect first name."),
                arguments("nam''e surname email@email.xyz", "Incorrect first name."),
                arguments("name - email@email.xyz", "Incorrect last name."),
                arguments("name ' email@email.xyz", "Incorrect last name."),
                arguments("name -s email@email.xyz", "Incorrect last name."),
                arguments("name s- email@email.xyz", "Incorrect last name."),
                arguments("name surn--ame email@email.xyz", "Incorrect last name."),
                arguments("name surn''ame email@email.xyz", "Incorrect last name."),
                arguments("name surn'-ame email@email.xyz", "Incorrect last name."),
                arguments("name surn-'ame email@email.xyz", "Incorrect last name."),
                arguments("name surname e", "Incorrect email."),
                arguments("name surname email.xyz", "Incorrect email."),
                arguments("name surname email@emailxyz", "Incorrect email."),
                arguments("name surname email@emai.l.xyz", "Incorrect email."),
                arguments("name surname email@e@email.xyz", "Incorrect email.")
        );
    }

    @ParameterizedTest
    @MethodSource("createStudentsWithCorrectCredentials")
    void tryToAddStudent_correctStudentsCredentials_True(String studentCredentials) {
        assertTrue(app.tryToAddStudent(studentCredentials));
    }

    @ParameterizedTest
    @MethodSource("createStudentsWithIncorrectCredentialsAndExpectedMsg")
    void validateStudentCredentials_InvalidStudentsCredentials_ThrowsIllegalArgumentException
            (String studentCredentials, String expectedExceptionMessage) {

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                app.validateStudentCredentials(studentCredentials));

        assertEquals(expectedExceptionMessage, exception.getMessage());
    }

    @Test
    void validateStudentCredentials_EmailTaken_ThrowsIllegalArgumentException() {
        app.tryToAddStudent("name surname email@email.xyz");

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                app.validateStudentCredentials("name surname email@email.xyz"));

        assertEquals("This email is already taken.", exception.getMessage());
    }

    @Test
    void tryToAddPoints_AddFirstPointsProperly_PointsAdded() {
        Student student = studentFactory.getStudent("John", "Smith", "john@john.john");
        app.studentsList.put(1_000_000, student);

        app.tryToAddPoints("1000000 500 500 500 500");

        assertAll(
                () -> assertEquals(500, student.getCourses().get("Java").getPoints()),
                () -> assertEquals(500, student.getCourses().get("DSA").getPoints()),
                () -> assertEquals(500, student.getCourses().get("Databases").getPoints()),
                () -> assertEquals(500, student.getCourses().get("Spring").getPoints())
        );
    }

    @Test
    void tryToAddPoints_AddPointsSecondTimeProperly_PointsMerged() {
        Student student = studentFactory.getStudent("John", "Smith", "john@john.john");
        app.studentsList.put(1_000_000, student);

        app.tryToAddPoints("1000000 500 500 500 500");
        app.tryToAddPoints("1000000 500 500 500 500");

        assertAll(
                () -> assertEquals(1000, student.getCourses().get("Java").getPoints()),
                () -> assertEquals(1000, student.getCourses().get("DSA").getPoints()),
                () -> assertEquals(1000, student.getCourses().get("Databases").getPoints()),
                () -> assertEquals(1000, student.getCourses().get("Spring").getPoints())
        );
    }

    @Test
    void tryToAddPoints_isEachCourseFinished_True() {
        Student student = studentFactory.getStudent("John", "Smith", "john@john.john");
        app.studentsList.put(1_000_000, student);

        app.tryToAddPoints("1000000 600 400 480 550");

        assertAll(
                () -> assertTrue(student.getCourses().get("Java").isFinished()),
                () -> assertTrue(student.getCourses().get("DSA").isFinished()),
                () -> assertTrue(student.getCourses().get("Databases").isFinished()),
                () -> assertTrue(student.getCourses().get("Spring").isFinished())
        );
    }
}