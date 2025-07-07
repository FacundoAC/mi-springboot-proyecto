package com.ricocan.dms.service;

import com.ricocan.dms.model.Area;
import com.ricocan.dms.repository.AreaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AreaService {

    @Autowired
    private AreaRepository areaRepository;

    public List<Area> listarTodas() {
        return areaRepository.findAll();
    }

    public List<Area> listarPorIdPlanta(int idPlanta) {
        return areaRepository.findByPlanta_Id(idPlanta);
    }

}
