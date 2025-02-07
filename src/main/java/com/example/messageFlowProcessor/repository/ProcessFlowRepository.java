package com.example.messageFlowProcessor.repository;

import org.springframework.stereotype.Repository;

import com.example.messageFlowProcessor.entity.ProcessFlow;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ProcessFlowRepository extends JpaRepository<ProcessFlow, String>{
	@Query("SELECT p FROM ProcessFlow p WHERE p.scenario = :scenario AND p.country = :country AND p.instance = :instance")
    Optional<ProcessFlow> findByKeyCountryInstance(@Param("scenario") String scenario, @Param("country") String country, @Param("instance") String instance);
	
}
