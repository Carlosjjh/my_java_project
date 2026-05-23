package carlos.jiang.repository;

import carlos.jiang.model.AppUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    boolean existsByAccount(String account);

    Optional<AppUser> findByAccount(String account);
}
