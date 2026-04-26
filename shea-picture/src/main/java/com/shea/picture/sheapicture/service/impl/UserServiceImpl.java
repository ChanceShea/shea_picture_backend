package com.shea.picture.sheapicture.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shea.picture.sheapicture.constant.UserConstant;
import com.shea.picture.sheapicture.domain.dto.user.UserLoginDTO;
import com.shea.picture.sheapicture.domain.dto.user.UserQueryDTO;
import com.shea.picture.sheapicture.domain.dto.user.UserRegisterDTO;
import com.shea.picture.sheapicture.domain.entity.User;
import com.shea.picture.sheapicture.domain.enums.UserRole;
import com.shea.picture.sheapicture.domain.vo.LoginUserVO;
import com.shea.picture.sheapicture.domain.vo.UserVO;
import com.shea.picture.sheapicture.exception.BusinessException;
import com.shea.picture.sheapicture.exception.ErrorCode;
import com.shea.picture.sheapicture.manager.auth.StpKit;
import com.shea.picture.sheapicture.mapper.UserMapper;
import com.shea.picture.sheapicture.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.shea.picture.sheapicture.exception.ThrowUtils.throwIf;

/**
 * @author xgw
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2026-04-18 09:05:51
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    /**
     * 用户注册
     *
     * @param dto 用户注册参数
     * @return 用户ID
     */
    @Override
    public long userRegister(UserRegisterDTO dto) {
        // 1. 参数校验
        if (StrUtil.hasBlank(dto.getUserAccount(), dto.getUserPassword(), dto.getCheckPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (dto.getUserAccount().length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名长度不能小于4");
        }
        if (dto.getUserPassword().length() < 8 || dto.getCheckPassword().length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度不能小于8");
        }
        if (!dto.getUserPassword().equals(dto.getCheckPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        // 2. 检查用户账号是否存在
        LambdaQueryWrapper<User> eq = Wrappers.lambdaQuery(User.class)
                .eq(User::getUserAccount, dto.getUserAccount());
        long count = this.count(eq);
        throwIf(count > 0, ErrorCode.PARAMS_ERROR, "用户账号已存在");
        // 3. 密码加密
        String encryptPassword = getEncryptPassword(dto.getUserPassword());
        // 4. 插入数据
        User user = new User();
        user.setUserAccount(dto.getUserAccount());
        user.setUserPassword(encryptPassword);
        user.setUserName("用户" + dto.getUserAccount());
        user.setUserRole(UserRole.USER.getValue());
        boolean save = this.save(user);
        throwIf(!save, ErrorCode.SYSTEM_ERROR, "注册失败");
        return user.getId();
    }

    @Override
    public LoginUserVO userLogin(UserLoginDTO dto, HttpServletRequest request) {
        // 1 参数校验
        if (StrUtil.hasBlank(dto.getUserAccount(), dto.getUserPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (dto.getUserAccount().length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名长度不能小于4");
        }
        if (dto.getUserPassword().length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度不能小于8");
        }
        // 2 对用户传递的密码进行加密
        String encryptPassword = getEncryptPassword(dto.getUserPassword());
        // 3 查询数据库中用户是否存在
        LambdaQueryWrapper<User> account = Wrappers.lambdaQuery(User.class)
                .eq(User::getUserAccount, dto.getUserAccount())
                .eq(User::getUserPassword, encryptPassword);
        User user = this.baseMapper.selectOne(account);
        // 3.1 不存在则抛异常
        if (user == null) {
            log.info("user login failed,userAccount cannot match password");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号不存在或密码错误");
        }
        // 4 登录成功，返回用户信息
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE,user);
        // 记录用户登录态到sa-token，便于空间鉴权使用，保证该用户信息和SpringSession中信息过期时间一致
        StpKit.SPACE.login(user.getId());
        StpKit.SPACE.getSession().set(UserConstant.USER_LOGIN_STATE,user);
        return this.getLoginUserVO(user);
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 判断用户是否登录
        User currentUser = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        throwIf(currentUser == null, ErrorCode.NOT_LOGIN_ERROR);
        // 从数据库中查询，避免缓存数据过期，造成数据不一致
        Long userId = currentUser.getId();
        currentUser = this.getById(userId);
        throwIf(currentUser == null, ErrorCode.NOT_LOGIN_ERROR);
        return currentUser;
    }

    /**
     * 根据用户信息获取登录用户信息，脱敏处理
     * @param user 用户信息
     * @return 登录用户信息
     */
    @Override
    public LoginUserVO getLoginUserVO(User user) {
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtil.copyProperties(user,loginUserVO);
        return loginUserVO;
    }

    /**
     * 根据密码明文获取加密密码
     * @param password 密码明文
     * @return 加密密码
     */
    @Override
    public String getEncryptPassword(String password) {
        final String SALT = "Shea";
        return DigestUtils.md5DigestAsHex((SALT + password).getBytes());
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        Object obj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (obj == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return true;
    }

    @Override
    public UserVO getUserVO(User user) {
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user,userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream()
                .map(this::getUserVO)
                .collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryDTO dto) {
        if (dto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        Long id = dto.getId();
        String userName = dto.getUserName();
        String userAccount = dto.getUserAccount();
        String userProfile = dto.getUserProfile();
        String userRole = dto.getUserRole();
        String sortField = dto.getSortField();
        String sortOrder = dto.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.eq(StrUtil.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StrUtil.isNotBlank(userAccount), "userAccount", userAccount);
        queryWrapper.like(StrUtil.isNotBlank(userName), "userName", userName);
        queryWrapper.like(StrUtil.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField),sortOrder.equals("ascend"),sortField);
        return queryWrapper;
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && UserRole.ADMIN.getValue().equals(user.getUserRole());
    }
}




