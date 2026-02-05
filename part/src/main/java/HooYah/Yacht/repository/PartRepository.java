package HooYah.Yacht.repository;

import HooYah.Yacht.domain.Part;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PartRepository extends JpaRepository<Part, Long> {

    @Query("select p from Part p where p.yachtId = :yachtId")
    List<Part> findPartListByYacht(@Param("yachtId") Long yachtId);

}
