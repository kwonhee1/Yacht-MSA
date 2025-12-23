package HooYah.Yacht.yacht.service;

import HooYah.Yacht.common.excetion.CustomException;
import HooYah.Yacht.common.excetion.ErrorCode;
import HooYah.Yacht.yacht.domain.Yacht;
import HooYah.Yacht.yacht.dto.request.CreateYachtDto;
import HooYah.Yacht.yacht.dto.request.CreateYachtDto.YachtInfo;
import HooYah.Yacht.yacht.dto.request.UpdateYachtDto;
import HooYah.Yacht.yacht.repository.YachtRepository;
import HooYah.Yacht.yacht.domain.YachtUser;
import HooYah.Yacht.yacht.repository.YachtUserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class YachtService {

    private final YachtRepository yachtRepository;
    private final YachtUserRepository yachtUserRepository;

    @Transactional
    public Yacht createYacht (CreateYachtDto dto, Long userId) {
        Yacht createdYacht = createYacht(dto.getYacht(), userId);
        return createdYacht;
    }

    private Yacht createYacht (YachtInfo yachtInfo, Long userId) {
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

    @Transactional
    public void deleteYacht(Long userId, Long yachtId) {
        Optional<Yacht> yacht = yachtUserRepository.findYacht(yachtId, userId); // throw not found

        if(yacht.isEmpty())
            throw new CustomException(ErrorCode.NOT_FOUND);

        yachtRepository.delete(yacht.get());
    }

}
