package com.tupperware.wao.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tupperware.wao.dto.OfertaUsuarioDTO;
import com.tupperware.wao.entity.RegistroOfertaWao;

public interface RegistroOfertaWaoRepository extends JpaRepository<RegistroOfertaWao, Integer> {
	Optional<RegistroOfertaWao> findByIdOfertaAndContrato(Integer idOfertas, Integer contrato);
	
	//Buscar ofertas registradas por usuario (contrato)
	@Query("select new com.tupperware.wao.dto.OfertaUsuarioDTO(o.id, o.descripcionArticulo, "
			+ " o.codigoArticulo, o.codigoAuxiliar, r.cantidadSolicitada, r.fechaRegistro, "
			+ " r.estado, o.anio, o.campania) "
			+ "from RegistroOfertaWao r "
			+ "inner join OfertaWao o on r.idOferta = o.id "
			+ "inner join User u on r.contrato = u.contrato "
			+ "where r.contrato = :contrato")
	List<OfertaUsuarioDTO> findOfertasByContrato(@Param("contrato") Integer contrato);
	
	@Query("select new com.tupperware.wao.dto.OfertaUsuarioDTO(o.id, o.descripcionArticulo, "
			+ " o.codigoArticulo, o.codigoAuxiliar, r.cantidadSolicitada, r.fechaRegistro, "
			+ " r.estado, o.anio, o.campania) "
			+ "from RegistroOfertaWao r "
			+ "inner join OfertaWao o on r.idOferta = o.id "
			+ "inner join User u on r.contrato = u.contrato "
			+ "where r.contrato = :contrato and o.anio = :anio and o.campania = :campania")
	List<OfertaUsuarioDTO> findOfertasByContratoAndAnioAndCampania(@Param("contrato") Integer contrato,
										@Param("anio") Short anio, @Param("campania") Short campania);
}
