package cycling;

import java.io.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * CyclingPortalImpl is the attempt of creating a functioning implementer of the MiniCyclingPortal interface.
 * 
 * @author Ahnaf Tahmid Haque
 * @version 2.0
 *
 */
public class CyclingPortalImpl implements MiniCyclingPortal {
    private Map<Integer, Race> races = new HashMap<>();
    private Map<Integer, Team> teams = new HashMap<>();
    private Map<Integer, Rider> riders = new HashMap<>();
    private int nextRaceId = 1;
    private int nextTeamId = 1;
    private int nextRiderId = 1;
    private int nextStageId = 1;
    private int nextCheckpointId = 1;

    // Implementing interface methods

    @Override
    public int[] getRaceIds() {
        return races.keySet().stream().mapToInt(Integer::intValue).toArray();
    }

    @Override
    public int createRace(String name, String description) throws IllegalNameException, InvalidNameException {
        if (name == null || name.trim().isEmpty() || name.length() > 30 || name.contains(" ")) {
            throw new InvalidNameException("Invalid race name.");
        }
        for (Race race : races.values()) {
            if (race.getName().equals(name)) {
                throw new IllegalNameException("Race name already exists.");
            }
        }
        Race race = new Race(nextRaceId++, name, description);
        races.put(race.getId(), race);
        return race.getId();
    }

    @Override
    public String viewRaceDetails(int raceId) throws IDNotRecognisedException {
        Race race = races.get(raceId);
        if (race == null) {
            throw new IDNotRecognisedException("Race ID not recognised.");
        }
        return race.toString();
    }

    @Override
    public void removeRaceById(int raceId) throws IDNotRecognisedException {
        if (races.remove(raceId) == null) {
            throw new IDNotRecognisedException("Race ID not recognised.");
        }
    }

    @Override
    public int getNumberOfStages(int raceId) throws IDNotRecognisedException {
        Race race = races.get(raceId);
        if (race == null) {
            throw new IDNotRecognisedException("Race ID not recognised.");
        }
        return race.getStages().size();
    }

    @Override
    public int addStageToRace(int raceId, String stageName, String description, double length, LocalDateTime startTime, StageType type)
            throws IDNotRecognisedException, IllegalNameException, InvalidNameException, InvalidLengthException {
        if (stageName == null || stageName.trim().isEmpty() || stageName.length() > 30 || stageName.contains(" ")) {
            throw new InvalidNameException("Invalid stage name.");
        }
        if (length < 5) {
            throw new InvalidLengthException("Stage length must be at least 5 km.");
        }
        Race race = races.get(raceId);
        if (race == null) {
            throw new IDNotRecognisedException("Race ID not recognised.");
        }
        for (Stage stage : race.getStages()) {
            if (stage.getName().equals(stageName)) {
                throw new IllegalNameException("Stage name already exists.");
            }
        }
        Stage stage = new Stage(nextStageId++, stageName, description, length, startTime, type);
        race.addStage(stage);
        return stage.getId();
    }

    @Override
    public int[] getRaceStages(int raceId) throws IDNotRecognisedException {
        Race race = races.get(raceId);
        if (race == null) {
            throw new IDNotRecognisedException("Race ID not recognised.");
        }
        return race.getStages().stream().mapToInt(Stage::getId).toArray();
    }

    @Override
    public double getStageLength(int stageId) throws IDNotRecognisedException {
        Stage stage = findStage(stageId);
        return stage.getLength();
    }

    @Override
    public void removeStageById(int stageId) throws IDNotRecognisedException {
        for (Race race : races.values()) {
            if (race.removeStageById(stageId)) {
                return;
            }
        }
        throw new IDNotRecognisedException("Stage ID not recognised.");
    }

    @Override
    public int addCategorizedClimbToStage(int stageId, Double location, CheckpointType type, Double averageGradient, Double length)
            throws IDNotRecognisedException, InvalidLocationException, InvalidStageStateException, InvalidStageTypeException {
        Stage stage = findStage(stageId);
        if (stage.getType() == StageType.TT) {
            throw new InvalidStageTypeException("Time-trial stages cannot contain any checkpoint.");
        }
        if (location < 0 || location > stage.getLength()) {
            throw new InvalidLocationException("Invalid location.");
        }
        Checkpoint checkpoint = new Checkpoint(nextCheckpointId++, location, type, averageGradient, length);
        stage.addCheckpoint(checkpoint);
        return checkpoint.getId();
    }

    @Override
    public int addIntermediateSprintToStage(int stageId, double location) throws IDNotRecognisedException, InvalidLocationException, InvalidStageStateException, InvalidStageTypeException {
        Stage stage = findStage(stageId);
        if (stage.getType() == StageType.TT) {
            throw new InvalidStageTypeException("Time-trial stages cannot contain any checkpoint.");
        }
        if (location < 0 || location > stage.getLength()) {
            throw new InvalidLocationException("Invalid location.");
        }
        Checkpoint checkpoint = new Checkpoint(nextCheckpointId++, location, CheckpointType.SPRINT, null, null);
        stage.addCheckpoint(checkpoint);
        return checkpoint.getId();
    }

    @Override
    public void removeCheckpoint(int checkpointId) throws IDNotRecognisedException, InvalidStageStateException {
        for (Race race : races.values()) {
            for (Stage stage : race.getStages()) {
                if (stage.removeCheckpointById(checkpointId)) {
                    return;
                }
            }
        }
        throw new IDNotRecognisedException("Checkpoint ID not recognised.");
    }

    @Override
    public void concludeStagePreparation(int stageId) throws IDNotRecognisedException, InvalidStageStateException {
        Stage stage = findStage(stageId);
        stage.setWaitingForResults(true);
    }

    @Override
    public int[] getStageCheckpoints(int stageId) throws IDNotRecognisedException {
        Stage stage = findStage(stageId);
        return stage.getCheckpoints().stream().mapToInt(Checkpoint::getId).toArray();
    }

    @Override
    public int createTeam(String name, String description) throws IllegalNameException, InvalidNameException {
        if (name == null || name.trim().isEmpty() || name.length() > 30 || name.contains(" ")) {
            throw new InvalidNameException("Invalid team name.");
        }
        for (Team team : teams.values()) {
            if (team.getName().equals(name)) {
                throw new IllegalNameException("Team name already exists.");
            }
        }
        Team team = new Team(nextTeamId++, name, description);
        teams.put(team.getId(), team);
        return team.getId();
    }

    @Override
    public void removeTeam(int teamId) throws IDNotRecognisedException {
        if (teams.remove(teamId) == null) {
            throw new IDNotRecognisedException("Team ID not recognised.");
        }
    }

    @Override
    public int[] getTeams() {
        return teams.keySet().stream().mapToInt(Integer::intValue).toArray();
    }

    @Override
    public int[] getTeamRiders(int teamId) throws IDNotRecognisedException {
        Team team = teams.get(teamId);
        if (team == null) {
            throw new IDNotRecognisedException("Team ID not recognised.");
        }
        return team.getRiders().stream().mapToInt(Rider::getId).toArray();
    }

    @Override
    public int createRider(int teamID, String name, int yearOfBirth) throws IDNotRecognisedException, IllegalArgumentException {
        if (name == null || name.trim().isEmpty() || yearOfBirth < 1900) {
            throw new IllegalArgumentException("Invalid rider name or year of birth.");
        }
        Team team = teams.get(teamID);
        if (team == null) {
            throw new IDNotRecognisedException("Team ID not recognised.");
        }
        Rider rider = new Rider(nextRiderId++, name, yearOfBirth, team);
        team.addRider(rider);
        riders.put(rider.getId(), rider);
        return rider.getId();
    }

    @Override
    public void removeRider(int riderId) throws IDNotRecognisedException {
        Rider rider = riders.remove(riderId);
        if (rider == null) {
            throw new IDNotRecognisedException("Rider ID not recognised.");
        }
        rider.getTeam().removeRider(rider);
    }

    @Override
    public void registerRiderResultsInStage(int stageId, int riderId, LocalTime... checkpointTimes)
            throws IDNotRecognisedException, DuplicatedResultException, InvalidCheckpointTimesException, InvalidStageStateException {
        Stage stage = findStage(stageId);
        Rider rider = findRider(riderId);
        stage.registerRiderResults(rider, checkpointTimes);
    }

    @Override
    public LocalTime[] getRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {
        Stage stage = findStage(stageId);
        return stage.getRiderResults(riderId);
    }

    @Override
    public LocalTime getRiderAdjustedElapsedTimeInStage(int stageId, int riderId) throws IDNotRecognisedException {
        Stage stage = findStage(stageId);
        return stage.getRiderAdjustedElapsedTime(riderId);
    }

    @Override
    public void deleteRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {
        Stage stage = findStage(stageId);
        stage.deleteRiderResults(riderId);
    }

    @Override
    public int[] getRidersRankInStage(int stageId) throws IDNotRecognisedException {
        Stage stage = findStage(stageId);
        return stage.getRidersRank();
    }

    @Override
    public LocalTime[] getRankedAdjustedElapsedTimesInStage(int stageId) throws IDNotRecognisedException {
        Stage stage = findStage(stageId);
        return stage.getRankedAdjustedElapsedTimes();
    }

    @Override
    public int[] getRidersPointsInStage(int stageId) throws IDNotRecognisedException {
        Stage stage = findStage(stageId);
        return stage.getRidersPoints();
    }

    @Override
    public int[] getRidersMountainPointsInStage(int stageId) throws IDNotRecognisedException {
        Stage stage = findStage(stageId);
        return stage.getRidersMountainPoints();
    }

    @Override
    public void eraseCyclingPortal() {
        races.clear();
        teams.clear();
        riders.clear();
        nextRaceId = 1;
        nextTeamId = 1;
        nextRiderId = 1;
        nextStageId = 1;
        nextCheckpointId = 1;
    }

    @Override
    public void saveCyclingPortal(String filename) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(this);
        }
    }

    @Override
    public void loadCyclingPortal(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            CyclingPortalImpl loaded = (CyclingPortalImpl) in.readObject();
            this.races = loaded.races;
            this.teams = loaded.teams;
            this.riders = loaded.riders;
            this.nextRaceId = loaded.nextRaceId;
            this.nextTeamId = loaded.nextTeamId;
            this.nextRiderId = loaded.nextRiderId;
            this.nextStageId = loaded.nextStageId;
            this.nextCheckpointId = loaded.nextCheckpointId;
        }
    }

    private Stage findStage(int stageId) throws IDNotRecognisedException {
        for (Race race : races.values()) {
            for (Stage stage : race.getStages()) {
                if (stage.getId() == stageId) {
                    return stage;
                }
            }
        }
        throw new IDNotRecognisedException("Stage ID not recognised.");
    }

    private Rider findRider(int riderId) throws IDNotRecognisedException {
        Rider rider = riders.get(riderId);
        if (rider == null) {
            throw new IDNotRecognisedException("Rider ID not recognised.");
        }
        return rider;
    }
}