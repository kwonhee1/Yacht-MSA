package HooYah.Redis;

import HooYah.Redis.connection.SaveSecond;
import HooYah.Redis.pool.JedisPool;
import HooYah.Redis.pool.Pool;
import HooYah.Redis.template.RedisTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RedisServiceImpl implements RedisService {

    private final String category;
    private final RedisTemplate redisTemplate;

    /*
        ObjectMapper must init in Redis library
        외부에서 주입을 받게 되면 -> 입력될 때 사용되는 ObjectMapper와 출력될때 사용되는 ObjectMapper 버전 차이로 인해 작동하지 않을 수있음!
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    private SaveSecond saveSecond = new SaveSecond(3600L); // default value 1 hour

    public RedisServiceImpl(
            String category,
            Pool pool
    ) {
        this.redisTemplate = new RedisTemplate(pool);
        this.category = category;
    }

    public RedisServiceImpl(
            String category,
            JedisPool jedisPool,
            Long second
    ) {
        this(category, jedisPool);
        this.saveSecond = new SaveSecond(second);
    }

    @Override
    public void add(Long id, Object value) {
        String key = toKey(category, id);

        redisTemplate.add(key, objectToString(value), saveSecond);
    }

    @Override
    public Optional getOrSelect(Long subjectId, Select<Optional> select) {
        return getOrSelect(toKey(category, subjectId), select);
    }

    @Override
    public Optional getOrSelect(Long subjectId, Long selectId, Select<Optional> select) {
        return getOrSelect(toKey(category, subjectId, selectId), select);
    }

    private Optional getOrSelect(String key, Select<Optional> select) {
        RedisValue redisValue = redisTemplate.get(key, saveSecond);

        if(redisValue.hasValue())
            return Optional.of(stringToObject(redisValue.get()));
        if(redisValue.isNull())
            return Optional.empty();

        // not value in redis, need select
        Optional selectedData = select.select();
        if(selectedData.isEmpty())
            redisTemplate.add(key, RedisValue.NULL, saveSecond);
        else
            redisTemplate.add(key, objectToString(redisValue.get()), saveSecond);

        return selectedData;
    }

    @Override
    public List<Optional> getListOrSelect(Long subjectId, List<Long> selectIdList, Select<List> select) {
        List<String> keyList = selectIdList
                        .stream()
                        .map((id)->toKey(category, subjectId, id))
                        .toList();

        List<RedisValue> redisValueList = redisTemplate.getList(keyList, saveSecond);

        boolean containsUnKnown = false;
        for(RedisValue redisValue : redisValueList)
            if(redisValue.isUnKnown())
                containsUnKnown = true;

        if(!containsUnKnown) {
            List<Optional> response =  new ArrayList<>();
            for(RedisValue redisValue : redisValueList){
                if(redisValue.hasValue())
                    response.add(Optional.of(stringToObject(redisValue.get())));
                else
                    response.add(Optional.empty());
            }
            return response;
        }

        // contains unKnown -> must select all
        List selectedData = select.select();

        for(int i = 0; i < selectedData.size(); i++) {
            // null 값이 있으면 data를 RedisValue.NULL로 처리한다
            if (selectedData.get(i) == null)
                selectedData.set(i, RedisValue.NULL);
        }

        redisTemplate.addAll(keyList, objectToString(selectedData), saveSecond);
        return selectedData.stream().map(Optional::ofNullable).toList();
    }

    private String toKey(String category, Long... id) {
        StringBuilder key = new StringBuilder(category);

        for(Long i : id)
            key.append(i);

        return key.toString();
    }

    private String objectToString(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch ( JsonProcessingException e) {
            throw new RedisException(e.getMessage());
        }
    }

    private Map stringToObject(String value) {
        try {
            return objectMapper.readValue(value, Map.class);
        } catch ( JsonProcessingException e) {
            throw new RedisException(e.getMessage());
        }
    }

    private List<String> objectToString(List values) {
        return values.stream().map(this::objectToString).toList();
    }

    private List<Map> stringToObject(List<String> values) {
        return values.stream().map(this::stringToObject).toList();
    }

}
