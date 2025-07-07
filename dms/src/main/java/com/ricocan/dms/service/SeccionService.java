package com.ricocan.dms.service;


import com.ricocan.dms.model.Seccion;
import com.ricocan.dms.repository.SeccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeccionService {

    @Autowired
    private SeccionRepository seccionRepository;
    
    public List<Seccion> listarSeccionTodas() {
        return seccionRepository.findAll();
    }

    public List<Seccion> listarPorArea(int idArea) {
        return seccionRepository.findByIdArea(idArea);
    }
}
