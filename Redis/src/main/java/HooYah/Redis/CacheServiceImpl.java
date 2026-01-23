package HooYah.Redis;

import HooYah.Redis.connection.SaveSecond;
import HooYah.Redis.pool.JedisPool;
import HooYah.Redis.pool.Pool;
import HooYah.Redis.template.TemplateImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CacheServiceImpl<T> implements CacheService<T> {

    private final String category;
    private final TemplateImpl templateImpl;
    private final Class<T> type;

    /*
        ObjectMapper must init in Redis library
        외부에서 주입을 받게 되면 -> 입력될 때 사용되는 ObjectMapper와 출력될때 사용되는 ObjectMapper 버전 차이로 인해 작동하지 않을 수있음!
     */
    private final ObjectMapper objectMapper;

    private SaveSecond saveSecond = new SaveSecond(3600L); // default value 1 hour

    public CacheServiceImpl(
            String category,
            Pool pool,
            Class<T> type
    ) {
        this.templateImpl = new TemplateImpl(pool);
        this.category = category;
        this.type = type;
        this.objectMapper = initObjectMapper();
    }

    public CacheServiceImpl(
            String category,
            JedisPool jedisPool,
            Long second,
            Class<T> type
    ) {
        this(category, jedisPool, type);
        this.saveSecond = new SaveSecond(second);
    }

    private ObjectMapper initObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.USE_LONG_FOR_INTS);
        return objectMapper;
    }

    @Override
    public void add(Long id, T value) {
        String key = toKey(category, id);
        CacheValue cacheValue = CacheValue.ofSource(objectToString(value));

        templateImpl.add(key, cacheValue.getString(), saveSecond);
    }

    @Override
    public T getOrSelect(Long selectId, CacheService.Select<T> select) {
        return getOrSelect(toKey(category, selectId), select);
    }

    @Override
    public T getOrSelect(Long subjectId, Long selectId, CacheService.Select<T> select) {
        return getOrSelect(toKey(category, subjectId, selectId), select);
    }

    private T getOrSelect(String key, CacheService.Select<T> select) {
        String cacheString = templateImpl.get(key, saveSecond);
        CacheValue cacheValue = CacheValue.ofSaved(cacheString);

        if(cacheValue.hasValue())
            return stringToObject(cacheValue.getString());

        // not value in redis, need select
        T selectedData = select.select();

        CacheValue newValue = CacheValue.ofSource(objectToString(selectedData));
        templateImpl.add(key, newValue.getString(), saveSecond);

        return selectedData;
    }

    @Override
    public List<T> getListOrSelect(Long subjectId, List<Long> selectIdList, CacheService.Select<List<T>> select) {
        List<String> keyList = selectIdList
                        .stream()
                        .map((id)->toKey(category, subjectId, id))
                        .toList();
        return getListOrSelectPrivate(keyList, select);
    }

    @Override
    public List<T> getListOrSelect(List<Long> selectIdList, CacheService.Select<List<T>> select) {
        List<String> keyList = selectIdList
                .stream()
                .map((id)-> toKey(category, id))
                .toList();
        return getListOrSelectPrivate(keyList, select);
    }

    // todo : method name
    private List<T> getListOrSelectPrivate(List<String> keyList, CacheService.Select<List<T>> select) {
        List<CacheValue> cacheValueList =
                templateImpl.getList(keyList, saveSecond)
                    .stream()
                    .map(CacheValue::ofSaved)
                    .toList();

        // check contain unKnown -> if contains must select
        boolean containsUnKnown = false;
        for(CacheValue cacheValue : cacheValueList)
            if(cacheValue.isUnKnown())
                containsUnKnown = true;

        if(!containsUnKnown) {
            // convert String List to Value List and return
            return cacheValueList
                    .stream()
                    .map(cache -> stringToObject(cache.getString()))
                    .toList();
        }

        // if contains unKnown -> must select all
        List<T> selectedData = select.select();

        List<String> writeDataList =
                selectedData
                        .stream()
                        .map(data -> CacheValue.ofSource(objectToString(data))) // List<Object> -> List<CacheValue>
                        .map(cacheValue -> cacheValue.getString()) // List<CacheValue> -> List<String>
                        .toList();

        templateImpl.addAll(keyList, writeDataList, saveSecond);
        return selectedData;
    }

    private String toKey(String category, Long... id) {
        StringBuilder key = new StringBuilder(category);

        for(Long i : id) {
            key.append("-");
            key.append(i);
        }

        return key.toString();
    }

    private String objectToString(Object value) {
        if(value == null)
            return null;
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RedisException(e.getMessage());
        }
    }

    private T stringToObject(String value) {
        if(value == null ||  value.isEmpty())
            return null;
        try {
            return objectMapper.readValue(value, type);
        } catch (JsonProcessingException e) {
            throw new RedisException(e.getMessage());
        }
    }

}

class CacheValue {

    public final static String NULL = "NULL";

    private final String value;
    private final Status status;

    /*
        convert saved data (from Redis or InMemory Map, ... etc) to CacheValue

        @Param value : saved Serialized data
     */
    public static CacheValue ofSaved(String value) {
        if (value == null || value.isEmpty())
            return new CacheValue(null, Status.UN_KNOWN);
        else if (value.equals(NULL))
            return new CacheValue(null, Status.NULL);
        else
            return new CacheValue(value, Status.EXIST);
    }

    /*
        convert external data to CacheValue

        @Param value : Serialized external data
    */
    public static CacheValue ofSource(String value) {
        if (value == null)
            return new CacheValue(NULL, Status.NULL);
        else {
            return new CacheValue(value, Status.EXIST);
        }
    }

    private CacheValue(String value, Status status) {
        this.value = value;
        this.status = status;
    }

    public String getString() {
        if (status == Status.UN_KNOWN)
            throw new RuntimeException("No Value!");
        return value;
    }

    public boolean isUnKnown() {
        return status == Status.UN_KNOWN;
    }

    public boolean hasValue() {
        return status != Status.UN_KNOWN;
    }

    public boolean isNull() {
        return status == Status.NULL;
    }

    enum Status {
        EXIST,
        NULL,
        UN_KNOWN // need select
    }

}
