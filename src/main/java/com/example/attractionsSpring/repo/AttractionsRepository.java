package com.example.attractionsSpring.repo;

import com.example.attractionsSpring.model.Attraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;



public interface AttractionsRepository extends JpaRepository<Attraction,Long> {
}
