package com.tupperware.auth.repository.informix;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class ZonasResponsablesRepository {

	@PersistenceContext(unitName = "informixPU")
	private EntityManager em;
	
	public String obtenerNodoResponsable(String numeroDNI) {
		String query = "call concat_columna("
                + "'select n.nombre zona from responsable r, nodo_responsable nr, nodo n, consejera c, persona p, documentopersona dp "
                + "where r.idresponsable = nr.responsables_idresponsable and nr.nodo_idnodo = n.idnodo and c.persona_idpersona = r.persona_idpersona "
                + "and nvl(r.fechafin,today+1) > today and p.idpersona = c.persona_idpersona and dp.iddocumentopersona = p.docpersona_iddocumentopersona "
                + "and n.tiponodo_idtiponodo = 3 and dp.numero = "+numeroDNI +""
                + "union "
                + "select z.nombre zona from nodo z, (select n.idnodo from responsable r, nodo_responsable nr, nodo n, consejera c, persona p, documentopersona dp "
                + "where r.idresponsable = nr.responsables_idresponsable and nr.nodo_idnodo = n.idnodo and c.persona_idpersona = r.persona_idpersona "
                + "and nvl(r.fechafin,today+1) > today and p.idpersona = c.persona_idpersona and dp.iddocumentopersona = p.docpersona_iddocumentopersona "
                + "and n.tiponodo_idtiponodo = 2 and dp.numero = "+numeroDNI+") t where z.padre_idnodo = t.idnodo order by zona',',')";

   // Ejecutamos la consulta
   return (String) em.createNativeQuery(query)
           .getSingleResult();
	}
}
