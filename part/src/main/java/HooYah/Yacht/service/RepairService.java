package HooYah.Yacht.service;

import HooYah.Redis.CacheService;
import HooYah.Yacht.domain.Part;
import HooYah.Yacht.domain.Repair;
import HooYah.Yacht.event.DeletedEvent;
import HooYah.Yacht.event.NextRepairDateChangedEvent;
import HooYah.Yacht.excetion.CustomException;
import HooYah.Yacht.excetion.ErrorCode;
import HooYah.Yacht.publisher.MessagePublisher;
import HooYah.Yacht.repository.PartRepository;
import HooYah.Yacht.repository.RepairRepository;
import HooYah.Yacht.webclient.WebClient;
import HooYah.Yacht.webclient.WebClient.HttpMethod;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
public class RepairService {

    private final RepairRepository repairRepository;
    private final PartRepository partRepository;

    private final CacheService yachtCacheService;
    private final WebClient webClient;

    private final TransactionTemplate transactionTemplate;
    private final MessagePublisher<NextRepairDateChangedEvent> nextRepairDateChangedMessagePublisher;

    @Value("${web-client.gateway}")
    private String gatewayURL;
    @Value("${web-client.yacht-user}")
    private String yachtUserURI;

    private final MessagePublisher<DeletedEvent> repairDeleteMessagePublisher;

    public List<Repair> getRepairListByPart(
            Long partId, Long userId
    ) {
        Part part = partRepository.findById(partId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND)
        );

        // validate User in Yacht
        validateYachtUser(part.getYachtId(), userId);

        return repairRepository.findRepairListByPart(partId);
    }

    public Repair addRepair(Long partId, String content, OffsetDateTime repairDate, Long userId) {
        Part part = partRepository.findById(partId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND)
        );
        validateYachtUser(part.getYachtId(), userId);

        Repair newRepair = addRepair(part, content, repairDate, userId);

        publishLastRepairUpdateEvent(part, newRepair, userId);

        return newRepair;
    }

    public Repair addRepair(Part part, String content, OffsetDateTime repairDate, Long userId) {
        Repair newRepair = repairRepository.save( // only one query, not need transaction
                Repair
                        .builder()
                        .part(part)
                        .userId(userId)
                        .repairDate(repairDate)
                        .content(content)
                        .build()
        );

        return newRepair;
    }

    // used from addPartList (proxy api)
    public void addRepairList(List<Part> partList, List<OffsetDateTime> repairDateList, Long userId) {
        List<Repair> repairList = new ArrayList<>();

        for (int i = 0; i < repairDateList.size(); i++) {
            repairList.add(Repair
                    .builder()
                    .part(partList.get(i))
                    .repairDate(repairDateList.get(i))
                    .content("auto generated")
                    .build()
            );
        }

        List<Repair> createdRepairList = repairRepository.saveAll(repairList);

        createdRepairList.forEach(repair -> nextRepairDateChangedMessagePublisher.publish(
                new NextRepairDateChangedEvent(
                    userId,
                    repair.getPart(),
                    repair.getRepairDate())
        ));
    }

    public Repair updateRepair(Long repairId, String content, OffsetDateTime updateDate, Long userId) {
        Repair repair = repairRepository.findById(repairId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND)
        );
        Part part = repair.getPart();

        validateYachtUser(part.getYachtId(), userId);

        repair.updateContent(content);
        boolean isUpdateRepairDate = repair.updateRepairDate(updateDate);

        repairRepository.save(repair);

        if (isUpdateRepairDate)
            publishLastRepairUpdateEvent(part, repair, userId);

        return repair;
    }

    public void deleteRepair(Long repairId, Long userId) {
        Repair repair = repairRepository.findById(repairId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND)
        );
        Part part = repair.getPart();

        validateYachtUser(part.getYachtId(), userId);

        transactionTemplate.executeWithoutResult((status)->
                repairRepository.delete(repair)
        );

        repairDeleteMessagePublisher.publish(new DeletedEvent(repairId, userId));
    }

    private void validateYachtUser(Long yachtId, Long userId) {
        String uri = String.format(gatewayURL + yachtUserURI, yachtId, userId);

        Object yachtUser = yachtCacheService.getOrSelect(
                yachtId, userId,
                () -> webClient.webClient(uri, HttpMethod.GET, null).toMap()
        );

        if (yachtUser == null) {
            throw new CustomException(ErrorCode.CONFLICT);
        }
    }

    private void publishLastRepairUpdateEvent(Part part, Repair createdRepair, Long userId) {
        Optional<Repair> lastRepairOpt = repairRepository.findByIdOrderByRepairDateDesc(part.getId());
        if(lastRepairOpt.isEmpty())
            return;

        if(!lastRepairOpt.get().equals(createdRepair))
            return;

        Repair lastRepair = lastRepairOpt.get();
        nextRepairDateChangedMessagePublisher.publish(new NextRepairDateChangedEvent(userId, part, lastRepair.getRepairDate()));
    }

}
