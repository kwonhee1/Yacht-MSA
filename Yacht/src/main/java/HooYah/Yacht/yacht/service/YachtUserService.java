package HooYah.Yacht.yacht.service;

import HooYah.Yacht.common.excetion.CustomException;
import HooYah.Yacht.common.excetion.ErrorCode;
import HooYah.Yacht.yacht.domain.Yacht;
import HooYah.Yacht.yacht.dto.response.ResponseYachtDto;
import HooYah.Yacht.yacht.repository.YachtRepository;
import HooYah.Yacht.yacht.domain.YachtUser;
import HooYah.Yacht.yacht.repository.YachtUserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class YachtUserService {

    private final YachtRepository yachtRepository;
    private final YachtUserRepository yachtUserRepository;

    public List<ResponseYachtDto> yachtList(Long userId) {
        List<Yacht> yachtList = yachtUserRepository.findYachtListByUser(userId);
        return yachtList.stream().map(ResponseYachtDto::of).toList();
    }

    @Transactional
    public List<Long> yachtUserIdList(Long yachtId, Long userId) {
        Optional<Yacht> yacht = yachtUserRepository.findYacht(yachtId, userId);

        if(yacht.isEmpty())
            throw new CustomException(ErrorCode.NOT_FOUND);

        return yacht.get().getYachtUser()
                .stream()
                .map(YachtUser::getUserId)
                .toList();
    }

    public long getYachtCode(Long yachtId, Long userId) {
        Optional<Yacht> yacht = yachtUserRepository.findYacht(yachtId, userId);

        if(yacht.isEmpty())
            throw new CustomException(ErrorCode.NOT_FOUND);

        return toHash(yacht.get().getId());
    }

    @Transactional
    public void inviteYachtByHash(Long code, Long userId) {
        long yachtId = decodeHash(code);

        Yacht yacht = yachtRepository.findById(yachtId).orElseThrow(
                ()->new CustomException(ErrorCode.NOT_FOUND)
        );

        Optional<Yacht> yachtOptional = yachtUserRepository.findYacht(yacht.getId(), userId);
        if (yachtOptional.isPresent())
            throw new CustomException(ErrorCode.CONFLICT);

        yachtUserRepository.save(
                YachtUser
                        .builder()
                        .userId(userId)
                        .yacht(yacht)
                        .build()
        );
    }

    private long toHash(Long id) {
        return id; // 추후 hash 적용
    }

    private long decodeHash(Long hash) {
        return hash; // 추후 hash 적용
    }

}
