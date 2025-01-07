package com.tupperware.bitacora.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tupperware.bitacora.entity.UserActionLog;
import com.tupperware.bitacora.repository.UserActionLogRepository;

@Service
public class UserActionLogService {

	@Autowired
	private UserActionLogRepository userAction;
	
	public void logAction(Integer contrato, String action, String details) {
		UserActionLog log = new UserActionLog();
		log.setContrato(contrato);
		log.setAction(action);
		log.setDetails(details);
		log.setCreatedAt(LocalDateTime.now());
		
		userAction.save(log);
	}
	
	public void logAction(Integer contratoLogeado, Integer contrato, String action, String details) {
		UserActionLog log = new UserActionLog();
		log.setContratoLogeado(contratoLogeado);
		log.setContrato(contrato);
		log.setAction(action);
		log.setDetails(details);
		log.setCreatedAt(LocalDateTime.now());
		
		userAction.save(log);
	}
}
