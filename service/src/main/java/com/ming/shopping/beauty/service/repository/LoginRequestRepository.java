package com.ming.shopping.beauty.service.repository;

import com.ming.shopping.beauty.service.entity.login.LoginRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author helloztt
 */
public interface LoginRequestRepository extends JpaRepository<LoginRequest, Long>, JpaSpecificationExecutor<LoginRequest> {
    /**
     * 根据 sessionId 查找登录请求
     *
     * @param sessionId
     * @return
     */
    LoginRequest findBySessionId(String sessionId);
}
