package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

import com.tallerwebi.dominio.servicio.ServicioBalanceoEquipo;
import com.tallerwebi.dominio.servicio.ServicioBalanceoEquipoImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioBalanceoEquipoTest {

    private ServicioBalanceoEquipo servicioBalanceoEquipo;

    @BeforeEach
    public void init() {
        servicioBalanceoEquipo = new ServicioBalanceoEquipoImpl();
    }

    @Test
    public void deberiaFormarDosEquiposDeCincoConDiezJugadores() {

        Partido partido = new Partido();
        partido.setId(1L);

        Equipo local = new Equipo();
        local.setId(1L);
        local.setNombre("Equipo Local");

        Equipo visitante = new Equipo();
        visitante.setId(2L);
        visitante.setNombre("Equipo Visitante");

        partido.setEquipo1(local);
        partido.setEquipo2(visitante);

        List<InscripcionPartido> inscripciones = new ArrayList<>();

        for (int i = 0; i < 10; i++) {

            Jugador jugador = new Jugador();
            jugador.setId((long) i);
            jugador.setPuntos((10 - i) * 10);

            InscripcionPartido inscripcion = new InscripcionPartido();
            inscripcion.setPartido(partido);
            inscripcion.setJugador(jugador);

            inscripciones.add(inscripcion);
        }

        Collections.shuffle(inscripciones);

        partido.setInscripciones(inscripciones);

        servicioBalanceoEquipo.balancearEquipos(partido);

        List<InscripcionPartido> resultado = partido.getInscripciones();

        int cantLocal = 0;
        int cantVisitante = 0;

        for (InscripcionPartido inscripcion : resultado) {

            if (local.equals(inscripcion.getEquipo())) {
                cantLocal++;
            }

            if (visitante.equals(inscripcion.getEquipo())) {
                cantVisitante++;
            }
        }

        assertThat(cantLocal, is(5));
        assertThat(cantVisitante, is(5));

        // Snake Draft:
        // Local = 0, 3, 4, 7, 8

        assertThat(resultado.get(0).getEquipo(), is(local));
        assertThat(resultado.get(3).getEquipo(), is(local));
        assertThat(resultado.get(4).getEquipo(), is(local));
        assertThat(resultado.get(7).getEquipo(), is(local));
        assertThat(resultado.get(8).getEquipo(), is(local));

        // Visitante = 1, 2, 5, 6, 9

        assertThat(resultado.get(1).getEquipo(), is(visitante));
        assertThat(resultado.get(2).getEquipo(), is(visitante));
        assertThat(resultado.get(5).getEquipo(), is(visitante));
        assertThat(resultado.get(6).getEquipo(), is(visitante));
        assertThat(resultado.get(9).getEquipo(), is(visitante));
    }

    @Test
    public void deberiaFormarDosEquiposDeSieteConCatorceJugadores() {

        Partido partido = new Partido();
        partido.setId(1L);

        Equipo local = new Equipo();
        local.setId(1L);
        local.setNombre("Equipo Local");

        Equipo visitante = new Equipo();
        visitante.setId(2L);
        visitante.setNombre("Equipo Visitante");

        partido.setEquipo1(local);
        partido.setEquipo2(visitante);

        List<InscripcionPartido> inscripciones = new ArrayList<>();

        for (int i = 0; i < 14; i++) {

            Jugador jugador = new Jugador();
            jugador.setId((long) i);
            jugador.setPuntos((14 - i) * 10);

            InscripcionPartido inscripcion = new InscripcionPartido();
            inscripcion.setPartido(partido);
            inscripcion.setJugador(jugador);

            inscripciones.add(inscripcion);
        }

        Collections.shuffle(inscripciones);

        partido.setInscripciones(inscripciones);

        servicioBalanceoEquipo.balancearEquipos(partido);

        List<InscripcionPartido> resultado = partido.getInscripciones();

        int cantLocal = 0;
        int cantVisitante = 0;

        for (InscripcionPartido inscripcion : resultado) {

            if (local.equals(inscripcion.getEquipo())) {
                cantLocal++;
            }

            if (visitante.equals(inscripcion.getEquipo())) {
                cantVisitante++;
            }
        }

        assertThat(cantLocal, is(7));
        assertThat(cantVisitante, is(7));

        // Local

        assertThat(resultado.get(0).getEquipo(), is(local));
        assertThat(resultado.get(3).getEquipo(), is(local));
        assertThat(resultado.get(4).getEquipo(), is(local));
        assertThat(resultado.get(7).getEquipo(), is(local));
        assertThat(resultado.get(8).getEquipo(), is(local));
        assertThat(resultado.get(11).getEquipo(), is(local));
        assertThat(resultado.get(12).getEquipo(), is(local));

        // Visitante

        assertThat(resultado.get(1).getEquipo(), is(visitante));
        assertThat(resultado.get(2).getEquipo(), is(visitante));
        assertThat(resultado.get(5).getEquipo(), is(visitante));
        assertThat(resultado.get(6).getEquipo(), is(visitante));
        assertThat(resultado.get(9).getEquipo(), is(visitante));
        assertThat(resultado.get(10).getEquipo(), is(visitante));
        assertThat(resultado.get(13).getEquipo(), is(visitante));
    }

    @Test
    public void deberiaFormarDosEquiposDeOnceConVeintidosJugadores() {

        Partido partido = new Partido();
        partido.setId(1L);

        Equipo local = new Equipo();
        local.setId(1L);
        local.setNombre("Equipo Local");

        Equipo visitante = new Equipo();
        visitante.setId(2L);
        visitante.setNombre("Equipo Visitante");

        partido.setEquipo1(local);
        partido.setEquipo2(visitante);

        List<InscripcionPartido> inscripciones = new ArrayList<>();

        for (int i = 0; i < 22; i++) {

            Jugador jugador = new Jugador();
            jugador.setId((long) i);
            jugador.setPuntos((22 - i) * 10);

            InscripcionPartido inscripcion = new InscripcionPartido();
            inscripcion.setPartido(partido);
            inscripcion.setJugador(jugador);

            inscripciones.add(inscripcion);
        }

        Collections.shuffle(inscripciones);

        partido.setInscripciones(inscripciones);

        servicioBalanceoEquipo.balancearEquipos(partido);

        List<InscripcionPartido> resultado = partido.getInscripciones();

        int cantLocal = 0;
        int cantVisitante = 0;

        for (InscripcionPartido inscripcion : resultado) {

            if (local.equals(inscripcion.getEquipo())) {
                cantLocal++;
            }

            if (visitante.equals(inscripcion.getEquipo())) {
                cantVisitante++;
            }
        }

        assertThat(cantLocal, is(11));
        assertThat(cantVisitante, is(11));

        // Local

        assertThat(resultado.get(0).getEquipo(), is(local));
        assertThat(resultado.get(3).getEquipo(), is(local));
        assertThat(resultado.get(4).getEquipo(), is(local));
        assertThat(resultado.get(7).getEquipo(), is(local));
        assertThat(resultado.get(8).getEquipo(), is(local));
        assertThat(resultado.get(11).getEquipo(), is(local));
        assertThat(resultado.get(12).getEquipo(), is(local));
        assertThat(resultado.get(15).getEquipo(), is(local));
        assertThat(resultado.get(16).getEquipo(), is(local));
        assertThat(resultado.get(19).getEquipo(), is(local));
        assertThat(resultado.get(20).getEquipo(), is(local));

        // Visitante

        assertThat(resultado.get(1).getEquipo(), is(visitante));
        assertThat(resultado.get(2).getEquipo(), is(visitante));
        assertThat(resultado.get(5).getEquipo(), is(visitante));
        assertThat(resultado.get(6).getEquipo(), is(visitante));
        assertThat(resultado.get(9).getEquipo(), is(visitante));
        assertThat(resultado.get(10).getEquipo(), is(visitante));
        assertThat(resultado.get(13).getEquipo(), is(visitante));
        assertThat(resultado.get(14).getEquipo(), is(visitante));
        assertThat(resultado.get(17).getEquipo(), is(visitante));
        assertThat(resultado.get(18).getEquipo(), is(visitante));
        assertThat(resultado.get(21).getEquipo(), is(visitante));
    }

    @Test
    public void deberiaAsignarUnCapitanPorEquipo() {

        Partido partido = new Partido();

        Equipo local = new Equipo();
        local.setId(1L);

        Equipo visitante = new Equipo();
        visitante.setId(2L);

        partido.setEquipo1(local);
        partido.setEquipo2(visitante);

        Jugador jugador1 = new Jugador();
        jugador1.setPuntos(80);

        Jugador jugador2 = new Jugador();
        jugador2.setPuntos(60);

        Jugador jugador3 = new Jugador();
        jugador3.setPuntos(90);

        Jugador jugador4 = new Jugador();
        jugador4.setPuntos(70);

        InscripcionPartido ins1 = new InscripcionPartido(partido, jugador1);

        ins1.setEquipo(local);

        InscripcionPartido ins2 = new InscripcionPartido(partido, jugador2);

        ins2.setEquipo(local);

        InscripcionPartido ins3 = new InscripcionPartido(partido, jugador3);

        ins3.setEquipo(visitante);

        InscripcionPartido ins4 = new InscripcionPartido(partido, jugador4);

        ins4.setEquipo(visitante);

        partido.setInscripciones(new ArrayList<>(List.of(ins1, ins2, ins3, ins4)));

        servicioBalanceoEquipo.asignarCapitanes(partido);

        assertTrue(ins1.isEsCapitan());
        assertFalse(ins2.isEsCapitan());

        assertTrue(ins3.isEsCapitan());
        assertFalse(ins4.isEsCapitan());
    }

    @Test
    public void deberiaElegirComoCapitanAlJugadorConMasPuntos() {

        Partido partido = new Partido();

        Equipo local = new Equipo();
        local.setId(1L);

        Equipo visitante = new Equipo();
        visitante.setId(2L);

        partido.setEquipo1(local);
        partido.setEquipo2(visitante);

        Jugador jugador1 = new Jugador();
        jugador1.setPuntos(40);

        Jugador jugador2 = new Jugador();
        jugador2.setPuntos(80);

        Jugador jugador3 = new Jugador();
        jugador3.setPuntos(60);

        Jugador jugador4 = new Jugador();
        jugador4.setPuntos(90);

        InscripcionPartido ins1 = new InscripcionPartido(partido, jugador1);

        ins1.setEquipo(local);

        InscripcionPartido ins2 = new InscripcionPartido(partido, jugador2);

        ins2.setEquipo(local);

        InscripcionPartido ins3 = new InscripcionPartido(partido, jugador3);

        ins3.setEquipo(visitante);

        InscripcionPartido ins4 = new InscripcionPartido(partido, jugador4);

        ins4.setEquipo(visitante);

        partido.setInscripciones(new ArrayList<>(List.of(ins1, ins2, ins3, ins4)));

        servicioBalanceoEquipo.asignarCapitanes(partido);

        assertFalse(ins1.isEsCapitan());
        assertTrue(ins2.isEsCapitan());

        assertFalse(ins3.isEsCapitan());
        assertTrue(ins4.isEsCapitan());
    }

    @Test
    public void deberiaElegirComoCapitanAlQueSeInscribioPrimeroSiEmpatanEnPuntos() {

        Partido partido = new Partido();

        Equipo local = new Equipo();
        local.setId(1L);

        partido.setEquipo1(local);

        Jugador jugador1 = new Jugador();
        jugador1.setPuntos(80);

        Jugador jugador2 = new Jugador();
        jugador2.setPuntos(80);

        InscripcionPartido ins1 = new InscripcionPartido(partido, jugador1);

        ins1.setEquipo(local);

        ins1.setFechaInscripcion(LocalDateTime.of(2026, 10, 20, 10, 0));

        InscripcionPartido ins2 = new InscripcionPartido(partido, jugador2);

        ins2.setEquipo(local);

        ins2.setFechaInscripcion(LocalDateTime.of(2026, 10, 20, 9, 0));

        partido.setInscripciones(new ArrayList<>(List.of(ins1, ins2)));

        servicioBalanceoEquipo.asignarCapitanes(partido);

        assertFalse(ins1.isEsCapitan());
        assertTrue(ins2.isEsCapitan());
    }

    @Test
    public void deberiaOrdenarLosJugadoresDeMayorAMenorPuntaje() {

        Partido partido = new Partido();

        Equipo local = new Equipo();
        Equipo visitante = new Equipo();

        partido.setEquipo1(local);
        partido.setEquipo2(visitante);

        Jugador jugador1 = new Jugador();
        jugador1.setPuntos(20);

        Jugador jugador2 = new Jugador();
        jugador2.setPuntos(100);

        Jugador jugador3 = new Jugador();
        jugador3.setPuntos(50);

        Jugador jugador4 = new Jugador();
        jugador4.setPuntos(80);

        InscripcionPartido ins1 = new InscripcionPartido(partido, jugador1);

        InscripcionPartido ins2 = new InscripcionPartido(partido, jugador2);

        InscripcionPartido ins3 = new InscripcionPartido(partido, jugador3);

        InscripcionPartido ins4 = new InscripcionPartido(partido, jugador4);

        partido.setInscripciones(new ArrayList<>(List.of(ins1, ins2, ins3, ins4)));

        servicioBalanceoEquipo.balancearEquipos(partido);

        List<InscripcionPartido> resultado = partido.getInscripciones();

        assertEquals(100, resultado.get(0).getJugador().getPuntos());

        assertEquals(80, resultado.get(1).getJugador().getPuntos());

        assertEquals(50, resultado.get(2).getJugador().getPuntos());

        assertEquals(20, resultado.get(3).getJugador().getPuntos());
    }
}