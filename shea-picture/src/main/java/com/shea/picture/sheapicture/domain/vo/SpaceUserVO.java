package com.shea.picture.sheapicture.domain.vo;

import com.shea.picture.sheapicture.domain.entity.SpaceUser;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : Shea.
 * @since : 2026/4/25 21:24
 */
@Data
public class SpaceUserVO implements Serializable {

    private final static long serialVersionUID = 1L;

    /**
     * ID
     */
    private Long id;

    /**
     * 空间ID
     */
    private Long spaceId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 空间角色：viewer/editor/admin
     */
    private String spaceRole;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 用户信息
     */
    private UserVO user;

    /**
     * 空间信息
     */
    private SpaceVO space;

    public static SpaceUser voToObj(SpaceUserVO vo) {
        if (vo == null) {
            return null;
        }
        SpaceUser spaceUser = new SpaceUser();
        BeanUtils.copyProperties(vo, spaceUser);
        return spaceUser;
    }

    public static SpaceUserVO objToVo(SpaceUser spaceUser) {
        if (spaceUser == null) {
            return null;
        }
        SpaceUserVO vo = new SpaceUserVO();
        BeanUtils.copyProperties(spaceUser, vo);
        return vo;
    }

}
