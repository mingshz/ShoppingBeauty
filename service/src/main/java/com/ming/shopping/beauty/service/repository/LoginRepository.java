package com.ming.shopping.beauty.service.repository;

import com.ming.shopping.beauty.service.entity.login.Login;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author helloztt
 */
public interface LoginRepository extends JpaRepository<Login, Long>, JpaSpecificationExecutor<Login> {

    /**
     * 根据登录名查找用户
     *
     * @param loginName 手机号
     * @return
     */
    Login findByLoginName(String loginName);

    /**
     * 更新角色可用状态
     *
     * @param id
     * @param enabled
     * @return
     */
    @Transactional(rollbackFor = RuntimeException.class)
    @Modifying(clearAutomatically = true)
    @Query("update Login set enabled = ?2 where id = ?1")
    int updateLoginEnabled(long id, boolean enabled);

    /**
     * 更新角色管理员状态
     *
     * @param id
     * @param manageAble
     * @return
     */
    @Transactional(rollbackFor = RuntimeException.class)
    @Modifying(clearAutomatically = true)
    @Query("update Login set manageable = ?2 where id = ?1")
    int updateLoginManageAble(long id, boolean manageAble);
}