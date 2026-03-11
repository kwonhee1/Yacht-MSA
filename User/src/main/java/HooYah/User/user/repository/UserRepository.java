package HooYah.User.user.repository;

import HooYah.User.user.domain.User;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")}) // timeout 3초
    @Query("select u from User u where u.email = :email")
    Optional<User> findByEmailWithLock(@Param("email") String email);

    @Query("select u from User u where u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);
}

