package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.Cancha;
import com.tallerwebi.dominio.repositorio.RepositorioCancha;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("servicioHome")
@Transactional
public class ServicioHomeImp implements ServicioHome {

  private RepositorioCancha repositorioCancha;

  @Autowired
  public ServicioHomeImp(RepositorioCancha repositorioCancha) {
    this.repositorioCancha = repositorioCancha;
  }

  @Override
  public List<Cancha> obtenerCanchasParaHome(Integer cantidad) {
    return repositorioCancha.buscarDestacadas(cantidad);
  }
}
