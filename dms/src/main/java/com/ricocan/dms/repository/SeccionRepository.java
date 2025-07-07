package com.ricocan.dms.repository;

import com.ricocan.dms.model.Seccion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SeccionRepository extends JpaRepository<Seccion, Integer> {
    List<Seccion> findByIdArea(int idArea);
}
