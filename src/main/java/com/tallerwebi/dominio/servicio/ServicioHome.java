package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.Cancha;
import java.util.List;

@FunctionalInterface
public interface ServicioHome {
  List<Cancha> obtenerCanchasParaHome(Integer cantidad);
}
