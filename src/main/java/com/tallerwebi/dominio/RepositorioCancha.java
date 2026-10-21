package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioCancha {
  Cancha buscarPorNombre(String nombre);

  List<Cancha> buscarTodas();

  List<String> buscarLocalidades();
}
