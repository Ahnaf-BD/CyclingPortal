package cycling;

public class Rider {
    private int id;
    private String name;
    private int yearOfBirth;
    private Team team;

    public Rider(int id, String name, int yearOfBirth, Team team) {
        this.id = id;
        this.name = name;
        this.yearOfBirth = yearOfBirth;
        this.team = team;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getYearOfBirth() {
        return yearOfBirth;
    }

    public Team getTeam() {
        return team;
    }
}
