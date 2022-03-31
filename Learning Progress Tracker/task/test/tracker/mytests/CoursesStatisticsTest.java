package tracker.mytests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import tracker.CoursesStatistics;
import tracker.student.Student;
import tracker.student.StudentFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CoursesStatisticsTest {
    private final StudentFactory studentFactory = new StudentFactory();
    private Map<Integer, Student> studentsList = new LinkedHashMap<>();

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "Unknown"})
    void getCourseParticipantsAndTheirStats_ProvideUnknownCourses_ReturnUnknownCourseStr(String courseName) {
        String actual = CoursesStatistics.getCourseParticipantsAndTheirStats(studentsList, courseName);
        assertEquals("Unknown course.", actual);
    }

    @Test
    void getCourseParticipantsAndTheirStats_ExistingCourseAndStudents_JavaStudentsSortedByCompletionProgress
            () {
        createAndAddRandomStudentsToList(4);
        addCoursesToStudents();

        String[] actualLines = CoursesStatistics.getCourseParticipantsAndTheirStats(studentsList, "Java")
                .split(System.lineSeparator());

        assertAll(
                () -> assertTrue(actualLines[0].matches("Java.*")),
                () -> assertTrue(actualLines[1].matches("id\\s+points\\s+completed.*")),
                () -> assertTrue(actualLines[2].matches("1000003\\s+600\\s+100\\.0\\s?%.*")),
                () -> assertTrue(actualLines[3].matches("1000000\\s+500\\s+83\\.3\\s?%.*")),
                () -> assertTrue(actualLines[4].matches("1000002\\s+400\\s+66\\.7\\s?%.*")),
                () -> assertTrue(actualLines[5].matches("1000001\\s+80\\s+13\\.3\\s?%.*"))
        );
    }

    private void createAndAddRandomStudentsToList(int studentsAmount) {
        studentsList = new LinkedHashMap<>();
        for (int i = 0; i < studentsAmount; i++) {
            studentsList.put(1_000_000 + i, getStudentWithRandomValidCredentials());
        }
    }

    private void addCoursesToStudents() {
        studentsList.get(1_000_000).addPoints(500, 200, 250, 550);
        studentsList.get(1_000_001).addPoints(80, 400, 400, 150);
        studentsList.get(1_000_002).addPoints(400, 100, 300, 400);
        studentsList.get(1_000_003).addPoints(600, 120, 350, 200);
    }

    private Student getStudentWithRandomValidCredentials() {
        StringBuilder name = new StringBuilder();
        for (int i = 0; i < ThreadLocalRandom.current().nextInt(3, 8 + 1); i++) {
            name.append((char) ThreadLocalRandom.current().nextInt(97, 122 + 1));
        }

        StringBuilder lastName = new StringBuilder();
        for (int i = 0; i < ThreadLocalRandom.current().nextInt(3, 8 + 1); i++) {
            lastName.append((char) ThreadLocalRandom.current().nextInt(97, 122 + 1));
        }

        StringBuilder email = new StringBuilder();
        for (int i = 0; i < ThreadLocalRandom.current().nextInt(3, 8 + 1); i++) {
            email.append((char) ThreadLocalRandom.current().nextInt(97, 122 + 1));
        }
        email.append("@email.xyz");

        return studentFactory.getStudent(name.toString(), lastName.toString(), email.toString());
    }

    @Test
    void getCourseParticipantsAndTheirStats_ExistingCourseAndStudents_DSAStudentsSortedByCompletionProgress
            () {
        createAndAddRandomStudentsToList(4);
        addCoursesToStudents();

        String[] actualLines = CoursesStatistics.getCourseParticipantsAndTheirStats(studentsList, "DSA")
                .split(System.lineSeparator());

        assertAll(
                () -> assertTrue(actualLines[0].matches("DSA.*")),
                () -> assertTrue(actualLines[1].matches("id\\s+points\\s+completed.*")),
                () -> assertTrue(actualLines[2].matches("1000001\\s+400\\s+100\\.0\\s?%.*")),
                () -> assertTrue(actualLines[3].matches("1000000\\s+200\\s+50\\.0\\s?%.*")),
                () -> assertTrue(actualLines[4].matches("1000003\\s+120\\s+30\\.0\\s?%.*")),
                () -> assertTrue(actualLines[5].matches("1000002\\s+100\\s+25\\.0\\s?%.*"))
        );
    }

    @Test
    void getCourseParticipantsAndTheirStats_ExistingCourseAndStudents_DatabasesStudentsSortedByCompletionProgress
            () {
        createAndAddRandomStudentsToList(4);
        addCoursesToStudents();

        String[] actualLines = CoursesStatistics.getCourseParticipantsAndTheirStats(studentsList, "Databases")
                .split(System.lineSeparator());

        assertAll(
                () -> assertTrue(actualLines[0].matches("Databases.*")),
                () -> assertTrue(actualLines[1].matches("id\\s+points\\s+completed.*")),
                () -> assertTrue(actualLines[2].matches("1000001\\s+400\\s+83\\.3\\s?%.*")),
                () -> assertTrue(actualLines[3].matches("1000003\\s+350\\s+72\\.9\\s?%.*")),
                () -> assertTrue(actualLines[4].matches("1000002\\s+300\\s+62\\.5\\s?%.*")),
                () -> assertTrue(actualLines[5].matches("1000000\\s+250\\s+52\\.1\\s?%.*"))
        );
    }

    @Test
    void getCourseParticipantsAndTheirStats_ExistingCourseAndStudents_SpringStudentsSortedByCompletionProgress
            () {
        createAndAddRandomStudentsToList(4);
        addCoursesToStudents();

        String[] actualLines = CoursesStatistics.getCourseParticipantsAndTheirStats(studentsList, "Spring")
                .split(System.lineSeparator());

        assertAll(
                () -> assertTrue(actualLines[0].matches("Spring.*")),
                () -> assertTrue(actualLines[1].matches("id\\s+points\\s+completed.*")),
                () -> assertTrue(actualLines[2].matches("1000000\\s+550\\s+100\\.0\\s?%.*")),
                () -> assertTrue(actualLines[3].matches("1000002\\s+400\\s+72\\.7\\s?%.*")),
                () -> assertTrue(actualLines[4].matches("1000003\\s+200\\s+36\\.4\\s?%.*")),
                () -> assertTrue(actualLines[5].matches("1000001\\s+150\\s+27\\.3\\s?%.*"))
        );
    }

    @Test
    void getMostPopularCoursesNames_EmptyStudentsList_EmptySet() {
        Set<String> actualResult = CoursesStatistics.getMostPopularCoursesNames(studentsList);
        assertTrue(actualResult.isEmpty());
    }

    @Test
    void getMostPopularCoursesNames_StudentsListWithoutCourses_EmptySet() {
        createAndAddRandomStudentsToList(5);

        Set<String> actualResult = CoursesStatistics.getMostPopularCoursesNames(studentsList);
        assertTrue(actualResult.isEmpty());
    }

    @Test
    void getMostPopularCoursesNames_StudentsWith0Points_EmptySet() {
        createAndAddRandomStudentsToList(5);
        for (Student student : studentsList.values()) {
            student.addPoints(0, 0, 0, 0);
        }

        Set<String> actualResult = CoursesStatistics.getMostPopularCoursesNames(studentsList);
        assertTrue(actualResult.isEmpty());
    }

    @Test
    void getMostPopularCoursesNames_JavaMostPopular_SetContainsOnlyJava() {
        createAndAddRandomStudentsToList(5);
        for (Student student : studentsList.values()) {
            student.addPoints(1, 0, 0, 0);
        }
        studentsList.get(1_000_002).addPoints(0, 100, 100, 100);
        studentsList.get(1_000_003).addPoints(0, 100, 0, 0);

        Set<String> actualResult = CoursesStatistics.getMostPopularCoursesNames(studentsList);
        assertThat(actualResult).containsExactly("Java");
    }

    @Test
    void getMostPopularCoursesNames_SpringMostPopular_SetContainsOnlySpring() {
        createAndAddRandomStudentsToList(5);
        for (Student student : studentsList.values()) {
            student.addPoints(0, 0, 0, 1);
        }
        studentsList.get(1_000_002).addPoints(100, 100, 100, 0);
        studentsList.get(1_000_003).addPoints(100, 100, 0, 0);

        Set<String> actualResult = CoursesStatistics.getMostPopularCoursesNames(studentsList);
        assertThat(actualResult).containsExactly("Spring");
    }

    @Test
    void getMostPopularCoursesNames_DSAAndDatabasesMostPopular_SetContainsOnlyDSAAndDatabases() {
        createAndAddRandomStudentsToList(5);
        for (Student student : studentsList.values()) {
            student.addPoints(0, 1, 2, 0);
        }
        studentsList.get(1_000_002).addPoints(100, 0, 100, 0);
        studentsList.get(1_000_003).addPoints(100, 0, 0, 0);

        Set<String> actualResult = CoursesStatistics.getMostPopularCoursesNames(studentsList);
        assertThat(actualResult).containsExactlyInAnyOrder("DSA", "Databases");
    }

    @Test
    void getMostPopularCoursesNames_AllMostPopular_SetContainsAllCourses() {
        createAndAddRandomStudentsToList(5);
        for (Student student : studentsList.values()) {
            student.addPoints(1, 2, 3, 4);
        }

        Set<String> actualResult = CoursesStatistics.getMostPopularCoursesNames(studentsList);
        assertThat(actualResult).containsExactlyInAnyOrder("Java", "DSA", "Databases", "Spring");
    }

    @Test
    void getLeastPopularCoursesNames_EmptyStudentsList_EmptySet() {
        Set<String> actualResult = CoursesStatistics.getLeastPopularCoursesNames(studentsList);
        assertTrue(actualResult.isEmpty());
    }

    @Test
    void getLeastPopularCoursesNames_StudentsListWithoutCourses_EmptySet() {
        createAndAddRandomStudentsToList(5);

        Set<String> actualResult = CoursesStatistics.getLeastPopularCoursesNames(studentsList);
        assertTrue(actualResult.isEmpty());
    }

    @Test
    void getLeastPopularCoursesNames_StudentsWith0Points_EmptySet() {
        createAndAddRandomStudentsToList(5);
        for (Student student : studentsList.values()) {
            student.addPoints(0, 0, 0, 0);
        }

        Set<String> actualResult = CoursesStatistics.getLeastPopularCoursesNames(studentsList);
        assertTrue(actualResult.isEmpty());
    }

    @Test
    void getLeastPopularCoursesNames_JavaLeastPopular_SetContainsOnlyJava() {
        createAndAddRandomStudentsToList(5);
        for (Student student : studentsList.values()) {
            student.addPoints(0, 2, 3, 4);
        }
        studentsList.get(1_000_002).addPoints(1, 1, 1, 1);
        studentsList.get(1_000_003).addPoints(1, 1, 1, 1);

        Set<String> actualResult = CoursesStatistics.getLeastPopularCoursesNames(studentsList);
        assertThat(actualResult).containsExactly("Java");
    }

    @Test
    void getLeastPopularCoursesNames_SpringLeastPopular_SetContainsOnlySpring() {
        createAndAddRandomStudentsToList(5);
        for (Student student : studentsList.values()) {
            student.addPoints(1, 2, 3, 0);
        }
        studentsList.get(1_000_002).addPoints(1, 1, 1, 1);
        studentsList.get(1_000_003).addPoints(1, 1, 1, 1);

        Set<String> actualResult = CoursesStatistics.getLeastPopularCoursesNames(studentsList);
        assertThat(actualResult).containsExactly("Spring");
    }

    @Test
    void getLeastPopularCoursesNames_DSAAndDatabasesLeastPopular_SetContainsOnlyDSAAndDatabases() {
        createAndAddRandomStudentsToList(5);
        for (Student student : studentsList.values()) {
            student.addPoints(1, 0, 0, 1);
        }
        studentsList.get(1_000_002).addPoints(1, 1, 1, 1);
        studentsList.get(1_000_003).addPoints(1, 1, 1, 1);

        Set<String> actualResult = CoursesStatistics.getLeastPopularCoursesNames(studentsList);
        assertThat(actualResult).containsExactlyInAnyOrder("DSA", "Databases");
    }

    @Test
    void getHighestActivityCoursesNames_EmptyStudentsList_EmptySet() {
        Set<String> actualResult = CoursesStatistics.getHighestActivityCoursesNames(studentsList);
        assertTrue(actualResult.isEmpty());
    }

    @Test
    void getHighestActivityCoursesNames_StudentsListWithoutCourses_EmptySet() {
        createAndAddRandomStudentsToList(5);

        Set<String> actualResult = CoursesStatistics.getHighestActivityCoursesNames(studentsList);
        assertTrue(actualResult.isEmpty());
    }

    @Test
    void getHighestActivityCoursesNames_StudentsWith0Points_EmptySet() {
        createAndAddRandomStudentsToList(5);
        for (Student student : studentsList.values()) {
            student.addPoints(0, 0, 0, 0);
        }

        Set<String> actualResult = CoursesStatistics.getHighestActivityCoursesNames(studentsList);
        assertTrue(actualResult.isEmpty());
    }

    @Test
    void getHighestActivityCoursesNames_DatabasesHighestActivity_SetContainsOnlyDatabases() {
        createAndAddRandomStudentsToList(5);
        for (Student student : studentsList.values()) {
            student.addPoints(1, 2, 3, 4);
        }
        studentsList.get(1_000_002).addPoints(0, 0, 1, 0);
        studentsList.get(1_000_003).addPoints(1, 1, 1, 1);

        Set<String> actualResult = CoursesStatistics.getHighestActivityCoursesNames(studentsList);
        assertThat(actualResult).containsExactly("Databases");
    }

    @Test
    void getHighestActivityCoursesNames_SpringHighestActivity_SetContainsOnlySpring() {
        createAndAddRandomStudentsToList(5);
        for (Student student : studentsList.values()) {
            student.addPoints(1, 2, 3, 4);
        }
        studentsList.get(1_000_002).addPoints(0, 0, 0, 1);
        studentsList.get(1_000_003).addPoints(1, 1, 1, 1);

        Set<String> actualResult = CoursesStatistics.getHighestActivityCoursesNames(studentsList);
        assertThat(actualResult).containsExactly("Spring");
    }

    @Test
    void getHighestActivityCoursesNames_JavaAndSpringHighestActivity_SetContainsOnlyJavaAndSpring() {
        createAndAddRandomStudentsToList(5);
        for (Student student : studentsList.values()) {
            student.addPoints(1, 0, 0, 1);
        }
        studentsList.get(1_000_002).addPoints(1, 0, 0, 1);
        studentsList.get(1_000_003).addPoints(1, 1, 1, 1);

        Set<String> actualResult = CoursesStatistics.getHighestActivityCoursesNames(studentsList);
        assertThat(actualResult).containsExactlyInAnyOrder("Java", "Spring");
    }

    @Test
    void getLowestActivityCoursesNames_EmptyStudentsList_EmptySet() {
        Set<String> actualResult = CoursesStatistics.getLowestActivityCoursesNames(studentsList);
        assertTrue(actualResult.isEmpty());
    }

    @Test
    void getLowestActivityCoursesNames_StudentsListWithoutCourses_EmptySet() {
        createAndAddRandomStudentsToList(5);

        Set<String> actualResult = CoursesStatistics.getLowestActivityCoursesNames(studentsList);
        assertTrue(actualResult.isEmpty());
    }

    @Test
    void getLowestActivityCoursesNames_StudentsWith0Points_EmptySet() {
        createAndAddRandomStudentsToList(5);
        for (Student student : studentsList.values()) {
            student.addPoints(0, 0, 0, 0);
        }

        Set<String> actualResult = CoursesStatistics.getLowestActivityCoursesNames(studentsList);
        assertTrue(actualResult.isEmpty());
    }

    @Test
    void getLowestActivityCoursesNames_SpringLowestActivity_SetContainsOnlySpring() {
        createAndAddRandomStudentsToList(5);
        for (Student student : studentsList.values()) {
            student.addPoints(1, 2, 3, 0);
        }
        studentsList.get(1_000_002).addPoints(0, 1, 1, 0);
        studentsList.get(1_000_003).addPoints(1, 1, 0, 1);

        Set<String> actualResult = CoursesStatistics.getLowestActivityCoursesNames(studentsList);
        assertThat(actualResult).containsExactly("Spring");
    }

    @Test
    void getLowestActivityCoursesNames_DSALowestActivity_SetContainsOnlyDSA() {
        createAndAddRandomStudentsToList(5);
        for (Student student : studentsList.values()) {
            student.addPoints(1, 0, 3, 4);
        }
        studentsList.get(1_000_002).addPoints(0, 1, 1, 1);
        studentsList.get(1_000_003).addPoints(0, 1, 0, 1);

        Set<String> actualResult = CoursesStatistics.getLowestActivityCoursesNames(studentsList);
        assertThat(actualResult).containsExactly("DSA");
    }

    @Test
    void getLowestActivityCoursesNames_JavaAndSpringLowestActivity_SetContainsOnlyJavaAndSpring() {
        createAndAddRandomStudentsToList(5);
        for (Student student : studentsList.values()) {
            student.addPoints(0, 1, 1, 0);
        }
        studentsList.get(1_000_002).addPoints(1, 0, 0, 1);
        studentsList.get(1_000_003).addPoints(1, 1, 1, 1);

        Set<String> actualResult = CoursesStatistics.getLowestActivityCoursesNames(studentsList);
        assertThat(actualResult).containsExactlyInAnyOrder("Java", "Spring");
    }

    @Test
    void getEasiestCoursesNames_EmptyStudentsList_EmptySet() {
        Set<String> actualResult = CoursesStatistics.getEasiestCoursesNames(studentsList);
        assertTrue(actualResult.isEmpty());
    }

    @Test
    void getEasiestCoursesNames_StudentsListWithoutCourses_EmptySet() {
        createAndAddRandomStudentsToList(5);

        Set<String> actualResult = CoursesStatistics.getEasiestCoursesNames(studentsList);
        assertTrue(actualResult.isEmpty());
    }

    @Test
    void getEasiestCoursesNames_StudentsWith0Points_EmptySet() {
        createAndAddRandomStudentsToList(5);
        for (Student student : studentsList.values()) {
            student.addPoints(0, 0, 0, 0);
        }

        Set<String> actualResult = CoursesStatistics.getEasiestCoursesNames(studentsList);
        assertTrue(actualResult.isEmpty());
    }

    @Test
    void getEasiestCoursesNames_DSAEasiestCourse_SetContainsOnlyDSA() {
        createAndAddRandomStudentsToList(5);
        for (Student student : studentsList.values()) {
            student.addPoints(1, 20, 3, 4);
        }
        studentsList.get(1_000_002).addPoints(1, 0, 1, 0);
        studentsList.get(1_000_003).addPoints(1, 0, 1, 1);

        Set<String> actualResult = CoursesStatistics.getEasiestCoursesNames(studentsList);
        assertThat(actualResult).containsExactly("DSA");
    }

    @Test
    void getEasiestCoursesNames_JavaEasiestCourse_SetContainsOnlyJava() {
        createAndAddRandomStudentsToList(5);
        for (Student student : studentsList.values()) {
            student.addPoints(20, 1, 3, 4);
        }
        studentsList.get(1_000_002).addPoints(0, 1, 1, 1);
        studentsList.get(1_000_003).addPoints(0, 1, 1, 1);

        Set<String> actualResult = CoursesStatistics.getEasiestCoursesNames(studentsList);
        assertThat(actualResult).containsExactly("Java");
    }

    @Test
    void getEasiestCoursesNames_DatabasesAndSpringEasiestCourses_SetContainsOnlyDatabasesAndSpring() {
        createAndAddRandomStudentsToList(5);
        for (Student student : studentsList.values()) {
            student.addPoints(1, 1, 20, 20);
        }
        studentsList.get(1_000_002).addPoints(1, 1, 20, 1);
        studentsList.get(1_000_003).addPoints(1, 1, 1, 20);

        Set<String> actualResult = CoursesStatistics.getEasiestCoursesNames(studentsList);
        assertThat(actualResult).containsExactlyInAnyOrder("Databases", "Spring");
    }

    @Test
    void getHardestCoursesNames_EmptyStudentsList_EmptySet() {
        Set<String> actualResult = CoursesStatistics.getHardestCoursesNames(studentsList);
        assertTrue(actualResult.isEmpty());
    }

    @Test
    void getHardestCoursesNames_StudentsListWithoutCourses_EmptySet() {
        createAndAddRandomStudentsToList(5);

        Set<String> actualResult = CoursesStatistics.getHardestCoursesNames(studentsList);
        assertTrue(actualResult.isEmpty());
    }

    @Test
    void getHardestCoursesNames_StudentsWith0Points_EmptySet() {
        createAndAddRandomStudentsToList(5);
        for (Student student : studentsList.values()) {
            student.addPoints(0, 0, 0, 0);
        }

        Set<String> actualResult = CoursesStatistics.getHardestCoursesNames(studentsList);
        assertTrue(actualResult.isEmpty());
    }

    @Test
    void getHardestCoursesNames_DSAHardestCourse_SetContainsOnlyDSA() {
        createAndAddRandomStudentsToList(5);
        for (Student student : studentsList.values()) {
            student.addPoints(10, 1, 10, 1);
        }
        studentsList.get(1_000_002).addPoints(1, 0, 1, 1);
        studentsList.get(1_000_003).addPoints(1, 1, 1, 1);

        Set<String> actualResult = CoursesStatistics.getHardestCoursesNames(studentsList);
        assertThat(actualResult).containsExactly("DSA");
    }

    @Test
    void getHardestCoursesNames_SpringHardestCourse_SetContainsOnlySpring() {
        createAndAddRandomStudentsToList(5);
        for (Student student : studentsList.values()) {
            student.addPoints(20, 10, 30, 4);
        }
        studentsList.get(1_000_002).addPoints(1, 1, 1, 1);
        studentsList.get(1_000_003).addPoints(0, 1, 1, 1);

        Set<String> actualResult = CoursesStatistics.getHardestCoursesNames(studentsList);
        assertThat(actualResult).containsExactly("Spring");
    }

    @Test
    void getHardestCoursesNames_DatabasesAndSpringHardestCourses_SetContainsOnlyDatabasesAndSpring() {
        createAndAddRandomStudentsToList(5);
        for (Student student : studentsList.values()) {
            student.addPoints(10, 10, 1, 1);
        }
        studentsList.get(1_000_002).addPoints(0, 1, 1, 0);
        studentsList.get(1_000_003).addPoints(1, 1, 0, 1);

        Set<String> actualResult = CoursesStatistics.getHardestCoursesNames(studentsList);
        assertThat(actualResult).containsExactlyInAnyOrder("Databases", "Spring");
    }
}