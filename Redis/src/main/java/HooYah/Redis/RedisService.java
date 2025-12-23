package HooYah.Redis;

import java.util.Optional;

public interface RedisService {

    void add(Long id, Object value);

    <R> Optional<R> getOrSelect(Long subjectId, Class<R> clazz, Select<R> select);

    <R> Optional<R> getListOrSelect(Long subjectId, Long selectId, Class<R> clazz, Select<R> select);

    @FunctionalInterface
    interface Select <R> {
        Optional<R> select();
    }

}
