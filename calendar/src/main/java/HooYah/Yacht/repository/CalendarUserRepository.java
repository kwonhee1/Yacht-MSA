package HooYah.Yacht.repository;

import HooYah.Yacht.domain.CalendarUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarUserRepository extends JpaRepository<CalendarUser, Long> {
    void deleteAllByUserId(Long userId);
}
