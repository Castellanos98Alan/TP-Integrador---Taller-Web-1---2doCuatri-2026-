package com.tallerwebi.dominio;

@SuppressWarnings("PMD.ImplicitFunctionalInterface") //sin esto no me levanta (preguntar al profe )
public interface RepositorioCancha {
  Cancha buscarPorNombre(String nombre);
}
