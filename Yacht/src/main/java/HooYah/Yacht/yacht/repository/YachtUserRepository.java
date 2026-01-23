package HooYah.Yacht.yacht.repository;

import HooYah.Yacht.yacht.domain.YachtUser;
import HooYah.Yacht.yacht.domain.Yacht;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface YachtUserRepository extends JpaRepository<YachtUser,Integer> {

    @Query("select yu.yacht from YachtUser yu where yu.userId = :userId")
    List<Yacht> findYachtListByUser(@Param("userId") Long userId);

    @Query("select yu.yacht from YachtUser yu where yu.yacht.id = :yachtId and yu.userId = :userId")
    Optional<Yacht> findYacht(@Param("yachtId") Long yachtId, @Param("userId") Long userId);

    @Query("select yu.yacht from YachtUser yu where yu.userId = :userId")
    List<Yacht> findAllYachtByUserId(Long userId);
}
