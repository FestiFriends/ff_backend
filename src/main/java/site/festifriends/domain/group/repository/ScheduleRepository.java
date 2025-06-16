package site.festifriends.domain.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.festifriends.entity.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, Long>, ScheduleRepositoryCustom {

}
