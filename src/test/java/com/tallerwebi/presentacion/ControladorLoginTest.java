package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import com.tallerwebi.dominio.servicio.ServicioLogin;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorLoginTest {

  private ControladorLogin controladorLogin;
  private Usuario usuarioMock;
  private DatosLogin datosLoginMock;
  private HttpServletRequest requestMock;
  private HttpSession sessionMock;
  private ServicioLogin servicioLoginMock;

  @BeforeEach
  public void init() {
    datosLoginMock = new DatosLogin("dami@unlam.com", "123");
    usuarioMock = mock(Usuario.class);
    when(usuarioMock.getEmail()).thenReturn("dami@unlam.com");
    requestMock = mock(HttpServletRequest.class);
    sessionMock = mock(HttpSession.class);
    servicioLoginMock = mock(ServicioLogin.class);
    controladorLogin = new ControladorLogin(servicioLoginMock);
  }

  @Test
  public void loginConUsuarioYPasswordIncorrectosDeberiaLlevarALoginNuevamente() {
    when(servicioLoginMock.consultarUsuario(anyString(), anyString())).thenReturn(null);

    ModelAndView modelAndView = controladorLogin.validarLogin(datosLoginMock, requestMock);

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("login"));
    assertThat(
      modelAndView.getModel().get("error").toString(),
      equalToIgnoringCase("Usuario o clave incorrecta")
    );
    verify(sessionMock, times(0)).setAttribute("ROL", "ADMIN");
  }

  @Test
  public void loginConUsuarioYPasswordCorrectosDeberiaLlevarAHome() {
    Usuario usuarioEncontradoMock = mock(Usuario.class);
    when(usuarioEncontradoMock.getRol()).thenReturn("ADMIN");

    when(requestMock.getSession()).thenReturn(sessionMock);
    when(servicioLoginMock.consultarUsuario(anyString(), anyString()))
      .thenReturn(usuarioEncontradoMock);

    ModelAndView modelAndView = controladorLogin.validarLogin(datosLoginMock, requestMock);

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/home"));
    verify(sessionMock, times(1)).setAttribute("ROL", usuarioEncontradoMock.getRol());
  }

  // MODIFICADO: el registro ahora recibe los datos del jugador.
  @Test
  public void registrarmeSiUsuarioNoExisteDeberiaCrearUsuarioYVolverAlLogin()
    throws UsuarioExistente {
    ModelAndView modelAndView = controladorLogin.registrarme(usuarioMock, "Dami", "Defensor", null);

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/login"));
    verify(servicioLoginMock, times(1)).registrar(usuarioMock, "Dami", "Defensor", null);
  }

  // MODIFICADO: se simula el usuario existente usando la nueva firma.
  @Test
  public void registrarmeSiUsuarioExisteDeberiaVolverAFormularioYMostrarError()
    throws UsuarioExistente {
    doThrow(UsuarioExistente.class)
      .when(servicioLoginMock)
      .registrar(usuarioMock, "Dami", "Defensor", null);

    ModelAndView modelAndView = controladorLogin.registrarme(usuarioMock, "Dami", "Defensor", null);

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("nuevo-usuario"));
    assertThat(
      modelAndView.getModel().get("error").toString(),
      equalToIgnoringCase("El usuario ya existe")
    );
  }

  // MODIFICADO: se simula un error con la nueva firma del registro.
  @Test
  public void errorEnRegistrarmeDeberiaVolverAFormularioYMostrarError() throws UsuarioExistente {
    doThrow(RuntimeException.class)
      .when(servicioLoginMock)
      .registrar(usuarioMock, "Dami", "Defensor", null);

    ModelAndView modelAndView = controladorLogin.registrarme(usuarioMock, "Dami", "Defensor", null);

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("nuevo-usuario"));
    assertThat(
      modelAndView.getModel().get("error").toString(),
      equalToIgnoringCase("Error al registrar el nuevo usuario")
    );
  }

  @Test
  public void irALoginDeberiaRetornarVistaLoginConDatosLogin() {
    ModelAndView modelAndView = controladorLogin.irALogin();

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("login"));
    assertThat(modelAndView.getModel().get("datosLogin"), instanceOf(DatosLogin.class));
  }

  @Test
  public void nuevoUsuarioDeberiaRetornarVistaNuevoUsuarioConUsuarioVacio() {
    ModelAndView modelAndView = controladorLogin.nuevoUsuario();

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("nuevo-usuario"));
    assertThat(modelAndView.getModel().get("usuario"), instanceOf(Usuario.class));
  }

  @Test
  public void irAHomeDeberiaRetornarVistaHome() {
    ModelAndView modelAndView = controladorLogin.irAHome();

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("home"));
  }

  @Test
  public void inicioDeberiaRedirigirALogin() {
    ModelAndView modelAndView = controladorLogin.inicio();

    assertThat(modelAndView.getViewName(), equalToIgnoringCase("redirect:/login"));
  }
}
