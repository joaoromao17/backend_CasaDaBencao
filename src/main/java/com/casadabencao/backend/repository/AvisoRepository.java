package com.casadabencao.backend.repository;

import com.casadabencao.backend.model.Aviso;
import com.casadabencao.backend.model.TipoAviso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AvisoRepository extends JpaRepository<Aviso, Long> {
    List<Aviso> findByTipo(TipoAviso tipo);
    List<Aviso> findByTipoAndMinisterioId(TipoAviso tipo, Long ministerioId);
    List<Aviso> findByDataExpiracaoIsNullOrDataExpiracaoAfter(LocalDate hoje);
}
