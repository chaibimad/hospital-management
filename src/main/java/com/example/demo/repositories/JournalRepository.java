package com.example.demo.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.models.Journal;

@Repository
public interface JournalRepository extends JpaRepository<Journal, Integer> {
    List<Journal> findAllByOrderByDateHeureDesc();

    @Query("SELECT j FROM Journal j WHERE (:user IS NULL OR j.utilisateurEmail = :user) " +
           "AND (:action IS NULL OR j.action = :action) " +
           "AND (:start IS NULL OR j.dateHeure >= :start) AND (:end IS NULL OR j.dateHeure <= :end) " +
           "ORDER BY j.dateHeure DESC")
    List<Journal> findFiltered(@Param("user") String utilisateurEmail, @Param("action") String action,
                              @Param("start") LocalDateTime dateDebut, @Param("end") LocalDateTime dateFin);
}