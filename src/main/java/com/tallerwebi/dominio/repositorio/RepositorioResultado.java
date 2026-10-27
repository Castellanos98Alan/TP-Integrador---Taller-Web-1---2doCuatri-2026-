package com.tallerwebi.dominio.repositorio;

import com.tallerwebi.dominio.Partido;
import com.tallerwebi.dominio.Resultado;

public interface RepositorioResultado {
  void guardar(Resultado resultado);

  void modificar(Resultado resultado);

  Resultado buscarPorId(Integer id);

  Resultado buscarPorPartido(Partido partido);
}
