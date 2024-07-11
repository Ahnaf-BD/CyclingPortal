package cycling;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class Stage {
    private int id;
    private String name;
    private String description;
    private double length;
    private LocalDateTime startTime;
    private StageType type;
    private List<Checkpoint> checkpoints = new ArrayList<>();
    private Map<Integer, LocalTime[]> results = new HashMap<>();
    private boolean waitingForResults;

    public Stage(int id, String name, String description, double length, LocalDateTime startTime, StageType type) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.length = length;
        this.startTime = startTime;
        this.type = type;
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

    public double getLength() {
        return length;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public StageType getType() {
        return type;
    }

    public List<Checkpoint> getCheckpoints() {
        return checkpoints;
    }

    public void addCheckpoint(Checkpoint checkpoint) {
        checkpoints.add(checkpoint);
    }

    public boolean removeCheckpointById(int checkpointId) {
        return checkpoints.removeIf(checkpoint -> checkpoint.getId() == checkpointId);
    }

    public void setWaitingForResults(boolean waitingForResults) {
        this.waitingForResults = waitingForResults;
    }

    public void registerRiderResults(Rider rider, LocalTime... checkpointTimes)
            throws DuplicatedResultException, InvalidCheckpointTimesException, InvalidStageStateException {
        if (waitingForResults) {
            if (results.containsKey(rider.getId())) {
                throw new DuplicatedResultException("Rider results already registered for this stage.");
            }
            if (checkpointTimes.length != checkpoints.size() + 2) {
                throw new InvalidCheckpointTimesException("Invalid number of checkpoint times.");
            }
            results.put(rider.getId(), checkpointTimes);
        } else {
            throw new InvalidStageStateException("Stage is not waiting for results.");
        }
    }

    public LocalTime[] getRiderResults(int riderId) {
        return results.getOrDefault(riderId, new LocalTime[0]);
    }

    public LocalTime getRiderAdjustedElapsedTime(int riderId) {
        LocalTime[] times = results.get(riderId);
        if (times == null) return null;

        LocalTime startTime = times[0];
        LocalTime finishTime = times[times.length - 1];
        long elapsedTimeInSeconds = java.time.Duration.between(startTime, finishTime).getSeconds();

        for (LocalTime time : times) {
            long seconds = java.time.Duration.between(startTime, time).getSeconds();
            if (seconds < elapsedTimeInSeconds) {
                elapsedTimeInSeconds = seconds;
            }
        }
        return LocalTime.ofSecondOfDay(elapsedTimeInSeconds);
    }

    public void deleteRiderResults(int riderId) {
        results.remove(riderId);
    }

    public int[] getRidersRank() {
        return results.keySet().stream().sorted((r1, r2) -> {
            LocalTime time1 = getRiderAdjustedElapsedTime(r1);
            LocalTime time2 = getRiderAdjustedElapsedTime(r2);
            return time1.compareTo(time2);
        }).mapToInt(Integer::intValue).toArray();
    }

    public LocalTime[] getRankedAdjustedElapsedTimes() {
        return results.keySet().stream().sorted((r1, r2) -> {
            LocalTime time1 = getRiderAdjustedElapsedTime(r1);
            LocalTime time2 = getRiderAdjustedElapsedTime(r2);
            return time1.compareTo(time2);
        }).map(this::getRiderAdjustedElapsedTime).toArray(LocalTime[]::new);
    }

    public int[] getRidersPoints() {
        // Placeholder for points calculation
        return new int[results.size()];
    }

    public int[] getRidersMountainPoints() {
        // Placeholder for mountain points calculation
        return new int[results.size()];
    }
}
