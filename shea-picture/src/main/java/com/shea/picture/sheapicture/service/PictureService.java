package com.shea.picture.sheapicture.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shea.picture.sheapicture.common.DeleteRequest;
import com.shea.picture.sheapicture.domain.dto.picture.*;
import com.shea.picture.sheapicture.domain.entity.Picture;
import com.shea.picture.sheapicture.domain.entity.User;
import com.shea.picture.sheapicture.domain.vo.PictureVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author xgw
* @description 针对表【picture(图片)】的数据库操作Service
* @createDate 2026-04-18 19:02:45
*/
public interface PictureService extends IService<Picture> {

    /**
     * 校验图片
     * @param picture
     */
    void validPicture(Picture picture);

    /**
     * 上传图片
     * @param inputSource 输入源，可以是MultipartFile或Url
     * @param pictureUploadDTO 上传图片的DTO
     * @param loginUser 当前登录用户
     * @return
     */
    PictureVO uploadPicture(Object inputSource, PictureUploadDTO pictureUploadDTO, User loginUser);

    /**
     * 获取查询条件
     * @param dto
     * @return
     */
    QueryWrapper<Picture> getQueryWrapper(PictureQueryDTO dto);

    /**
     * 获取图片包装类（单条 脱敏处理）
     * @param picture
     * @param request
     * @return
     */
    PictureVO getPictureVO(Picture picture, HttpServletRequest request);

    /**
     * 获取图片包装类（分页 脱敏处理）
     * @param picturePage
     * @param request
     * @return
     */
    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);

    /**
     * 审核图片
     * @param pictureReviewDTO
     * @param loginUser
     * @return
     */
    void reviewPicture(PictureReviewDTO pictureReviewDTO, User loginUser);

    void fillReviewParams(Picture picture, User loginUser);

    /**
     * 批量抓取并上传图片
     * @param dto 批量上传图片的DTO
     * @param loginUser 当前登录用户
     * @return 上传图片的数量
     */
    Integer uploadPictureByBatch(PictureUploadBatchDTO dto, User loginUser);


    Page<PictureVO> listPictureVOByPageWithCache(PictureQueryDTO dto, HttpServletRequest request);

    /**
     * 清理图片文件
     * @param oldPicture 旧图片
     */
    void clearPictureFile(Picture oldPicture);

    /**
     * 校验图片权限
     * @param picture 图片
     * @param loginUser 当前登录用户
     */
    void checkPictureAuth(Picture picture, User loginUser);

    /**
     * 删除图片
     * @param deleteRequest 删除图片的请求参数
     * @param request HTTP请求
     * @return
     */
    boolean removePictureById(DeleteRequest deleteRequest, HttpServletRequest request);

    /**
     * 编辑图片
     * @param dto 编辑图片的DTO
     * @param request HTTP请求
     * @return
     */
    boolean editPicture(PictureEditDTO dto, HttpServletRequest request);

    boolean removePictureBySpaceId(DeleteRequest deleteRequest, HttpServletRequest request);

    List<PictureVO> searchPictureByColor(Long spaceId, String picColor, User loginUser);

    /**
     * 批量编辑图片
     * @param dto 编辑图片的DTO
     * @param loginUser 登录用户
     */
    void editPictureByBatch(PictureEditBatchDTO dto, User loginUser);
}
