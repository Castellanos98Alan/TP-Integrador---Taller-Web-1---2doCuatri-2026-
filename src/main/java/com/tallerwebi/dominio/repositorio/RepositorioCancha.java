package com.tallerwebi.dominio.repositorio;

import com.tallerwebi.dominio.Cancha;
import java.util.List;

public interface RepositorioCancha {
  Cancha buscarPorNombre(String nombre);

  List<Cancha> buscarTodas();
  List<Cancha> buscarDestacadas(Integer limite);
  List<String> buscarLocalidades();
}
