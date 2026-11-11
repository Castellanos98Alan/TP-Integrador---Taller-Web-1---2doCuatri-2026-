package com.tallerwebi.dominio;

import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.servicio.RecordatorioScheduler;
import com.tallerwebi.dominio.servicio.ServicioRecordatorio;
import org.junit.jupiter.api.Test;

public class RecordatorioSchedulerTest {

  @Test
  public void queNoSeEnvienRecordatoriosSiEstaDeshabilitado() {
    ServicioRecordatorio servicioMock = mock(ServicioRecordatorio.class);

    RecordatorioScheduler scheduler = new RecordatorioScheduler(servicioMock);

    scheduler.revisarPartidos();

    verifyNoInteractions(servicioMock);
  }
}
