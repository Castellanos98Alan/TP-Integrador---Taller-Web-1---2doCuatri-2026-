package com.tallerwebi.dominio;

import jakarta.persistence.*;

@Entity
public class Usuario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String email;
  private String password;
  private String rol;
  private Boolean activo = false;
  private String zona;
  private String biografia;
  private String nickname;
  private Integer dni;

  public Usuario() {}

  public Usuario(String email, Boolean activo) {
    this.email = email;
    this.activo = activo;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getRol() {
    return rol;
  }

  public void setRol(String rol) {
    this.rol = rol;
  }

  public Boolean getActivo() {
    return activo;
  }

  public void setActivo(Boolean activo) {
    this.activo = activo;
  }

  public void activar() {
    activo = true;
  }

  public String getZona() {
    return zona;
  }

  public void setZona(String zona) {
    this.zona = zona;
  }

  public String getBiografia() {
    return biografia;
  }

  public void setBiografia(String biografia) {
    this.biografia = biografia;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public Integer getDni() {
    return dni;
  }

  public void setDni(Integer dni) {
    this.dni = dni;
  }
}
