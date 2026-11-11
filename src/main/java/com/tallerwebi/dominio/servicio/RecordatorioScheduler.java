package com.tallerwebi.dominio.servicio;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RecordatorioScheduler {

  private final ServicioRecordatorio servicioRecordatorio;

  public RecordatorioScheduler(ServicioRecordatorio servicioRecordatorio) {
    this.servicioRecordatorio = servicioRecordatorio;
  }

  @Scheduled(cron = "0 * * * * *", zone = "America/Argentina/Buenos_Aires")
  public void revisarPartidos() {
    String habilitado = System.getenv("RECORDATORIOS_HABILITADOS");

    if (!"true".equalsIgnoreCase(habilitado)) {
      return;
    }

    LocalDateTime ahora = LocalDateTime
      .now(ZoneId.of("America/Argentina/Buenos_Aires"))
      .truncatedTo(ChronoUnit.MINUTES);

    servicioRecordatorio.enviarRecordatoriosPendientes(ahora);
  }
}
