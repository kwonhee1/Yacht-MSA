package HooYah.Redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;

public class RedisServiceImpl implements RedisService {

    private final RedisTemplate redisTemplate;
    private final String category;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Long saveSecond = 3600L; // default value 1 hour

    public RedisServiceImpl(String category, ConnectionPool connectionPool, Long second) {
        this.redisTemplate = new JedisTemplate(connectionPool);
        this.category = category;
        this.saveSecond = second;
    }

    public RedisServiceImpl(String category, ConnectionPool connectionPool) {
        this.redisTemplate = new JedisTemplate(connectionPool);
        this.category = category;
    }

    @Override
    public void add(Long id, Object value) {
        String key = toKey(category, id);

        redisTemplate.add(key, objectToString(value), saveSecond);
    }

    @Override
    public <R> Optional<R> getOrSelect(Long subjectId, Class<R> clazz, Select<R> select) {
        return getOrSelect(toKey(category, subjectId), clazz, select);
    }

    @Override
    public <R> Optional<R> getListOrSelect(Long subjectId, Long selectId, Class<R> clazz, Select<R> select) {
        return getOrSelect(toKey(category, subjectId, selectId), clazz, select);
    }

    private <R> Optional<R> getOrSelect(String key, Class<R> clazz, Select<R> select) {
        RedisValue redisValue = redisTemplate.get(key, saveSecond);

        if(redisValue.hasValue())
            return Optional.of(stringToObject(redisValue.get(), clazz));
        if(redisValue.isNull())
            return Optional.empty();

        // not value in redis, need select
        Optional<R> selectedData = select.select();
        if(selectedData.isEmpty())
            redisTemplate.add(key, RedisValue.NULL, saveSecond);
        else
            redisTemplate.add(key, objectToString(redisValue.get()), saveSecond);

        return selectedData;
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

    private <RT> RT stringToObject(String value, Class<RT> clazz) {
        try {
            return objectMapper.readValue(value, clazz);
        } catch ( JsonProcessingException e) {
            throw new RedisException(e.getMessage());
        }
    }
}
