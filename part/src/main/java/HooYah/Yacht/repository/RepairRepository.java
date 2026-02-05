package HooYah.Yacht.repository;

import HooYah.Yacht.domain.Repair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RepairRepository extends JpaRepository<Repair, Long> {
    @Query("select r from Repair r where r.part.id = :partId")
    List<Repair> findRepairListByPart (@Param("partId") Long partId);
    @Query("select r from Repair r where r.part.id = :partId order by r.repairDate desc limit 1")
    Optional<Repair> findByIdOrderByRepairDateDesc(@Param("partId") Long partId);

    @Query(value = """
            select 
                t.*
            from
            ( 
                select repair.* , ROW_NUMBER() OVER (PARTITION BY repair.part_id ORDER BY repair_date DESC) last_date
                from repair
                where repair.part_id in :partIdList
            ) t
            where t.last_date = 1    
            """, nativeQuery = true)
    List<Repair> findAllLastRepair(@Param("partIdList") List<Long> partIdList);

}
