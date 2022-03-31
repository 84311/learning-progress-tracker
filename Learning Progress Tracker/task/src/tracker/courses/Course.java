package tracker.courses;

public abstract class Course {
    public final String NAME;
    public final int MAX_POINTS;

    int points;
    int completedTasksAmount = 0;
    boolean finished = false;
    boolean notified = false;

    Course(String name, int maxPoints, int points) {
        this.NAME = name;
        this.MAX_POINTS = maxPoints;
        this.points = points;

        if (points >= MAX_POINTS) {
            finished = true;
        }

        if (points > 0) {
            completedTasksAmount = 1;
        }
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
        if (points > 0) {
            completedTasksAmount++;
        }
        if (points >= MAX_POINTS) {
            finished = true;
        }
    }

    public int getCompletedTasksAmount() {
        return completedTasksAmount;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }

    @Override
    public String toString() {
        return NAME;
    }
}