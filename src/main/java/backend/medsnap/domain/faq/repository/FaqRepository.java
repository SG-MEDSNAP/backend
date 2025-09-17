package backend.medsnap.domain.faq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import backend.medsnap.domain.faq.entity.Faq;

@Repository
public interface FaqRepository extends JpaRepository<Faq, Long> {}
