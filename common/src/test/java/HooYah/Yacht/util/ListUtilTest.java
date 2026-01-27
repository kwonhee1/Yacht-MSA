package HooYah.Yacht.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ListUtilTest {

    private Map<Long, Data> dataMap;

    public ListUtilTest() {
        dataMap = new HashMap<>();

        dataMap.put(1L, new Data(1L, "a"));
        dataMap.put(2L, new Data(2L, "b"));
        dataMap.put(3L, new Data(3L, "c"));
        dataMap.put(4L, new Data(4L, "d"));
        dataMap.put(5L, new Data(5L, "e"));
    }

    @Test
    public void sortByRequestOrderTest() {
        List<Long> requestList = new ArrayList<>();
        requestList.add(2L); requestList.add(null); requestList.add(1L); requestList.add(2L); requestList.add(1L);

        List<Data> selectedList = List.of(dataMap.get(1L), dataMap.get(2L));

        List<Data> sortedList = ListUtil.sortByRequestOrder(requestList, selectedList, (data)->data.getId());
        List<Data> actureList = requestList
                .stream()
                .map(dataMap::get)
                .toList();

        for(int i = 0; i < actureList.size(); i++)
            Assertions.assertEquals(actureList.get(i), sortedList.get(i));
    }

    record Data (
            Long id, String name
    ) {
        public Long getId() {
            return id;
        }
    }

}
