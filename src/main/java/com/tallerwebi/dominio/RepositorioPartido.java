package com.tallerwebi.dominio;

public interface RepositorioPartido {
    Partido buscarPorId(Long id);
    void guardar(Partido partido);
    void modificar(Partido partido);
}
