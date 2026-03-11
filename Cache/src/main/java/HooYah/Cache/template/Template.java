package HooYah.Cache.template;

import HooYah.Cache.connection.SaveSecond;
import java.util.List;

public interface Template {

    void add(String key, String value, SaveSecond second);

    void addAll(List<String> keyList, List<String> valueList, SaveSecond second);

    String get(String key, SaveSecond second);

    List<String> getList(List<String> keyList, SaveSecond second);

    void close();

}
