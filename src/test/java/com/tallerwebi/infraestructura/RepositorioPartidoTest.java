package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.tallerwebi.dominio.Cancha;
import com.tallerwebi.dominio.Partido;
import com.tallerwebi.dominio.RepositorioPartido;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.infraestructura.config.HibernateInfraestructuraTestConfig;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { HibernateInfraestructuraTestConfig.class })
public class RepositorioPartidoTest {

  public static final String FILTRO_TODOS = "TODOS";

  @Autowired
  private SessionFactory sessionFactory;

  private RepositorioPartido repositorioPartido;

  @BeforeEach
  public void init() {
    repositorioPartido = new RepositorioPartidoImpl(sessionFactory);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaGuardarUnPartidoYBuscarloPorId() {
    // Given
    Partido partido = givenTengoUnPartido("PRINCIPIANTE", 10);

    // When
    repositorioPartido.guardar(partido);
    Partido partidoObtenido = repositorioPartido.buscarPorId(partido.getId());

    // Then
    assertThat(partidoObtenido.getNivel(), is(equalTo("PRINCIPIANTE")));
    assertThat(partidoObtenido.getCupoMaximo(), is(equalTo(10)));
    assertThat(partidoObtenido.getId(), is(equalTo(partido.getId())));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaListarPartidosSegunFiltro() {
    // preparación
    Cancha cancha = new Cancha();

    cancha.setNombre("Cancha de fútbol los leones");

    sessionFactory.getCurrentSession().persist(cancha);

    Partido partido01 = new Partido();
    partido01.setEsPrivado(false);
    partido01.setNivel("PRINCIPIANTE");
    partido01.setCupoMaximo(22);
    partido01.setCancha(cancha);
    partido01.setCodigoDeAcceso("123");
    partido01.setDistanciaKm(5);
    LocalDateTime fechaFalsa = LocalDateTime.of(2026, 10, 2, 14, 30);
    partido01.setFecha(fechaFalsa);

    Partido partido02 = new Partido();
    partido02.setEsPrivado(true);
    partido02.setNivel("INTERMEDIO");
    partido02.setCupoMaximo(22);
    partido02.setCancha(cancha);
    LocalDateTime fechaFalsa2 = LocalDateTime.of(2026, 10, 2, 18, 00);
    partido02.setFecha(fechaFalsa2);
    partido02.setDistanciaKm(3);

    repositorioPartido.guardar(partido01);
    repositorioPartido.guardar(partido02);

    // ejecución
    List<Partido> partidosObtenidos = whenObtenerPartidosPorFiltro(
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS
    );

    // validación
    assertThat(partidosObtenidos.size(), equalTo(2));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaGuardarUnPartidoConCanchaYOrganizador() {
    // Given
    Cancha cancha = givenExisteUnaCancha("Cancha Los Leones");
    Usuario organizador = givenExisteUnUsuario("organizador@test.com");

    Partido partido = givenTengoUnPartido("INTERMEDIO", 22);
    partido.setCancha(cancha);
    partido.setOrganizador(organizador);

    // When
    repositorioPartido.guardar(partido);
    Partido partidoObtenido = repositorioPartido.buscarPorId(partido.getId());

    // Then
    assertThat(partidoObtenido.getCancha().getNombre(), is(equalTo("Cancha Los Leones")));
    assertThat(partidoObtenido.getOrganizador().getEmail(), is(equalTo("organizador@test.com")));
  }

  //metodps

  private Partido givenTengoUnPartido(String nivel, Integer cupo) {
    Partido partido = new Partido();
    partido.setNivel(nivel);
    partido.setCupoMaximo(cupo);
    partido.setFecha(LocalDateTime.of(2026, 10, 20, 18, 0));
    return partido;
  }

  private Cancha givenExisteUnaCancha(String nombre) {
    Cancha cancha = new Cancha();
    cancha.setNombre(nombre);
    sessionFactory.getCurrentSession().persist(cancha);
    return cancha;
  }

  private Usuario givenExisteUnUsuario(String email) {
    Usuario usuario = new Usuario();
    usuario.setEmail(email);
    usuario.setPassword("1234");
    usuario.setRol("Organizador");
    sessionFactory.getCurrentSession().persist(usuario);
    return usuario;
  }

  @Transactional
  @Rollback
  @Test
  public void deberiaFiltrarPartidosPorNivelYProvincia() {
    // 1. Arrange (Preparación)
    // Cancha en la provincia objetivo
    Cancha canchaBuenosAires = new Cancha();
    canchaBuenosAires.setNombre("Cancha BA");
    canchaBuenosAires.setProvincia("BUENOS AIRES");
    sessionFactory.getCurrentSession().persist(canchaBuenosAires);

    Cancha canchaCordoba = new Cancha();
    canchaCordoba.setNombre("Cancha Cordoba");
    canchaCordoba.setProvincia("CORDOBA");
    sessionFactory.getCurrentSession().persist(canchaCordoba);

    Partido partidoCoincide = new Partido();
    partidoCoincide.setNivel("PRINCIPIANTE");
    partidoCoincide.setCancha(canchaBuenosAires);
    repositorioPartido.guardar(partidoCoincide);

    Partido partidoOtraProvincia = new Partido();
    partidoOtraProvincia.setNivel("PRINCIPIANTE");
    partidoOtraProvincia.setCancha(canchaCordoba);
    repositorioPartido.guardar(partidoOtraProvincia);

    Partido partidoOtroNivel = new Partido();
    partidoOtroNivel.setNivel("AVANZADO");
    partidoOtroNivel.setCancha(canchaBuenosAires);
    repositorioPartido.guardar(partidoOtroNivel);

    // 2. Act (Ejecución)
    List<Partido> resultado = whenObtenerPartidosPorFiltro(
      "PRINCIPIANTE",
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      "BUENOS AIRES",
      FILTRO_TODOS
    );

    // 3. Assert (Validación)
    assertThat(resultado.size(), equalTo(1));
    assertThat(resultado.get(0).getNivel(), equalTo("PRINCIPIANTE"));
    assertThat(resultado.get(0).getCancha().getProvincia(), equalTo("BUENOS AIRES"));
  }

  @Transactional
  @Rollback
  @Test
  public void deberiaFiltrarPartidosPorNivelYLocalidad() {
    // 1. Arrange (Preparación)
    // Cancha en la provincia objetivo
    Cancha canchaLaMatanza = new Cancha();
    canchaLaMatanza.setNombre("Cancha BA");
    canchaLaMatanza.setLocalidad("LA MATANZA");
    sessionFactory.getCurrentSession().persist(canchaLaMatanza);

    Cancha canchaCordoba = new Cancha();
    canchaCordoba.setNombre("Cancha Cordoba");
    canchaCordoba.setProvincia("CORDOBA");
    sessionFactory.getCurrentSession().persist(canchaCordoba);

    Partido partidoCoincide = new Partido();
    partidoCoincide.setNivel("PRINCIPIANTE");
    partidoCoincide.setCancha(canchaLaMatanza);
    repositorioPartido.guardar(partidoCoincide);

    Partido partidoOtraProvincia = new Partido();
    partidoOtraProvincia.setNivel("PRINCIPIANTE");
    partidoOtraProvincia.setCancha(canchaCordoba);
    repositorioPartido.guardar(partidoOtraProvincia);

    Partido partidoOtroNivel = new Partido();
    partidoOtroNivel.setNivel("AVANZADO");
    partidoOtroNivel.setCancha(canchaLaMatanza);
    repositorioPartido.guardar(partidoOtroNivel);

    // 2. Act (Ejecución)
    List<Partido> resultado = whenObtenerPartidosPorFiltro(
      "PRINCIPIANTE",
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      "LA MATANZA"
    );

    // 3. Assert (Validación)
    assertThat(resultado.size(), equalTo(1));
    assertThat(resultado.get(0).getNivel(), equalTo("PRINCIPIANTE"));
    assertThat(resultado.get(0).getCancha().getLocalidad(), equalTo("LA MATANZA"));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaFiltrarPartidosPorPrivacidadYCupo() {
    // 1. Preparación
    Cancha cancha = new Cancha();
    cancha.setNombre("Cancha de prueba 2");
    sessionFactory.getCurrentSession().persist(cancha);

    Partido partidoPrivado10 = new Partido();
    partidoPrivado10.setEsPrivado(true);
    partidoPrivado10.setCupoMaximo(10);
    partidoPrivado10.setCancha(cancha);

    Partido partidoPublico22 = new Partido();
    partidoPublico22.setEsPrivado(false);
    partidoPublico22.setCupoMaximo(22);
    partidoPublico22.setCancha(cancha);

    repositorioPartido.guardar(partidoPrivado10);
    repositorioPartido.guardar(partidoPublico22);

    // 2. Ejecución:
    List<Partido> resultado = whenObtenerPartidosPorFiltro(
      FILTRO_TODOS,
      "PRIVADO",
      "10",
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS,
      FILTRO_TODOS
    );

    // 3. Validación
    assertThat(resultado.size(), equalTo(1));
    assertThat(resultado.get(0).getEsPrivado(), equalTo(true));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaFiltrarPartidosPorFechaHoraYTipoPublico() {
    // 1. Preparación
    Cancha cancha = new Cancha();
    cancha.setNombre("Cancha de prueba 3");
    sessionFactory.getCurrentSession().persist(cancha);

    Partido partidoEspecifico = new Partido();
    partidoEspecifico.setEsPrivado(false);
    LocalDateTime fechaExacta = LocalDateTime.of(2026, 10, 2, 14, 30);
    partidoEspecifico.setFecha(fechaExacta);
    partidoEspecifico.setCancha(cancha);

    Partido partidoDistinto = new Partido();
    partidoDistinto.setEsPrivado(true);
    LocalDateTime fechaDistinta = LocalDateTime.of(2026, 11, 5, 20, 00);
    partidoDistinto.setFecha(fechaDistinta);
    partidoDistinto.setCancha(cancha);

    repositorioPartido.guardar(partidoEspecifico);
    repositorioPartido.guardar(partidoDistinto);

    // 2. Ejecución:
    List<Partido> resultado = whenObtenerPartidosPorFiltro(
      FILTRO_TODOS,
      "PUBLICO",
      FILTRO_TODOS,
      "2026-10-02",
      "14:30",
      FILTRO_TODOS,
      FILTRO_TODOS
    );

    // 3. Validación
    assertThat(resultado.size(), equalTo(1));
    assertThat(resultado.get(0).getEsPrivado(), equalTo(false));
  }

  private List<Partido> whenObtenerPartidosPorFiltro(
    String filtroTodo01,
    String filtroTodo02,
    String filtroTodo03,
    String filtroTodo04,
    String filtroTodo05,
    String filtroTodo06,
    String filtroTodo07
  ) {
    return repositorioPartido.buscarPartidosPorFiltro(
      filtroTodo01,
      filtroTodo02,
      filtroTodo03,
      filtroTodo04,
      filtroTodo05,
      filtroTodo06,
      filtroTodo07
    );
  }
}
