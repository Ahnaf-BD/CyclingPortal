package cycling;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import static org.junit.Assert.*;

public class CyclingPortalImplTest {
    private CyclingPortalImpl portal;

    @Before
    public void setUp() {
        portal = new CyclingPortalImpl();
    }

    @Test
    public void testAddCategorizedClimbToStage() throws IDNotRecognisedException, InvalidLocationException, InvalidStageStateException, InvalidStageTypeException {
        int checkpointId = portal.addCategorizedClimbToStage(1, 10.0, CheckpointType.C2, 5.0, 2.0);
        assertEquals(1, checkpointId);
    }

    @Test
    public void testAddIntermediateSprintToStage() throws IDNotRecognisedException, InvalidLocationException, InvalidStageStateException, InvalidStageTypeException {
        int checkpointId = portal.addIntermediateSprintToStage(1, 20.0);
        assertEquals(2, checkpointId);
    }

    @Test
    public void testCreateRace() throws IllegalNameException, InvalidNameException {
        int raceId = portal.createRace("Tour de Java", "A challenging race");
        assertEquals(1, raceId);
        assertEquals(1, portal.getRaceIds().length);
    }

    @Test
    public void testCreateRaceWithInvalidName() {
        try {
            portal.createRace(" ", "A challenging race");
            fail("Expected InvalidNameException");
        } catch (InvalidNameException e) {
            // expected
        }

        try {
            portal.createRace(null, "A challenging race");
            fail("Expected InvalidNameException");
        } catch (InvalidNameException e) {
            // expected
        }

        try {
            portal.createRace("This name is definitely more than thirty characters long", "A challenging race");
            fail("Expected InvalidNameException");
        } catch (InvalidNameException e) {
            // expected
        }
    }

    @Test
    public void testCreateRaceWithDuplicateName() throws IllegalNameException, InvalidNameException {
        portal.createRace("Tour de Java", "A challenging race");
        try {
            portal.createRace("Tour de Java", "Another race");
            fail("Expected IllegalNameException");
        } catch (IllegalNameException e) {
            // expected
        }
    }

    @Test
    public void testRemoveRaceById() throws IllegalNameException, InvalidNameException, IDNotRecognisedException {
        int raceId = portal.createRace("Tour de Java", "A challenging race");
        assertEquals(1, portal.getRaceIds().length);
        portal.removeRaceById(raceId);
        assertEquals(0, portal.getRaceIds().length);
    }

    @Test
    public void testAddStageToRace() throws Exception {
        int raceId = portal.createRace("Tour de Java", "A challenging race");
        int stageId = portal.addStageToRace(raceId, "Stage 1", "Mountain stage", 120.5, LocalDateTime.now(), StageType.HIGH_MOUNTAIN);
        assertEquals(1, stageId);
        assertEquals(1, portal.getRaceStages(raceId).length);
    }

    @Test
    public void testAddInvalidStage() throws Exception {
        int raceId = portal.createRace("Tour de Java", "A challenging race");
        try {
            portal.addStageToRace(raceId, "Stage 1", "Short stage", 4.0, LocalDateTime.now(), StageType.FLAT);
            fail("Expected InvalidLengthException");
        } catch (InvalidLengthException e) {
            // expected
        }
    }

    @Test
    public void testCreateTeam() throws IllegalNameException, InvalidNameException {
        int teamId = portal.createTeam("Team Java", "A strong team");
        assertEquals(1, teamId);
        assertEquals(1, portal.getTeams().length);
    }

    @Test
    public void testCreateRider() throws IllegalNameException, InvalidNameException, IDNotRecognisedException {
        int teamId = portal.createTeam("Team Java", "A strong team");
        int riderId = portal.createRider(teamId, "John Doe", 1990);
        assertEquals(1, riderId);
        assertEquals(1, portal.getTeamRiders(teamId).length);
    }

    @Test
    public void testRegisterRiderResults() throws Exception {
        int raceId = portal.createRace("Tour de Java", "A challenging race");
        int stageId = portal.addStageToRace(raceId, "Stage 1", "Mountain stage", 120.5, LocalDateTime.now(), StageType.HIGH_MOUNTAIN);
        int teamId = portal.createTeam("Team Java", "A strong team");
        int riderId = portal.createRider(teamId, "John Doe", 1990);
        portal.concludeStagePreparation(stageId);
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime checkpoint1 = LocalTime.of(10, 30);
        LocalTime finishTime = LocalTime.of(12, 0);
        portal.registerRiderResultsInStage(stageId, riderId, startTime, checkpoint1, finishTime);
        LocalTime[] results = portal.getRiderResultsInStage(stageId, riderId);
        assertEquals(startTime, results[0]);
        assertEquals(finishTime, results[2]);
    }

    @Test
    public void testSaveAndLoadCyclingPortal() throws IOException, ClassNotFoundException {
        int raceId = portal.createRace("Tour de Java", "A challenging race");
        portal.saveCyclingPortal("testPortal.ser");

        CyclingPortalImpl newPortal = new CyclingPortalImpl();
        newPortal.loadCyclingPortal("testPortal.ser");
        assertEquals(1, newPortal.getRaceIds().length);
        assertEquals(raceId, newPortal.getRaceIds()[0]);
    }
}
