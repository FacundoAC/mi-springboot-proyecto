package com.ricocan.dms.service;

import com.ricocan.dms.model.Planta;
import com.ricocan.dms.repository.PlantaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class PlantaService {

	@Autowired
	private PlantaRepository plantaRepository;

	public List<Planta> obtenerTodas() {
		return plantaRepository.findAll();
	}

}
