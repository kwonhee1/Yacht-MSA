package HooYah.Yacht.repository;

import HooYah.Yacht.domain.Alarm;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    @Query("select a "
            + "from Alarm a "
            + "where a.yachtId in :yachtIds "
            + "order by a.date asc")
    List<Alarm> findAllByYachtIds(@Param("yachtIds") List<Long> yachtIds, OffsetDateTime date);

    void deleteAllByPartId(Long partId);

    @Query("""
        SELECT a
        FROM Alarm a
        WHERE
            (a.date >= :nowStart AND a.date < :nowEnd)
            OR (a.date >= :oneDayStart AND a.date < :oneDayEnd)
            OR (a.date >= :oneWeekStart AND a.date < :oneWeekEnd)
    """)
    List<Alarm> findAllByDate(
            @Param("todayStart") OffsetDateTime todayStart, @Param("todayEnd")   OffsetDateTime todayEnd,
            @Param("oneDayStart") OffsetDateTime oneDayStart, @Param("oneDayEnd")   OffsetDateTime oneDayEnd,
            @Param("oneWeekStart") OffsetDateTime oneWeekStart, @Param("oneWeekEnd")   OffsetDateTime oneWeekEnd
    );
}
