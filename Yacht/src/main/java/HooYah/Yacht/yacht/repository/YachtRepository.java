package HooYah.Yacht.yacht.repository;

import HooYah.Yacht.yacht.domain.Yacht;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface YachtRepository extends JpaRepository<Yacht, Long> {

    // proxy
    @Query("select y from Yacht y where y.id in :idList")
    List<Yacht> findAllWithUserById (@Param("idList") List<Long> idList);

}
