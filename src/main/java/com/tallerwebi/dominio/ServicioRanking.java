package com.tallerwebi.dominio;

import java.util.List;

@FunctionalInterface
public interface ServicioRanking {
  List<Usuario> obtenerRanking();
}
