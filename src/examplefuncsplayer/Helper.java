package examplefuncsplayer;

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
}
