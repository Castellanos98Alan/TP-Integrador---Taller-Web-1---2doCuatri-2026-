package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.Jugador;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import com.tallerwebi.dominio.repositorio.RepositorioJugador;
import com.tallerwebi.dominio.repositorio.RepositorioUsuario;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("servicioLogin")
@Transactional
public class ServicioLoginImpl implements ServicioLogin {

  private RepositorioUsuario repositorioUsuario;
  private RepositorioJugador repositorioJugador;

  @Autowired
  public ServicioLoginImpl(
    RepositorioUsuario repositorioUsuario,
    RepositorioJugador repositorioJugador
  ) {
    this.repositorioUsuario = repositorioUsuario;
    this.repositorioJugador = repositorioJugador;
  }

  public ServicioLoginImpl(RepositorioUsuario repositorioUsuario) {
    this.repositorioUsuario = repositorioUsuario;
  }

  @Override
  public Usuario consultarUsuario(String email, String password) {
    return repositorioUsuario.buscarUsuario(email, password);
  }

  @Override
  public void registrar(Usuario usuario) throws UsuarioExistente {
    validarUsuarioNuevo(usuario);
    repositorioUsuario.guardar(usuario);
  }

  @Override
  public void registrar(Usuario usuario, String nombreJugador, String posicion1, String posicion2)
    throws UsuarioExistente {
    validarUsuarioNuevo(usuario);
    repositorioUsuario.guardar(usuario);

    Jugador jugador = new Jugador(nombreJugador, posicion1, posicion2, "D");

    jugador.setUsuario(usuario);
    repositorioJugador.guardar(jugador);
  }

  private void validarUsuarioNuevo(Usuario usuario) throws UsuarioExistente {
    Usuario usuarioEncontrado = repositorioUsuario.buscarUsuario(
      usuario.getEmail(),
      usuario.getPassword()
    );

    if (usuarioEncontrado != null) {
      throw new UsuarioExistente();
    }

    Usuario usuarioConDni = repositorioUsuario.buscarPorDni(usuario.getDni());

    if (usuarioConDni != null) {
      throw new UsuarioExistente();
    }
  }

  @Override
  public void modificar(Usuario usuario) {
    repositorioUsuario.modificar(usuario);
  }

  @Override
  public Usuario buscarUsuarioPorId(Long idUsuario) {
    return repositorioUsuario.buscarPorId(idUsuario);
  }
}
