package com.ming.shopping.beauty.service.service;

import com.ming.shopping.beauty.service.entity.login.Login;
import com.ming.shopping.beauty.service.entity.support.ManageLevel;
import com.ming.shopping.beauty.service.repository.LoginRepository;
import me.jiangcai.lib.jdbc.ConnectionProvider;
import me.jiangcai.lib.jdbc.JdbcService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

/**
 * @author helloztt
 */
@Service
public class InitService {
    private static final Log log = LogFactory.getLog(InitService.class);
    public static final String cjMobile = "18606509616";

    @Autowired
    private JdbcService jdbcService;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private LoginRepository loginRepository;

    @PostConstruct
    @Transactional(rollbackFor = RuntimeException.class)
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public void init() throws IOException, SQLException {
        database();
        initSuperManage();
    }

    /**
     * 定义一些超级管理员
     */
    private void initSuperManage() {
        if (loginRepository.findByLoginName(cjMobile) == null) {
            Login login = new Login();
            login.setLoginName(cjMobile);
            login.setManageable(true);
            login.setGuidable(true);
            login.addLevel(ManageLevel.root);
            login.setCreateTime(LocalDateTime.now());
            loginRepository.save(login);
        }
    }

    private void database() throws SQLException {
        jdbcService.runJdbcWork(connection -> {
            String fileName;
            if (connection.profile().isMySQL()) {
                fileName = "mysql";
            } else if (connection.profile().isH2()) {
                fileName = "h2";
            } else {
                throw new IllegalStateException("not support for:" + connection.getConnection());
            }
            try {
                try (Statement statement = connection.getConnection().createStatement()) {
                    statement.executeUpdate("DROP TABLE IF EXISTS `CapitalFlow`");
                    statement.executeUpdate(StreamUtils.copyToString(new ClassPathResource(
                                    "/CapitalFlow." + fileName + ".sql").getInputStream()
                            , Charset.forName("UTF-8")));
                }
            } catch (IOException e) {
                throw new IllegalStateException("读取SQL失败", e);
            }
            //

        });
    }

    private void executeSQLCode(ConnectionProvider connection, String resourceName) throws SQLException {
        try {
            String code = StreamUtils.copyToString(applicationContext.getResource("classpath:/" + resourceName).getInputStream(), Charset.forName("UTF-8"));
            try (Statement statement = connection.getConnection().createStatement()) {
                statement.executeUpdate(code);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}