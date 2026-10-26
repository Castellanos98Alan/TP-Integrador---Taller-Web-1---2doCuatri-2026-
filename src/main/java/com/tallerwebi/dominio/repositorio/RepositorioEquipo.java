package com.tallerwebi.dominio.repositorio;

import com.tallerwebi.dominio.Equipo;

public interface RepositorioEquipo {

    void guardar(Equipo equipo);

    Equipo buscarPorId(Long id);


}
