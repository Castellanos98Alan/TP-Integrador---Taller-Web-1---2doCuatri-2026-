package com.tallerwebi.dominio;
import com.tallerwebi.dominio.RepositorioPartido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServicioPartidoTest {

    private ServicioPartido servicioPartido;
    private RepositorioPartido repositorioPartidoMock;

    @BeforeEach
    public void init() {
        repositorioPartidoMock = mock(RepositorioPartido.class);
        servicioPartido = new ServicioPartidoImpl(repositorioPartidoMock);
    }

    @Test
    public void queUnOrganizadorPuedaCancelarSuPartidoActivoExitosamente() {
        Usuario organizador = new Usuario();
        organizador.setId(1L);

        Partido partido = new Partido();
        partido.setId(10L);
        partido.setOrganizador(organizador);
        partido.setEstado(Partido.EstadoPartido.ACTIVO);

        when(repositorioPartidoMock.buscarPorId(10L)).thenReturn(partido);

        servicioPartido.cancelarPartido(10L, organizador);

        assertEquals(Partido.EstadoPartido.CANCELADO, partido.getEstado());
        verify(repositorioPartidoMock, times(1)).modificar(partido);
    }

    @Test
    public void queUnUsuarioQueNoEsElOrganizadorNoPuedaCancelarElPartido() {
        Usuario organizador = new Usuario();
        organizador.setId(1L);

        Usuario intruso = new Usuario();
        intruso.setId(2L);

        Partido partido = new Partido();
        partido.setId(10L);
        partido.setOrganizador(organizador);
        partido.setEstado(Partido.EstadoPartido.ACTIVO);

        when(repositorioPartidoMock.buscarPorId(10L)).thenReturn(partido);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            servicioPartido.cancelarPartido(10L, intruso);
        });

        assertEquals("No estás autorizado para cancelar este partido.", exception.getMessage());
        verify(repositorioPartidoMock, never()).modificar(any());
    }

    @Test
    public void queNoSePuedaCancelarUnPartidoQueYaFinalizo() {
        Usuario organizador = new Usuario();
        organizador.setId(1L);

        Partido partido = new Partido();
        partido.setId(10L);
        partido.setOrganizador(organizador);
        partido.setEstado(Partido.EstadoPartido.FINALIZADO);

        when(repositorioPartidoMock.buscarPorId(10L)).thenReturn(partido);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            servicioPartido.cancelarPartido(10L, organizador);
        });

        assertEquals("No se puede cancelar un partido que ya finalizó.", exception.getMessage());
        verify(repositorioPartidoMock, never()).modificar(any());
    }
}
