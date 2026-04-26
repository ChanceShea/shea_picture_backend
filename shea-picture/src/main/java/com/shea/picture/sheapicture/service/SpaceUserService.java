package com.shea.picture.sheapicture.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shea.picture.sheapicture.common.DeleteRequest;
import com.shea.picture.sheapicture.domain.dto.spaceuser.SpaceUserAddDTO;
import com.shea.picture.sheapicture.domain.dto.spaceuser.SpaceUserQueryDTO;
import com.shea.picture.sheapicture.domain.entity.SpaceUser;
import com.shea.picture.sheapicture.domain.vo.SpaceUserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author xgw
* @description 针对表【space_user(空间用户关联)】的数据库操作Service
* @createDate 2026-04-25 21:14:20
*/
public interface SpaceUserService extends IService<SpaceUser> {

    /**
     * 添加空间成员
     * @param dto 添加空间DTO
     * @param loginUser 登录用户
     * @return 空间ID
     */
    long addSpaceUser(SpaceUserAddDTO dto);

    /**
     * 校验空间成员
      * @param spaceUser 空间成员
     * @param add 是否是添加
     */
    void validSpaceUser(SpaceUser spaceUser, boolean add);

    /**
     * 获取空间成员VO
     * @param spaceUser 空间成员
     * @param request 请求
     * @return 空间成员VO
     */
    SpaceUserVO getSpaceUserVO(SpaceUser spaceUser, HttpServletRequest request);

    /**
     * 获取空间VO
     * @param spaceUserList 空间成员列表
     * @return 空间成员VO列表
     */
    List<SpaceUserVO> getSpaceUserVOList(List<SpaceUser> spaceUserList);

    /**
     * 获取查询条件
     * @param dto 空间查询DTO
     * @return 查询条件
     */
    QueryWrapper<SpaceUser> getQueryWrapper(SpaceUserQueryDTO dto);


    boolean removeSpaceById(DeleteRequest deleteRequest, HttpServletRequest request);

}
