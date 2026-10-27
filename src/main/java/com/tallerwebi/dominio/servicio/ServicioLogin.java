package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;

// MODIFICADO: se agrega el registro con los datos del jugador.
public interface ServicioLogin {
  Usuario consultarUsuario(String email, String password);

  void registrar(Usuario usuario) throws UsuarioExistente;

  void registrar(Usuario usuario, String nombreJugador, String posicion1, String posicion2)
    throws UsuarioExistente;

  void modificar(Usuario usuario);

  Usuario buscarUsuarioPorId(Long idUsuario);
}
