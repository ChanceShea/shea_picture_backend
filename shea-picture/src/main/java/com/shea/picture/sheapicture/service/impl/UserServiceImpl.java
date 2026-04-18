package com.shea.picture.sheapicture.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shea.picture.sheapicture.domain.User;
import com.shea.picture.sheapicture.service.UserService;
import com.shea.picture.sheapicture.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author xgw
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2026-04-18 09:05:51
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




