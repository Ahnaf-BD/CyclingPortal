package cycling;

public class Checkpoint {
    private int id;
    private double location;
    private CheckpointType type;
    private Double averageGradient;
    private Double length;

    public Checkpoint(int id, double location, CheckpointType type, Double averageGradient, Double length) {
        this.id = id;
        this.location = location;
        this.type = type;
        this.averageGradient = averageGradient;
        this.length = length;
    }

    public int getId() {
        return id;
    }

    public double getLocation() {
        return location;
    }

    public CheckpointType getType() {
        return type;
    }

    public Double getAverageGradient() {
        return averageGradient;
    }

    public Double getLength() {
        return length;
    }
}
