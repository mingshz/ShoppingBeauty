package com.ming.shopping.beauty.manage.controller;

import com.ming.shopping.beauty.service.entity.login.Login;
import com.ming.shopping.beauty.service.entity.login.User;
import com.ming.shopping.beauty.service.exception.ApiResultException;
import com.ming.shopping.beauty.service.model.ApiResult;
import com.ming.shopping.beauty.service.model.ResultCodeEnum;
import com.ming.shopping.beauty.service.service.CapitalService;
import com.ming.shopping.beauty.service.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.text.MessageFormat;

/**
 * @author lxf
 */
@Controller
@PreAuthorize("hasAnyRole('ROOT')")
public class ManageMoneyController {

    @Autowired
    private CapitalService capitalService;
    @Autowired
    private LoginService loginService;

    /**
     * 管理员手动给某个账户充值
     *
     * @param login  充值操作的管理元
     * @param mobile 充值的用户电话号
     * @param amount 充值金额
     */
    @PostMapping("/manage/manualRecharge")
    @ResponseBody
    @Transactional
    public ApiResult manualRecharge(@AuthenticationPrincipal Login login, String mobile, BigDecimal amount) {
        //TODO 手动充值是否有最小充值限制?
        if (amount.compareTo(BigDecimal.ZERO) != 1) {
            throw new ApiResultException(ApiResult.withCodeAndMessage(ResultCodeEnum.RECHARGE_MONEY_NOT_ENOUGH.getCode(),
                    MessageFormat.format(ResultCodeEnum.RECHARGE_MONEY_NOT_ENOUGH.getMessage(), amount), null));
        }
        if (StringUtils.isEmpty(mobile)) {
            throw new ApiResultException(ApiResult.withCodeAndMessage(ResultCodeEnum.CARD_FAILURE.getCode(),
                    MessageFormat.format(ResultCodeEnum.REQUEST_DATA_ERROR.getMessage(), mobile), null));
        }

        Login one = loginService.findOne(mobile);
        User user = login.getUser();
        try {
            capitalService.manualRecharge(login, user, amount);
        } catch (Exception e) {
            throw new ApiResultException(ApiResult.withCodeAndMessage(ResultCodeEnum.CARD_FAILURE.getCode(),
                    ResultCodeEnum.CARD_FAILURE.getMessage(), null));
        }
        return ApiResult.withOk("充值金额:" + amount + ",手机号码:" + mobile + ",充值成功");
    }

    /**
     * 扣款/提现操作
     * @param login  操作员
     * @param mobile 提现/扣款用户
     * @param amount 提现/扣款金额
     * @return
     */
    @PostMapping("/manage/deduction")
    @Transactional
    @ResponseBody
    public ApiResult deduction(@AuthenticationPrincipal Login login, String mobile, BigDecimal amount) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) != 1) {
            throw new ApiResultException(ApiResult.withCodeAndMessage(ResultCodeEnum.REQUEST_DATA_ERROR.getCode(),
                    MessageFormat.format(ResultCodeEnum.REQUEST_DATA_ERROR.getMessage(), amount), null));
        }
        if (StringUtils.isEmpty(mobile)) {
            throw new ApiResultException(ApiResult.withCodeAndMessage(ResultCodeEnum.REQUEST_DATA_ERROR.getCode(),
                    MessageFormat.format(ResultCodeEnum.REQUEST_DATA_ERROR.getMessage(), mobile), null));
        }
        Login one = loginService.findOne(mobile);
        User user = login.getUser();
        try {
            capitalService.deduction(login, user, amount);
        } catch (Exception e) {
            throw new ApiResultException(ApiResult.withCodeAndMessage(ResultCodeEnum.DEDUCTION_FAILURE.getCode(),
                    ResultCodeEnum.DEDUCTION_FAILURE.getMessage(), null));
        }
        return ApiResult.withOk("扣款金额:" + amount + ",手机号码:" + mobile + ",扣款成功");
    }


}
