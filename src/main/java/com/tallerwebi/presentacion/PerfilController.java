package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Estadistica;
import com.tallerwebi.dominio.Jugador;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.servicio.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controlador para la vista.
 */
@Controller
public class PerfilController {

  private final ServicioLogin servicioLogin;
  private final ServicioJugador servicioJugador;
  private final ServicioRanking servicioRanking;
  private final ServicioEstadistica servicioEstadistica;
  private final ServicioSolicitudAmistad servicioSolicitudAmistad;

  private static final String USUARIO = "USUARIO";
  private static final String REDIRECT_LOGIN = "redirect:/login";
  private static final String FOTO = "foto";
  private static final int LONGITUD_CABECERA = 12;
  private static final String CARPETA_FOTOS = "src/main/webapp/uploads/perfiles";

  @Autowired
  public PerfilController(
    ServicioLogin servicioLogin,
    ServicioJugador servicioJugador,
    ServicioRanking servicioRanking,
    ServicioEstadistica servicioEstadistica,
    ServicioSolicitudAmistad servicioSolicitudAmistad
  ) {
    this.servicioLogin = servicioLogin;
    this.servicioJugador = servicioJugador;
    this.servicioRanking = servicioRanking;
    this.servicioEstadistica = servicioEstadistica;
    this.servicioSolicitudAmistad = servicioSolicitudAmistad;
  }

  @GetMapping("/perfil")
  public ModelAndView mostrarFormulario(HttpServletRequest request) {
    HttpSession session = request.getSession();
    Usuario usuario = (Usuario) session.getAttribute(USUARIO);

    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    Jugador jugador = servicioJugador.buscarPorUsuarioId(usuario.getId());

    Map<String, Object> model = new ModelMap();

    if (jugador != null) {
      Estadistica estadisticas = servicioEstadistica.obtenerEstadisticasGenerales(jugador);

      model = cargarDatosDelJugador(usuario, jugador, estadisticas);
    }

    model.put("email", usuario.getEmail());
    model.put("rol", usuario.getRol());
    model.put("activo", usuario.getActivo());

    model.put("esMiPerfil", true);
    model.put("idUsuarioPerfil", usuario.getId());

    return new ModelAndView("perfil", model);
  }

  @GetMapping("/editar-perfil")
  public ModelAndView editarPerfil(HttpServletRequest request) {
    HttpSession session = request.getSession();
    Usuario usuario = (Usuario) session.getAttribute(USUARIO);

    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    Jugador jugador = servicioJugador.buscarPorUsuarioId(usuario.getId());
    ModelAndView modelAndView = new ModelAndView("editar-perfil");

    modelAndView.addObject("nickname", usuario.getNickname());
    modelAndView.addObject("zona", usuario.getZona());
    modelAndView.addObject("biografia", usuario.getBiografia());
    modelAndView.addObject("posicion1", jugador != null ? jugador.getPosicion1() : null);
    modelAndView.addObject("posicion2", jugador != null ? jugador.getPosicion2() : null);
    modelAndView.addObject(FOTO, jugador != null ? jugador.getFoto() : null);
    modelAndView.addObject("fotoPosicionX", jugador != null ? jugador.getFotoPosicionX() : 50);
    modelAndView.addObject("fotoPosicionY", jugador != null ? jugador.getFotoPosicionY() : 50);

    return modelAndView;
  }

  @PostMapping("/guardar-foto")
  public ModelAndView guardarFoto(
    @RequestParam(value = "fotoBase64", required = false) String fotoBase64,
    @RequestParam(value = "fotoPosicionX", defaultValue = "50") Integer fotoPosicionX,
    @RequestParam(value = "fotoPosicionY", defaultValue = "50") Integer fotoPosicionY,
    HttpServletRequest request
  ) {
    Usuario usuario = (Usuario) request.getSession().getAttribute(USUARIO);

    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    Jugador jugador = servicioJugador.buscarPorUsuarioId(usuario.getId());

    if (jugador == null) {
      return new ModelAndView("redirect:/editar-perfil");
    }

    try {
      if (fotoBase64 != null && !fotoBase64.isBlank()) {
        String[] partes = fotoBase64.split(",", 2);
        byte[] bytes = Base64.getDecoder().decode(partes[partes.length - 1].trim());
        String extension = extensionDeImagen(bytes);

        if (extension == null || bytes.length > 2 * 1024 * 1024) {
          return new ModelAndView("redirect:/editar-perfil");
        }

        String nombreArchivo = UUID.randomUUID() + extension;
        Path carpeta = Path.of(CARPETA_FOTOS);

        Files.createDirectories(carpeta);
        Files.write(carpeta.resolve(nombreArchivo), bytes);

        jugador.setFoto("uploads/perfiles/" + nombreArchivo);
      }

      jugador.setFotoPosicionX(fotoPosicionX);
      jugador.setFotoPosicionY(fotoPosicionY);

      servicioJugador.modificar(jugador);
    } catch (Exception e) {
      return new ModelAndView("redirect:/editar-perfil");
    }

    return new ModelAndView("redirect:/perfil");
  }

  @GetMapping("/uploads/perfiles/{nombre:.+}")
  public ResponseEntity<byte[]> verFoto(@PathVariable("nombre") String nombre) throws IOException {
    Path carpeta = Path.of(CARPETA_FOTOS).toAbsolutePath().normalize();
    Path archivo = carpeta.resolve(nombre).normalize();

    if (!archivo.startsWith(carpeta) || !Files.isRegularFile(archivo)) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity
      .ok()
      .contentType(MediaTypeFactory.getMediaType(nombre).orElse(MediaType.APPLICATION_OCTET_STREAM))
      .body(Files.readAllBytes(archivo));
  }

  @PostMapping("/guardar-perfil")
  public ModelAndView guardarPerfil(
    @RequestParam("nickname") String nickname,
    @RequestParam(value = "posicion1", required = false) String posicion,
    @RequestParam(value = "posicion2", required = false) String posicion2,
    @RequestParam("zona") String zona,
    @RequestParam("biografia") String biografia,
    HttpServletRequest request
  ) {
    HttpSession session = request.getSession();
    Usuario usuario = (Usuario) session.getAttribute(USUARIO);

    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    usuario.setNickname(nickname);
    usuario.setBiografia(biografia);
    usuario.setZona(zona);
    servicioLogin.modificar(usuario);

    Jugador jugador = servicioJugador.buscarPorUsuarioId(usuario.getId());

    if (jugador == null) {
      jugador = new Jugador(nickname, posicion, posicion2, "D");
      jugador.setUsuario(usuario);
      servicioJugador.guardar(jugador);
    } else {
      jugador.setPosicion1(posicion != null && !posicion.isEmpty() ? posicion : null);
      jugador.setPosicion2(posicion2 != null && !posicion2.isEmpty() ? posicion2 : null);
      servicioJugador.modificar(jugador);
    }

    return new ModelAndView("redirect:/perfil");
  }

  private String extensionDeImagen(byte[] bytes) {
    if (bytes.length < LONGITUD_CABECERA) {
      return null;
    }

    String cabecera = new String(bytes, 0, LONGITUD_CABECERA, StandardCharsets.ISO_8859_1);

    if (cabecera.startsWith("\u00FF\u00D8\u00FF")) {
      return ".jpg";
    }
    if (cabecera.startsWith("\u0089PNG")) {
      return ".png";
    }
    if (cabecera.startsWith("RIFF") && cabecera.endsWith("WEBP")) {
      return ".webp";
    }
    return null;
  }

  @GetMapping("/perfil/{idUsuario}")
  public ModelAndView verPerfilJugador(
    @PathVariable("idUsuario") Long idUsuario,
    @RequestParam(value = "solicitudEnviada", required = false) Boolean solicitudEnviada,
    @RequestParam(value = "errorAmistad", required = false) Boolean errorAmistad,
    HttpServletRequest request
  ) {
    HttpSession session = request.getSession();
    Usuario usuarioLogueado = (Usuario) session.getAttribute(USUARIO);

    if (usuarioLogueado == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    Usuario usuarioPerfil = servicioLogin.buscarUsuarioPorId(idUsuario);

    if (usuarioPerfil == null) {
      return new ModelAndView("redirect:/ranking");
    }

    Jugador jugador = servicioJugador.buscarPorUsuarioId(idUsuario);

    if (jugador == null) {
      return new ModelAndView("redirect:/ranking");
    }

    Estadistica estadisticas = servicioEstadistica.obtenerEstadisticasGenerales(jugador);

    Map<String, Object> model = cargarDatosDelJugador(usuarioPerfil, jugador, estadisticas);

    model.put("esMiPerfil", usuarioLogueado.getId().equals(usuarioPerfil.getId()));

    model.put("idUsuarioPerfil", usuarioPerfil.getId());

    Boolean sonAmigos = servicioSolicitudAmistad.sonAmigos(usuarioLogueado, usuarioPerfil);

    model.put("sonAmigos", sonAmigos);

    if (Boolean.TRUE.equals(solicitudEnviada)) {
      model.put("mensajeAmistad", "Solicitud de amistad enviada correctamente.");
    }

    if (Boolean.TRUE.equals(errorAmistad)) {
      model.put("errorAmistad", "Ya le enviaste una solicitud a esta persona.");
    }

    return new ModelAndView("perfil", model);
  }

  private Map<String, Object> cargarDatosDelJugador(
    Usuario usuario,
    Jugador jugador,
    Estadistica estadisticas
  ) {
    Map<String, Object> model = new ModelMap();

    model.put("email", usuario.getEmail());
    model.put("rol", usuario.getRol());
    model.put("activo", usuario.getActivo());

    model.put("nickname", usuario.getNickname());
    model.put("biografia", usuario.getBiografia());
    model.put("zona", usuario.getZona());

    model.put("nombreJugador", jugador.getNombre());
    model.put("posicion1", jugador.getPosicion1());
    model.put("posicion2", jugador.getPosicion2());
    model.put("puntos", jugador.getPuntos());

    model.put("nivel", servicioRanking.calcularNivel(jugador.getPuntos()));

    model.put(FOTO, jugador.getFoto());
    model.put("fotoPosicionX", jugador.getFotoPosicionX());
    model.put("fotoPosicionY", jugador.getFotoPosicionY());

    model.put("partidosJugados", jugador.getPartidosJugados());
    model.put("victorias", jugador.getPartidosGanados());
    model.put("derrotas", jugador.getPartidosPerdidos());

    model.put("goles", estadisticas.getGoles());
    model.put("asistencias", estadisticas.getAsistencias());
    model.put("amarillas", estadisticas.getAmarillas());
    model.put("rojas", estadisticas.getRojas());
    model.put("puntajeTotal", estadisticas.getPuntaje());

    return model;
  }
}
