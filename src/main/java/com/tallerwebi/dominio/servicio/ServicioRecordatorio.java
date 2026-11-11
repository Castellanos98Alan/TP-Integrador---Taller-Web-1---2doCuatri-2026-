package com.tallerwebi.dominio.servicio;

import java.time.LocalDateTime;

@FunctionalInterface
public interface ServicioRecordatorio {
  void enviarRecordatoriosPendientes(LocalDateTime ahora);
}
