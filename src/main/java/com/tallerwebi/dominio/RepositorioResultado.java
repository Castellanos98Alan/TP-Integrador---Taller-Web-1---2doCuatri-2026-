package com.tallerwebi.dominio;

public interface RepositorioResultado {
  void guardar(Resultado resultado);

  void modificar(Resultado resultado);

  Resultado buscarPorId(Integer id);

  Resultado buscarPorPartido(Partido partido);
}
