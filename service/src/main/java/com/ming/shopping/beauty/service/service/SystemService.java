package com.ming.shopping.beauty.service.service;

/**
 * 系统服务；它不依赖任何玩意儿
 *
 * @author helloztt
 */
public interface SystemService {
    /**
     * 一些请求地址
     */
    String LOGIN = "/auth";
    String LOGINOUT = "/logout";
    String TO_LOGIN = "/toLogin";
    String AUTH = "/auth";

    /**
     * @param uri 传入uri通常/开头
     * @return 完整路径
     * @deprecated 应该准确地说明用户场景，比如桌面版或者移动版
     */
    String toUrl(String uri);

    String toMobileUrl(String uri);

    String toDesktopUrl(String uri);

    /**
     * @return 前端首页
     */
    default String toMobileHomeUrl() {
        return toMobileUrl("/#/personal");
    }

    /**
     *
     * @return 前端注册页面
     */
    default String toMobileJoinUrl() {
        return toMobileUrl("/#/join");
    }
}
