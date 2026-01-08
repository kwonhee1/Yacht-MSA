package HooYah.Yacht.repository;

import HooYah.Yacht.domain.Calendar;
import HooYah.Yacht.domain.CalendarType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {
    List<Calendar> findByPartId(Long partId);

    @Query("select c from Calendar c where c.yachtId in :yachtIdList order by c.startDate")
    List<Calendar> findAllByYachtOrderByStartDate(@Param("yachtIdList") List<Long> yachtIdList);
}

