package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Hero;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Hero entity.
 */
@SuppressWarnings("unused")
@Repository
public interface HeroRepository extends JpaRepository<Hero, Long> {}
