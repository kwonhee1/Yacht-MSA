package HooYah.Redis;

import java.util.List;
import java.util.Optional;

public interface CacheService {

    void add(Long id, Object value);

    Object getOrSelect(Long selectId, Select<Object> select);

    Object getOrSelect(Long subjectId, Long selectId, Select<Object> select);

    List getListOrSelect(List<Long> selectIdList, Select<List> select);

    List getListOrSelect(Long subjectId, List<Long> selectIdList, Select<List> select);

    @FunctionalInterface
    interface Select <S> {
        S select();
    }

}
