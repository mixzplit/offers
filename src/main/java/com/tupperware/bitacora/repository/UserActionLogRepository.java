package com.tupperware.bitacora.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tupperware.bitacora.entity.UserActionLog;

public interface UserActionLogRepository extends JpaRepository<UserActionLog, Long> {

}
