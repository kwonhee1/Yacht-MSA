package HooYah.Yacht.service;

import HooYah.Redis.CacheService;
import HooYah.Redis.CacheService.Select;
import HooYah.Yacht.part.controller.PartController;
import HooYah.Yacht.part.repository.PartRepository;
import HooYah.Yacht.part.service.PartService;
import HooYah.Yacht.repair.repository.RepairRepository;
import HooYah.Yacht.webclient.WebClient;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class PartListTest {

    @Autowired
    private PartRepository partRepository;
    @Autowired
    private RepairRepository repairRepository;

    private CacheService cacheService = Mockito.mock(CacheService.class);
    private WebClient webClient = Mockito.mock(WebClient.class);

    private PartService partService;

    @Test
    @Transactional
    public void partListTest() {
        Mockito.when(cacheService.getOrSelect(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(Select.class)))
                .thenReturn(Map.of("id", 1L));
        partService = new PartService(partRepository, repairRepository, cacheService, webClient);

        partService.getPartListByYacht(2L, 1L);
    }

}
