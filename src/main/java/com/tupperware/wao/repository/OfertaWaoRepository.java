package com.tupperware.wao.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tupperware.wao.entity.OfertaWao;

import jakarta.persistence.LockModeType;

public interface OfertaWaoRepository extends JpaRepository<OfertaWao, Integer> {
	
	List<OfertaWao> findAll();

	Optional<OfertaWao> findById(Integer id);
	// Validamos la oferta activa por id y fecha registro
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT o FROM OfertaWao o WHERE :fechaActual BETWEEN o.fechaInicio AND o.fechaFin AND o.id = :id")
	Optional<OfertaWao> findValidarOfertaActiva(@Param("fechaActual") LocalDateTime fechaActual, @Param("id") Integer id);
	
	//Ofertas Activas (se pasa la misma fecha en ambos parametros
	List<OfertaWao> findByFechaInicioBeforeAndFechaFinAfter(LocalDateTime fechaActual1, LocalDateTime fechaActual2);
	//Ofertas Activas por zona usuario (se pasa la misma fecha en ambos parametros)
	List<OfertaWao> findByFechaInicioBeforeAndFechaFinAfterAndZonasAsignadasContaining(LocalDateTime fechaActual1, LocalDateTime fechaActual2, String zona);
	
	//Obtengo las ofertas activas filtradas por usuario
	//y las dirigidas a todas las zonas
	@Query("   SELECT o FROM OfertaWao o "
			+ "WHERE :fechaActual BETWEEN o.fechaInicio AND o.fechaFin "
			+ "AND (o.zonasAsignadas like :zonaUsuario OR o.zonasAsignadas = '')")
	List<OfertaWao> findOfertasActivasPorZonaAndGlobal(
			@Param("fechaActual") LocalDateTime fechaActual,
			@Param("zonaUsuario") String zonaUsuario);
	
	@Query("SELECT o FROM OfertaWao o " +
		       "WHERE :fechaActual BETWEEN o.fechaInicio AND o.fechaFin " +
		       "AND (o.zonasAsignadas IN :zonasUsuarios OR o.zonasAsignadas = '')")
		List<OfertaWao> findOfertasGzGd(
		        @Param("fechaActual") LocalDateTime fechaActual,
		        @Param("zonasUsuarios") List<String> zonasUsuarios);

	
	/**
	 * Cantidad de solicitudes segun perfil,
	 * usuario logeado e id de la oferta
	 * @param idPerfil
	 * @param contratoUsuario
	 * @param idOferta
	 * @return
	 */
	@Query("SELECT COUNT(r.id) " +
		       "FROM RegistroOfertaWao r " +
		       "JOIN Revendedora rev ON r.contrato = rev.contrato " +
		       "WHERE r.idOferta = :idOferta " +
		       "AND ((:idPerfil = 1 AND rev.contrato = :contratoUsuario) " +
		       "OR (:idPerfil = 4 AND (rev.contrato = :contratoUsuario OR rev.patrocinante = :contratoUsuario)) "+
		       "OR (:idPerfil = 2 AND rev.zona IN (:zonasResponsables)) "+ 
		       "OR (:idPerfil = 3 AND rev.zona IN (:zonasResponsables)))")
	Long countSolicitudesPorPerfilYOferta(
		        @Param("idPerfil") Integer idPerfil,
		        @Param("contratoUsuario") Integer contratoUsuario,
		        @Param("idOferta") Integer idOferta,
		        @Param("zonasResponsables") List<String> zonasResponsables);


	/**
	 * detalle de las ofertas registradas
	 * segun perfil, usuario logeado y id
	 * de la oferta
	 * @param idPerfil
	 * @param contratoUsuario
	 * @param idOferta
	 * @return
	 */
	@Query("SELECT rev.contrato, rev.grupo, rev.nombres, r.cantidadSolicitada " +
		       "FROM RegistroOfertaWao r " +
		       "JOIN Revendedora rev ON r.contrato = rev.contrato " +
		       "WHERE r.idOferta = :idOferta " +
		       "AND ((:idPerfil = 1 AND rev.contrato = :contratoUsuario) " +
		       "OR (:idPerfil = 4 AND (rev.contrato = :contratoUsuario OR rev.patrocinante = :contratoUsuario)) "+
		       "OR (:idPerfil = 2 AND rev.zona IN (:zonasResponsables)) "+
		       "OR (:idPerfil = 3 AND rev.zona IN (:zonasResponsables)))")
	List<Object[]> detalleSolicitudesPorPerfilYOferta(
		        @Param("idPerfil") Integer idPerfil,
		        @Param("contratoUsuario") Integer contratoUsuario,
		        @Param("idOferta") Integer idOferta,
		        @Param("zonasResponsables") List<String> zonasResponsables);
	
}
