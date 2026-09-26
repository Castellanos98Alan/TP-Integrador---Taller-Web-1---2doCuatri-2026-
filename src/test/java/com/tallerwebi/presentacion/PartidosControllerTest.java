package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioPartido;
import com.tallerwebi.dominio.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model; // <-- Importante importar esto
import org.springframework.web.servlet.ModelAndView;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.mockito.Mockito.*;

public class PartidosControllerTest {

    private PartidosController partidosController;
    private ServicioPartido servicioPartidoMock;
    private HttpServletRequest requestMock;
    private HttpSession sessionMock;
    private Model modelMock; // <-- Declaramos el mock de Model

    @BeforeEach
    public void init() {
        servicioPartidoMock = mock(ServicioPartido.class);
        partidosController = new PartidosController(servicioPartidoMock);

        requestMock = mock(HttpServletRequest.class);
        sessionMock = mock(HttpSession.class);
        modelMock = mock(Model.class); // <-- Lo inicializamos
    }

    @Test
    public void queAlCancelarUnPartidoExitosamenteRedirijaALaVistaDePartidos() {
        Long idPartido = 5L;
        Usuario usuarioLogueado = new Usuario();
        usuarioLogueado.setId(1L);

        when(requestMock.getSession()).thenReturn(sessionMock);
        when(sessionMock.getAttribute("usuario")).thenReturn(usuarioLogueado);

        // Pasamos modelMock como segundo argumento
        ModelAndView modelAndView = partidosController.cancelarPartido(idPartido, modelMock, requestMock);

        verify(servicioPartidoMock, times(1)).cancelarPartido(idPartido, usuarioLogueado);
        assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/partidos"));
    }

    @Test
    public void queSiNoHayUsuarioLogueadoAlIntentarCancelarRedirijaAlLogin() {
        Long idPartido = 5L;

        when(requestMock.getSession()).thenReturn(sessionMock);
        when(sessionMock.getAttribute("usuario")).thenReturn(null); // Sin sesión activa

        // Pasamos modelMock como segundo argumento
        ModelAndView modelAndView = partidosController.cancelarPartido(idPartido, modelMock, requestMock);

        verify(servicioPartidoMock, never()).cancelarPartido(anyLong(), any());
        assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/login"));
    }
}