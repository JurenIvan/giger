package hr.fer.zemris.opp.giger.repository;

import hr.fer.zemris.opp.giger.domain.Organizer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizerRepository extends JpaRepository<Organizer,Long> {
}
