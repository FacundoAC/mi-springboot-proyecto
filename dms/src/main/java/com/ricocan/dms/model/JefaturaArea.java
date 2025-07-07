package com.ricocan.dms.model;

import jakarta.persistence.*;

@Entity
@Table(name = "jefatura_area")
public class JefaturaArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_area")
    private Area area;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Area getArea() { return area; }
    public void setArea(Area area) { this.area = area; }
}
