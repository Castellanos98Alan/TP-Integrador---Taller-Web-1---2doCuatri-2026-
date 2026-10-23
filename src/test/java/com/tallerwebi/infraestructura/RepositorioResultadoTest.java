package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.tallerwebi.dominio.Partido;
import com.tallerwebi.dominio.Resultado;
import com.tallerwebi.dominio.repositorio.RepositorioPartido;
import com.tallerwebi.dominio.repositorio.RepositorioResultado;
import com.tallerwebi.infraestructura.config.HibernateInfraestructuraTestConfig;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
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
public class RepositorioResultadoTest {

  @Autowired
  private SessionFactory sessionFactory;

  private RepositorioResultado repositorioResultado;
  private RepositorioPartido repositorioPartido;

  @BeforeEach
  public void init() {
    repositorioResultado = new RepositorioResultadoImpl(sessionFactory);
    repositorioPartido = new RepositorioPartidoImpl(sessionFactory);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaBuscarUnResultadoPorId() {
    // Given
    Resultado resultado = givenDadoQueTengoUnResultado(2, 1);

    // When
    Resultado resultadoEncontrado = repositorioResultado.buscarPorId(resultado.getId());

    // Then
    assertThat(resultadoEncontrado.getId(), equalTo(resultado.getId()));

    assertThat(resultadoEncontrado.getGolesLocal(), equalTo(2));

    assertThat(resultadoEncontrado.getGolesVisitante(), equalTo(1));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaBuscarUnResultadoPorPartido() {
    // Given
    Resultado resultado = givenDadoQueTengoUnResultado(4, 2);
    repositorioResultado.guardar(resultado);

    Partido partido = givenTengoUnPartido();
    partido.setResultado(resultado);

    // When
    Resultado resultadoEncontrado = repositorioResultado.buscarPorPartido(partido);

    // Then
    assertThat(resultadoEncontrado.getId(), equalTo(resultado.getId()));

    assertThat(resultadoEncontrado.getGolesLocal(), equalTo(4));

    assertThat(resultadoEncontrado.getGolesVisitante(), equalTo(2));
  }

  private Resultado givenDadoQueTengoUnResultado(Integer golesLocal, Integer golesVisitante) {
    Resultado resultado = new Resultado();

    resultado.setGolesLocal(golesLocal);
    resultado.setGolesVisitante(golesVisitante);
    resultado.setFechaRegistro(LocalDateTime.now());

    repositorioResultado.guardar(resultado);

    return resultado;
  }

  private Partido givenTengoUnPartido() {
    Partido partido = new Partido();

    partido.setNivel("INTERMEDIO");
    partido.setEsPrivado(false);
    partido.setFecha(LocalDateTime.of(2026, 10, 20, 20, 0));
    partido.setCupoMaximo(10);

    repositorioPartido.guardar(partido);

    return partido;
  }
}
