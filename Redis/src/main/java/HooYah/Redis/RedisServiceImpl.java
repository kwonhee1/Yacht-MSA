package HooYah.Redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.Optional;

public class RedisServiceImpl implements RedisService {

    private final String category;
    private final RedisTemplate redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Long saveSecond = 3600L; // default value 1 hour

    public RedisServiceImpl(
            String category,
            ConnectionPool connectionPool
    ) {
        this.redisTemplate = new JedisTemplate(connectionPool);
        this.category = category;
    }

    public RedisServiceImpl(
            String category,
            ConnectionPool connectionPool,
            Long second
    ) {
        this(category, connectionPool);
        this.saveSecond = second;
    }

    @Override
    public void add(Long id, Object value) {
        String key = toKey(category, id);

        redisTemplate.add(key, objectToString(value), saveSecond);
    }

    @Override
    public Optional getOrSelect(Long subjectId, Select select) {
        return getOrSelect(toKey(category, subjectId), select);
    }

    @Override
    public Optional getListOrSelect(Long subjectId, Long selectId, Select select) {
        return getOrSelect(toKey(category, subjectId, selectId), select);
    }

    private Optional getOrSelect(String key, Select select) {
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
}
