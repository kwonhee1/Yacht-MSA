package HooYah.Redis;

import java.util.List;
import java.util.Optional;

public interface CacheService<T> {

    void add(Long id, T value);

    T getOrSelect(Long selectId, Select<T> select);

    T getOrSelect(Long subjectId, Long selectId, Select<T> select);

    List<T> getListOrSelect(List<Long> selectIdList, Select<List<T>> select);

    List<T> getListOrSelect(Long subjectId, List<Long> selectIdList, Select<List<T>> select);

    @FunctionalInterface
    interface Select <S> {
        S select();
    }

}
