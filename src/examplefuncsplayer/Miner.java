package examplefuncsplayer;

import battlecode.common.*;

import java.util.ArrayList;

public class Miner implements CustomUnit {
    private static int SOUP_CARRY_LIMIT = 100;
    private static int MAX_VISION_DISTANCE = 36;

    private static int[][] SOUP_SEARCH = {
            {-5, 0},
            {-5, -3},
            {-3, -5},
            {0, -5},
            {3, -5},
            {5, -3},
            {5, 0},
            {5, 3},
            {3, 5},
            {0, 5},
            {-3, 5},
            {-5, 3},
            {-2, -2},
            {2, -2},
            {-2, 2},
            {2, 2}
    };

    private static int[][] SEARCH_AROUND = {
            {-1, -1},
            {-1, 0},
            {-1, 1},
            {0, -1},
            {0, 1},
            {1, -1},
            {1, 0},
            {1, 1}
    };

    private RobotController rc;
    private RobotInfo parentHQ = null;
    private ArrayList<MapLocation> soupLocations;
    private MinerState currentState;
    private MapLocation navigateLocation;
    private boolean mineAfter, depositAfter;

    public Miner(RobotController rc) {
        this.rc = rc;

        for (RobotInfo r: rc.senseNearbyRobots()) {
            if (r.type == RobotType.HQ) parentHQ = r;
        }

        this.soupLocations = new ArrayList<>();
        currentState = MinerState.SEARCH;
    }

    @Override
    public void takeAction() throws GameActionException  {
        switch(currentState) {
            case SEARCH:
                doFullSearch();
                break;
            case NAVIGATE:
                navigateToMapLocation(navigateLocation, mineAfter, depositAfter);
                break;
            case MINE:
                mineSoupUntilFullOrSpotEmpty(navigateLocation);
                break;
            case DEPOSIT:
                depositToHQ(navigateLocation);
                break;
        }
    }

    private void doFullSearch() throws GameActionException {
        ArrayList<MapLocation> soupSearchResults = doSoupSearch();

        for (MapLocation deepSearchResult: soupSearchResults) {
            if (!soupLocations.contains(deepSearchResult)) soupLocations.add(deepSearchResult);
            ArrayList<MapLocation> scanAround = scanAroundMapLocation(deepSearchResult);

            for (MapLocation scanAroundResult: scanAround) {
                if (!soupLocations.contains(scanAroundResult)) soupLocations.add(scanAroundResult);
            }
        }

        System.out.println("Found " + soupLocations.size() + " soup locations!");

        if (soupLocations.size() > 0) {
            navigateLocation = Helper.GetRandomElementFromArrayList(soupLocations);
            this.mineAfter = true;
            this.depositAfter = false;
            currentState = MinerState.NAVIGATE;
        } else {
            currentState = MinerState.EXPLORE;
        }
    }

    private void navigateToMapLocation(MapLocation navigateLocation, boolean mineAfter, boolean depositAfter) throws GameActionException {
        if (navigateLocation == null) return;

        MapLocation currentLocation = rc.getLocation();
        rc.setIndicatorLine(currentLocation, navigateLocation, 0, 200, 100);

        Direction moveDirection = currentLocation.directionTo(navigateLocation);
        if (rc.isReady() && rc.canMove(moveDirection)) {
            rc.move(moveDirection);
        } else if (rc.isReady()) {
            moveDirection = Helper.GetRandomElementFromArray(Direction.allDirections());
            if (rc.canMove(moveDirection)) {
                rc.move(moveDirection);
            }
        }

        if (currentLocation.distanceSquaredTo(navigateLocation) <= 2) {
            if (mineAfter) currentState = MinerState.MINE;
            if (depositAfter) currentState = MinerState.DEPOSIT;
        }
    }

    private void mineSoupUntilFullOrSpotEmpty(MapLocation navigateLocation) throws GameActionException {
        MapLocation currentLocation = rc.getLocation();
        rc.setIndicatorDot(navigateLocation, 0, 100, 200);
        Direction directionToSoup = currentLocation.directionTo(navigateLocation);

        if (rc.canMineSoup(directionToSoup)) {
            rc.mineSoup(directionToSoup);
        }

        int soupCarrying = rc.getSoupCarrying();
        int soupRemaining = rc.senseSoup(navigateLocation);

        if (soupRemaining == 0 && soupCarrying <= SOUP_CARRY_LIMIT && soupLocations.size() > 0) {
            System.out.println("Moving to nearby soup to mine more!");
            soupLocations.remove(navigateLocation);
            this.navigateLocation = Helper.GetRandomElementFromArrayList(soupLocations);
            this.mineAfter = true;
            this.depositAfter = false;
            currentState = MinerState.NAVIGATE;

        } else if (soupCarrying >= SOUP_CARRY_LIMIT) {
            System.out.println("Now full of soup!");
            this.navigateLocation = parentHQ.location;
            this.mineAfter = false;
            this.depositAfter = true;
            currentState = MinerState.NAVIGATE;
        }
    }

    private void depositToHQ(MapLocation navigateLocation) throws GameActionException {
        if (rc.canDepositSoup(rc.getLocation().directionTo(navigateLocation))) {
            System.out.println("Depositing soup!");
            rc.depositSoup(rc.getLocation().directionTo(navigateLocation), rc.getSoupCarrying());
        }

        if (rc.getSoupCarrying() == 0) {
            currentState = MinerState.SEARCH;
        }
    }

    private ArrayList<MapLocation> doSoupSearch() throws GameActionException {
        MapLocation currentLocation = rc.getLocation();
        ArrayList<MapLocation> returnList = new ArrayList<>();
        for (int[] offset: SOUP_SEARCH) {
            MapLocation checkLocation = currentLocation.translate(offset[0], offset[1]);

            if (rc.senseSoup(checkLocation) > 0) {
                rc.setIndicatorDot(checkLocation, 0,200,100);
                returnList.add(checkLocation);
            } else {
                rc.setIndicatorDot(checkLocation, 200, 0, 0);
            }
        }

        return returnList;
    }

    private ArrayList<MapLocation> scanAroundMapLocation(MapLocation l) throws GameActionException {
        ArrayList<MapLocation> returnList = new ArrayList<>();
        for (int[] offset: SEARCH_AROUND) {
            MapLocation checkLocation = l.translate(offset[0], offset[1]);

            if (rc.canSenseLocation(checkLocation) && rc.senseSoup(checkLocation) > 0) {
                rc.setIndicatorDot(checkLocation, 0, 200, 100);
                returnList.add(checkLocation);
            }
        }

        return returnList;
    }


}
