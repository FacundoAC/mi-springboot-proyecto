package com.ricocan.dms.repository;

import com.ricocan.dms.model.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AreaRepository extends JpaRepository<Area, Integer> {
	List<Area> findByPlanta_Id(int idPlanta);

}
