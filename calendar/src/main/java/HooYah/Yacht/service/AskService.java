package HooYah.Yacht.service;

import HooYah.Redis.CacheService;
import HooYah.Yacht.excetion.CustomException;
import HooYah.Yacht.excetion.ErrorCode;
import HooYah.Yacht.webclient.WebClient;
import HooYah.Yacht.webclient.WebClient.HttpMethod;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AskService {

    private final CacheService userCacheService;
    private final CacheService yachtCacheService;
    private final CacheService partCacheService;

    private final CacheService inMemoryUserCacheService;

    private final WebClient webClient;

    @Value("${web-client.gateway}")
    private String gatewayURL;
    private Shared shared = new Shared();

    // user
    @Value("${web-client.user-list}")
    private String userListURI;
    public List<?> getUserInfoList(List<Long> userIdList) {
        return userCacheService.getListOrSelect(
                userIdList,
                ()-> (List) webClient.webClient(userListURI, HttpMethod.POST, userIdList)
        );
    }
    public List<List<?>> getUserInfoListList(List<List<Long>> userIdList) {
        return shared.getListList(
                userIdList,
                userCacheService,
                (distinctIdList)->(List) webClient.webClient(userListURI, HttpMethod.POST, distinctIdList)
        );
    }

    // yacht
    @Value("${web-client.validate-yacht}")
    private String validateYachtURI;
    @Value("${web-client.yacht-list}")
    private String yachtListURI;
    public Object validateYachtUser(Long yachtId, Long userId) {
        String uri = String.format(validateYachtURI, yachtId, userId);

        Object yachtUser = yachtCacheService.getOrSelect(
                yachtId, userId,
                ()-> webClient.webClient(uri, HttpMethod.GET, null)
        );

        if(yachtUser == null)
            throw new CustomException(ErrorCode.CONFLICT);

        return yachtUser; // yachtInfo
    }
    public List<?> getYachtInfoList(List<Long> yachtIdList) {
        return yachtCacheService.getListOrSelect(
                yachtIdList,
                ()->(List) webClient.webClient(gatewayURL+yachtListURI, HttpMethod.POST, yachtIdList)
        );
    }

    // part
    @Value("${web-client.part-info}")
    private String partInfoURI;
    @Value("${web-client.part-list")
    private String partListURI;

    public Object validatePart(Long partId) {
        String uri = String.format(partInfoURI, partId);

        Object partDto = partCacheService.getOrSelect(
                partId,
                ()-> webClient.webClient(uri, HttpMethod.GET, null)
        );

        if(partDto == null)
            throw new CustomException(ErrorCode.CONFLICT);

        return partDto;
    }
    public List<?> getPartInfoList(List<Long> partIdList) {
        return (List) partCacheService.getListOrSelect(
                partIdList,
                ()-> (List) webClient.webClient(gatewayURL + partListURI, HttpMethod.POST, partIdList)
        );
    }

    // inMemory
    public List<Long> yachtListInMemory(Long userId) {
        // get yacht list (관리하는 모든 yacht List 조회)
        return (List<Long>) inMemoryUserCacheService.getOrSelect(
                userId,
                ()-> { throw new CustomException(ErrorCode.NOT_FOUND); }
        );
    }

    class Shared {
        public Shared() {}
        public List<List<?>> getListList(
                List<List<Long>> idList,
                CacheService cacheService,
                SelectWithDistinctIdList distinctSelect
        ) {
            List<Long> totalIdList = new ArrayList<>();
            List<Long> distinctIdList;

            for(List<Long> eachList : idList)
                totalIdList.addAll(eachList);

            distinctIdList = totalIdList.stream().distinct().toList();

            // select distinctIdList (for caching All Data from Redis)
            cacheService.getListOrSelect(
                    distinctIdList,
                    ()->distinctSelect.select(distinctIdList)
            );

            // select Total List (from cached Data)
            List totalDataList = cacheService.getListOrSelect(
                    totalIdList,
                    ()-> {throw new CustomException(ErrorCode.API_FAIL, "Redis Does Not Cached");}
            );

            // validate Redis response
            if(totalDataList.size() != totalIdList.size())
                throw new CustomException(ErrorCode.CONFLICT);

            List<List<?>> result = new ArrayList<>(idList.size());
            // indexing Redis Response to Response
            int startIndex = 0;
            for(int y = 0; y < idList.size(); y++) {
                int endIndex = startIndex + idList.get(y).size();
                result.add(y, totalDataList.subList(startIndex, endIndex));
                startIndex = endIndex;
            }

            return result;
        }

        @FunctionalInterface
        interface SelectWithDistinctIdList {
            List select(List<Long> distinctIdList);
        }

    }

}
