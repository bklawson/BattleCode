package examplefuncsplayer;

import battlecode.common.Direction;
import battlecode.common.MapLocation;

import java.util.ArrayList;

public class Helper {
    public static <T> T GetRandomElementFromArrayList(ArrayList<T> list) {
        if (list.size() == 0) return null;
        int index = (int) (Math.random() * list.size());
        return list.get(index);
    }

    public static <T> T GetRandomElementFromArray(T[] array) {
        if (array.length == 0) return null;
        int index = (int) (Math.random() * array.length);
        return array[index];
    }

    public static Direction[] GetAroundDirection(Direction direction) {
        Direction[] aroundDirection = new Direction[2];
        int index = 0;
        for (int i = 0; i < Direction.allDirections().length; i++) {
            if (Direction.allDirections()[i] == direction) {
                index = i;
                break;
            }
        }

        if (index - 1 < 0) aroundDirection[0] = Direction.allDirections()[Direction.allDirections().length-1];
        else aroundDirection[0] = Direction.allDirections()[index-1];

        if (index + 1 == Direction.allDirections().length) aroundDirection[1] = Direction.allDirections()[0];
        else aroundDirection[1] = Direction.allDirections()[index+1];

        return aroundDirection;
    }

    public static Direction GetNearestDirectionByDegrees(MapLocation currentLocation, MapLocation targetLocation) {

        float angle = (float) Math.toDegrees(Math.atan2(targetLocation.y - currentLocation.y, targetLocation.x - currentLocation.x));
        if (angle < 0) angle += 360;

        int index = Math.round(angle / 45);
        return Direction.allDirections()[index];
    }
}
