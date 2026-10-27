package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.Partido;

public interface ServicioBalanceoEquipo {
  void balancearEquipos(Partido partido);

  void asignarCapitanes(Partido partido);
}
