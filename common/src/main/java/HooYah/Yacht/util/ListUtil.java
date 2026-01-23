package HooYah.Yacht.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ListUtil {

    public static <T> List<T> sortByRequestOrder(
            List<Long> requestList,
            List<T> selectedList,
            Function<T, Long> getIdFunction
    ) {
        Map<Long, T> map = new HashMap<>(selectedList.size());

        for(T t : selectedList)
            map.put(getIdFunction.apply(t), t);

        return requestList
                .stream()
                .map(map::get)
                .toList();
    }

}
