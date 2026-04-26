package com.shea.picture.sheapicture.manager.auth;

import com.shea.picture.sheapicture.domain.entity.Picture;
import com.shea.picture.sheapicture.domain.entity.Space;
import com.shea.picture.sheapicture.domain.entity.SpaceUser;
import lombok.Data;

/**
 * 表示用户在特定空间内的授权上下文，包含图片、空间和空间用户信息
 * @author : Shea.
 * @since : 2026/4/26 09:35
 */
@Data
public class SpaceUserAuthContext {

    /**
     * 临时参数，不同请求对应不同id
     */
    private Long id;

    /**
     * 图片id
     */
    private Long pictureId;

    /**
     * 空间id
     */
    private Long spaceId;

    /**
     * 空间用户id
     */
    private Long spaceUserId;

    /**
     * 图片
     */
    private Picture picture;

    /**
     * 空间
     */
    private Space space;

    /**
     * 空间用户
     */
    private SpaceUser spaceUser;
}
