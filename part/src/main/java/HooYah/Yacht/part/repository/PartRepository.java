package HooYah.Yacht.part.repository;

import HooYah.Yacht.part.domain.Part;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PartRepository extends JpaRepository<Part, Long> {

    @Query("select p from Part p where p.yachtId = :yachtId")
    List<Part> findPartListByYacht(@Param("yachtId") Long yachtId);

}
