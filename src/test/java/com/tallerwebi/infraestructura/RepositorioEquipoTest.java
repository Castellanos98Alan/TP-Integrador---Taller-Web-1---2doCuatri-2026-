package com.tallerwebi.infraestructura;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Equipo;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RepositorioEquipoTest {

  private SessionFactory sessionFactoryMock;
  private Session sessionMock;
  private RepositorioEquipoImpl repositorioEquipo;

  @BeforeEach
  void setUp() {
    sessionFactoryMock = mock(SessionFactory.class);
    sessionMock = mock(Session.class);

    when(sessionFactoryMock.getCurrentSession()).thenReturn(sessionMock);

    repositorioEquipo = new RepositorioEquipoImpl(sessionFactoryMock);
  }

  @Test
  void guardarDeberiaPersistirElEquipo() {
    Equipo equipo = new Equipo();

    repositorioEquipo.guardar(equipo);

    verify(sessionMock).persist(equipo);
  }

  @Test
  void buscarPorIdDeberiaRetornarElEquipo() {
    Long id = 1L;
    Equipo equipoEsperado = new Equipo();

    when(sessionMock.get(Equipo.class, id)).thenReturn(equipoEsperado);

    Equipo resultado = repositorioEquipo.buscarPorId(id);

    assertSame(equipoEsperado, resultado);
    verify(sessionMock).get(Equipo.class, id);
  }

  @Test
  void buscarPorIdDeberiaRetornarNullCuandoNoExisteElEquipo() {
    Long id = 99L;

    when(sessionMock.get(Equipo.class, id)).thenReturn(null);

    Equipo resultado = repositorioEquipo.buscarPorId(id);

    assertSame(null, resultado);
    verify(sessionMock).get(Equipo.class, id);
  }
}
