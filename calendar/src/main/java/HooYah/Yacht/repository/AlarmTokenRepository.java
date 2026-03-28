package HooYah.Yacht.repository;

import HooYah.Yacht.domain.AlarmToken;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlarmTokenRepository extends JpaRepository<AlarmToken, Long> {
    Optional<AlarmToken> findByUserId(Long userId);
    List<AlarmToken> findAllByUserIdIn(List<Long> userIdList);

    void deleteByUserId(Long userId);
}
