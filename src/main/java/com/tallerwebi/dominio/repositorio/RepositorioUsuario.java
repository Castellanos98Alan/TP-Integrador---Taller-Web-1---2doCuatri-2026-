package com.tallerwebi.dominio.repositorio;

import com.tallerwebi.dominio.Usuario;

public interface RepositorioUsuario {
  Usuario buscarUsuario(String email, String password);
  void guardar(Usuario usuario);
  Usuario buscar(String email);
  void modificar(Usuario usuario);
  Usuario buscarPorDni(Integer dni);
  Usuario buscarPorId(Long id);
}
