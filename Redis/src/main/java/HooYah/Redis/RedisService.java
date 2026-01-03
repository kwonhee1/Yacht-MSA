package HooYah.Redis;

import java.util.List;
import java.util.Optional;

public interface RedisService {

    void add(Long id, Object value);

    Optional getOrSelect(Long selectId, Select<Optional> select);

    Optional getOrSelect(Long subjectId, Long selectId, Select<Optional> select);

    List getListOrSelect(List<Long> selectIdList, Select<List> select);

    List getListOrSelect(Long subjectId, List<Long> selectIdList, Select<List> select);

    @FunctionalInterface
    interface Select <S> {
        S select();
    }

}
