package tasks;

public enum Status {
    NEW("New"),
    IN_PROGRESS("inProgress"),
    DONE("Done");

    private final String name;

    Status(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
