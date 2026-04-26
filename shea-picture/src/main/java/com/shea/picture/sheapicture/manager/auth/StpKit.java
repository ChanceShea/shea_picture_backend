package com.shea.picture.sheapicture.manager.auth;

import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.stereotype.Component;

/**
 * StpLogic门面类，管理项目中所有StpLogic账号体系
 * @author : Shea.
 * @since : 2026/4/26 09:19
 */
@Component
public class StpKit {

    public static final String SPACE_TYPE = "space";


    public static final StpLogic DEFAULT = StpUtil.stpLogic;

    /**
     * Space 会话对象，管理Space表所有账号的登录，权限认证
     */
    public static final StpLogic SPACE = new StpLogic(SPACE_TYPE);
}
