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
	
	public void logAction(Long userId, String action, String details) {
		UserActionLog log = new UserActionLog();
		log.setUserId(userId);
		log.setAction(action);
		log.setDetails(details);
		log.setCreatedAt(LocalDateTime.now());
		
		userAction.save(log);
	}
}
