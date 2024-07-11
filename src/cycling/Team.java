package cycling;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private int id;
    private String name;
    private String description;
    private List<Rider> riders = new ArrayList<>();

    public Team(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Rider> getRiders() {
        return riders;
    }

    public void addRider(Rider rider) {
        riders.add(rider);
    }

    public void removeRider(Rider rider) {
        riders.remove(rider);
    }
}
