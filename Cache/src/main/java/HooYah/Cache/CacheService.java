package HooYah.Cache;

import java.util.List;

public interface CacheService<T> {

    void add(Long id, T value);

    T getOrSelect(Long selectId, Select<T> select);

    T getOrSelect(Long subjectId, Long selectId, Select<T> select);

    List<T> getListOrSelect(List<Long> selectIdList, Select<List<T>> select);

    List<T> getListOrSelect(Long subjectId, List<Long> selectIdList, Select<List<T>> select);

    void reconnect();

    @FunctionalInterface
    interface Select <S> {
        S select();
    }

}
