package HooYah.Yacht.yacht.repository;

import HooYah.Yacht.yacht.domain.Yacht;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface YachtRepository extends JpaRepository<Yacht, Long> {

    Optional<Yacht> findByInviteCode(String inviteCode);

    @Query("select y.id from Yacht y "
            + "left join YachtUser yu on yu.yacht.id = y.id "
            + "where yu.id is null")
    List<Long> findAllEmptyYacht();

    // proxy
    @Query("select y from Yacht y where y.id in :idList")
    List<Yacht> findAllWithUserById (@Param("idList") List<Long> idList);

}
