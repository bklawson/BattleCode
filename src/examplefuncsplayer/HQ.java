package examplefuncsplayer;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class HQ implements CustomUnit {
    private RobotController rc;
    private int minersBuilt = 0;

    public HQ(RobotController rc) {
        this.rc = rc;
    }

    @Override
    public void takeAction() throws GameActionException {
        if (rc.canBuildRobot(RobotType.MINER, Direction.EAST) && minersBuilt < 6) {
            rc.buildRobot(RobotType.MINER, Direction.EAST);
            minersBuilt++;
        }
    }
}
