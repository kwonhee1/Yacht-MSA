package HooYah.Redis;

import java.util.Optional;

public interface RedisService {

    void add(Long id, Object value);

    Optional getOrSelect(Long subjectId, Select select);

    Optional getListOrSelect(Long subjectId, Long selectId, Select select);

    @FunctionalInterface
    interface Select  {
        Optional select();
    }

}
