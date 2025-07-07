package com.ricocan.dms.repository;

import com.ricocan.dms.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findByUsuarioId(Long usuarioId);

    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE evento AUTO_INCREMENT = 1", nativeQuery = true)
    void resetAutoIncrement();

    @Modifying
    @Transactional
    @Query("UPDATE Evento e SET e.estado = :estado WHERE e.id = :id")
    void actualizarEstado(Long id, String estado);

    // ✅ Nuevo método: obtener eventos por lista de áreas
    List<Evento> findByAreaIn(List<String> areas);
}
