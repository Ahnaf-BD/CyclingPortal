package cycling;

import java.util.*;

public class Race {
    private int id;
    private String name;
    private String description;
    private List<Stage> stages = new ArrayList<>();

    public Race(int id, String name, String description) {
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

    public List<Stage> getStages() {
        return stages;
    }

    public void addStage(Stage stage) {
        stages.add(stage);
    }

    public boolean removeStageById(int stageId) {
        return stages.removeIf(stage -> stage.getId() == stageId);
    }

    public Stage getStageById(int stageId) {
        for (Stage stage : stages) {
            if (stage.getId() == stageId) {
                return stage;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        double totalLength = stages.stream().mapToDouble(Stage::getLength).sum();
        return String.format("Race ID: %d, Name: %s, Description: %s, Number of Stages: %d, Total Length: %.2f km",
                id, name, description, stages.size(), totalLength);
    }
}
