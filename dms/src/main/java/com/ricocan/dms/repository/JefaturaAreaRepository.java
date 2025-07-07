package com.ricocan.dms.repository;

import com.ricocan.dms.model.JefaturaArea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JefaturaAreaRepository extends JpaRepository<JefaturaArea, Long> {
    List<JefaturaArea> findByUsuarioId(Long usuarioId);
}
