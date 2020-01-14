package examplefuncsplayer;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public strictfp class RobotPlayer {
    static RobotController rc;

    static Direction[] directions = {
            Direction.NORTH,
            Direction.NORTHEAST,
            Direction.EAST,
            Direction.SOUTHEAST,
            Direction.SOUTH,
            Direction.SOUTHWEST,
            Direction.WEST,
            Direction.NORTHWEST
    };
    static RobotType[] spawnedByMiner = {RobotType.REFINERY, RobotType.VAPORATOR, RobotType.DESIGN_SCHOOL,
            RobotType.FULFILLMENT_CENTER, RobotType.NET_GUN};

    public static void run(RobotController rc) throws GameActionException {
        RobotPlayer.rc = rc;

        System.out.println("I'm a " + rc.getType() + " and I just got created!");
        CustomUnit unit = getUnitInstance(rc.getType());

        //noinspection InfiniteLoopStatement
        while (true) {
            if (unit != null) {
                unit.takeAction();
            }
        }

    }

    public static CustomUnit getUnitInstance(RobotType type) {
        switch (type) {
            case HQ:
                return new HQ(rc);
            case MINER:
                return new Miner(rc);
            default:
                return null;
        }
    }


}
