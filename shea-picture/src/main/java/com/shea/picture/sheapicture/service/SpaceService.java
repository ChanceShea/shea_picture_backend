package com.shea.picture.sheapicture.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shea.picture.sheapicture.common.DeleteRequest;
import com.shea.picture.sheapicture.domain.dto.space.SpaceAddDTO;
import com.shea.picture.sheapicture.domain.dto.space.SpaceQueryDTO;
import com.shea.picture.sheapicture.domain.entity.Space;
import com.shea.picture.sheapicture.domain.entity.User;
import com.shea.picture.sheapicture.domain.vo.SpaceVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author xgw
* @description 针对表【space(空间)】的数据库操作Service
* @createDate 2026-04-21 08:31:16
*/
public interface SpaceService extends IService<Space> {

    /**
     * 添加空间
     * @param dto 添加空间DTO
     * @param loginUser 登录用户
     * @return 空间ID
     */
    long addSpace(SpaceAddDTO dto, User loginUser);

    /**
     * 校验空间
     * @param space 空间
     * @param add 是否是添加
     */
    void validSpace(Space space, boolean add);

    /**
     * 获取空间VO
     * @param space 空间
     * @param request 请求
     * @return 空间VO
     */
    SpaceVO getSpaceVO(Space space, HttpServletRequest request);

    /**
     * 获取空间VO分页
     * @param spacePage 空间分页
     * @param request 请求
     * @return 空间VO分页
     */
    Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request);

    /**
     * 获取查询条件
     * @param space 空间
     * @param request 请求
     * @return 查询条件
     */
    QueryWrapper<Space> getQueryWrapper(SpaceQueryDTO dto);

    /**
     * 根据空间等级填充空间对象
     * @param space 空间对象
     */
    void fillSpaceBySpaceLevel(Space space);

    boolean removeSpaceById(DeleteRequest deleteRequest, HttpServletRequest request);
}
