package com.shea.picture.sheapicture.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shea.picture.sheapicture.domain.dto.picture.PictureQueryDTO;
import com.shea.picture.sheapicture.domain.dto.picture.PictureUploadDTO;
import com.shea.picture.sheapicture.domain.entity.Picture;
import com.shea.picture.sheapicture.domain.entity.User;
import com.shea.picture.sheapicture.domain.vo.PictureVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

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
     * @param multipartFile
     * @param pictureUploadDTO
     * @param loginUser
     * @return
     */
    PictureVO uploadPicture(MultipartFile multipartFile, PictureUploadDTO pictureUploadDTO, User loginUser);

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
}
