package HooYah.Yacht.yacht.service;

import HooYah.Yacht.event.DeletedEvent;
import HooYah.Yacht.excetion.CustomException;
import HooYah.Yacht.excetion.ErrorCode;
import HooYah.Yacht.publisher.MessagePublisher;
import HooYah.Yacht.yacht.domain.Yacht;
import HooYah.Yacht.yacht.dto.request.CreateYachtDto;
import HooYah.Yacht.yacht.dto.request.CreateYachtDto.YachtInfo;
import HooYah.Yacht.yacht.dto.request.UpdateYachtDto;
import HooYah.Yacht.yacht.event.YachtCreateEvent;
import HooYah.Yacht.yacht.repository.YachtRepository;
import HooYah.Yacht.yacht.domain.YachtUser;
import HooYah.Yacht.yacht.repository.YachtUserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
public class YachtService {

    private final YachtRepository yachtRepository;
    private final YachtUserRepository yachtUserRepository;

    private final TransactionTemplate transactionTemplate;

    private final MessagePublisher<YachtCreateEvent> yachtCreateMessagePublisher;
    private final MessagePublisher<DeletedEvent> yachtDeleteMessagePublisher;

    @Transactional
    public Yacht createYacht (CreateYachtDto dto, Long userId) {
        Yacht createdYacht = transactionTemplate.execute(status-> {
            return createYachtDomain(dto.getYacht(), userId);
        });

        if (dto.getPartList() != null && !dto.getPartList().isEmpty()) {
            yachtCreateMessagePublisher.publish(new YachtCreateEvent(createdYacht.getId(), userId, dto.getPartList()));
        }

        return createdYacht;
    }

    private Yacht createYachtDomain (YachtInfo yachtInfo, Long userId) {
        Yacht yacht = yachtRepository.save(Yacht
                .builder()
                .name(yachtInfo.getName())
                .nickName(yachtInfo.getNickName())
                .build()
        );
        yachtUserRepository.save(
                YachtUser
                        .builder()
                        .yacht(yacht)
                        .userId(userId)
                        .build()
        );
        return yacht;
    }

    @Transactional
    public Yacht updateYacht(Long userId, UpdateYachtDto dto) {
        Optional<Yacht> yachtOpt = yachtUserRepository.findYacht(dto.getId(), userId);

        if(yachtOpt.isEmpty())
            throw new CustomException(ErrorCode.NOT_FOUND);

        Yacht yacht = yachtOpt.get();

        yacht.updateName(dto.getName());
        yacht.updateNickName(dto.getNickName());

        return yacht;
    }

    public void deleteYacht(Long userId, Long yachtId) {
        transactionTemplate.executeWithoutResult(status -> {
            yachtUserRepository.findYacht(yachtId, userId).ifPresentOrElse(
                    (y)->yachtRepository.delete(y),
                    ()->{throw new CustomException(ErrorCode.NOT_FOUND);}
            );
        });

        yachtDeleteMessagePublisher.publish(new DeletedEvent(userId, yachtId));
    }

    public void deleteByUser(Long userId) {
        List<Long> emptyYachtList = transactionTemplate.execute(status -> {
            yachtUserRepository.deleteByUserId(userId);

            // delete all empty user yacht
            List<Long> emptyYachtIdList = yachtRepository.findAllEmptyYacht();
            yachtRepository.deleteAllById(emptyYachtIdList);

            return emptyYachtIdList;
        });

        emptyYachtList.forEach(yachtId -> {
            yachtDeleteMessagePublisher.publish(new DeletedEvent(yachtId, userId));
        });
    }

}

