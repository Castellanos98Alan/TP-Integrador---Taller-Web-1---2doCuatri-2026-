package com.tallerwebi.dominio;

import com.tallerwebi.presentacion.DatosCrearPartido;
import org.springframework.stereotype.Service;

@Service
public class ServicioPartidoImpl implements ServicioPartido {

  @Override
  public void crearPartido(DatosCrearPartido datos, Usuario creador) {
    // Lo hice para q no falle la inyección del contenedor Spring

    // mas adelante va a tener la funcion en el sistema de:
    // el partido en la base de datos
    // Controlar el cupo y los suplentes
    // Armado automático de equipos equilibrados
    // Carga y  validación del resultado final
  }
}
