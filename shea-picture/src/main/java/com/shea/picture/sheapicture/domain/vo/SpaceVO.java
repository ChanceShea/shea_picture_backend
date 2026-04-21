package com.shea.picture.sheapicture.domain.vo;

import com.shea.picture.sheapicture.domain.entity.Space;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 空间VO
 *
 * @author : Shea.
 * @since : 2026/4/21 08:39
 */
@Data
public class SpaceVO implements Serializable {

    /**
     * 空间ID
     */
    private Long id;

    /**
     * 空间名称
     */
    private String spaceName;
    /**
     * 空间等级
     */
    private Integer spaceLevel;

    /**
     * 空间最大存储空间
     */
    private Long maxSize;

    /**
     * 空间最大文件数量
     */
    private Long maxCount;

    /**
     * 空间已使用存储空间
     */
    private Long totalSize;

    /**
     * 空间已使用文件数量
     */
    private Long totalCount;

    /**
     * 空间创建用户ID
     */
    private Long userId;

    /**
     * 空间创建时间
     */
    private Date createTime;

    /**
     * 空间编辑时间
     */
    private Date editTime;

    /**
     * 空间更新时间
     */
    private Date updateTime;

    /**
     * 空间创建用户
     */
    private UserVO user;


    /**
     * VO转对象
     *
     * @param spaceVO 空间VO
     * @return 空间
     */
    public static Space voToObj(SpaceVO spaceVO) {
        if (spaceVO == null) {
            return null;
        }
        Space space = new Space();
        BeanUtils.copyProperties(spaceVO, space);
        return space;
    }

    /**
     * 对象转VO
     *
     * @param space 空间
     * @return 空间VO
     */
    public static SpaceVO objToVo(Space space) {
        if (space == null) {
            return null;
        }
        SpaceVO spaceVO = new SpaceVO();
        BeanUtils.copyProperties(space, spaceVO);
        return spaceVO;
    }
}
