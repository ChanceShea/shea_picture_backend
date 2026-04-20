package com.shea.picture.sheapicture.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shea.picture.sheapicture.annotation.AuthCheck;
import com.shea.picture.sheapicture.common.DeleteRequest;
import com.shea.picture.sheapicture.common.Result;
import com.shea.picture.sheapicture.constant.UserConstant;
import com.shea.picture.sheapicture.domain.dto.picture.*;
import com.shea.picture.sheapicture.domain.entity.Picture;
import com.shea.picture.sheapicture.domain.entity.User;
import com.shea.picture.sheapicture.domain.enums.PictureReviewStatus;
import com.shea.picture.sheapicture.domain.vo.PictureTagCategoryVO;
import com.shea.picture.sheapicture.domain.vo.PictureVO;
import com.shea.picture.sheapicture.exception.BusinessException;
import com.shea.picture.sheapicture.exception.ErrorCode;
import com.shea.picture.sheapicture.service.PictureService;
import com.shea.picture.sheapicture.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.shea.picture.sheapicture.exception.ThrowUtils.throwIf;

/**
 * @author : Shea.
 * @since : 2026/4/18 20:17
 */
@RestController
@RequestMapping("/picture")
@Slf4j
@RequiredArgsConstructor
public class PictureController {

    private final UserService userService;
    private final PictureService pictureService;


    /**
     * 上传图片
     * @param multipartFile 图片
     * @param dto 图片信息
     * @param request 请求
     * @return 图片信息
     */
    @PostMapping("/upload")
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Result<PictureVO> uploadPicture(
            @RequestPart("file") MultipartFile multipartFile,
            PictureUploadDTO dto,
            HttpServletRequest request
    ) {
        User loginUser = userService.getLoginUser(request);
        PictureVO pictureVO = pictureService.uploadPicture(multipartFile, dto, loginUser);
        return Result.success(pictureVO);
    }

    @PostMapping("/upload/url")
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Result<PictureVO> uploadPictureByUrl(
            @RequestBody PictureUploadDTO dto,
            HttpServletRequest request
    ) {
        User loginUser = userService.getLoginUser(request);
        String fileUrl = dto.getUrl();
        PictureVO pictureVO = pictureService.uploadPicture(fileUrl, dto, loginUser);
        return Result.success(pictureVO);
    }

    @DeleteMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Result<Boolean> removePictureById(@RequestBody DeleteRequest deleteRequest,HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Picture oldPicture = pictureService.getById(deleteRequest.getId());
        throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR,"图片不存在");
        // 验证图片是否属于当前用户或者当前用户是否是管理员，否则抛出无权限错误
        if (oldPicture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = pictureService.removeById(deleteRequest.getId());
        throwIf(!result, ErrorCode.OPERATION_ERROR);
        return Result.success(true);
    }

    @PutMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Result<Boolean> updatePicture(@RequestBody PictureUpdateDTO dto,HttpServletRequest request) {
        if (dto == null || dto.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Picture picture = new Picture();
        BeanUtils.copyProperties(dto, picture);
        // 将List<String> tags转换为String
        picture.setTags(JSONUtil.toJsonStr(dto.getTags()));
        // 参数校验
        pictureService.validPicture(picture);
        // 判断是否存在
        Long id = dto.getId();
        Picture oldPicture = pictureService.getById(id);
        throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR,"图片不存在");
        User loginUser = userService.getLoginUser(request);
        // 填充审核参数
        pictureService.fillReviewParams(picture, loginUser);
        boolean result = pictureService.updateById(picture);
        throwIf(!result, ErrorCode.OPERATION_ERROR);
        return Result.success(true);
    }

    /**
     * 根据id获取图片
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get/vo")
    public Result<PictureVO> getPictureVOById(Long id, HttpServletRequest request) {
        throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        Picture picture = pictureService.getById(id);
        throwIf(picture == null, ErrorCode.NOT_FOUND_ERROR,"图片不存在");
        return Result.success(PictureVO.objToVo(picture));
    }

    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Result<Page<Picture>> listPictureByPage(@RequestBody PictureQueryDTO dto, HttpServletRequest request) {
        long current = dto.getCurrent();
        long size = dto.getPageSize();
        Page<Picture> page = pictureService.page(new Page<>(current, size),pictureService.getQueryWrapper(dto));
        return Result.success(page);
    }

    @PostMapping("/list/page/vo")
    public Result<Page<PictureVO>> listPictureVOByPage(@RequestBody PictureQueryDTO dto, HttpServletRequest request) {
        long current = dto.getCurrent();
        long size = dto.getPageSize();
        throwIf(size > 20,ErrorCode.OPERATION_ERROR,"用户查询记录不能超过20条");
        // 普通用户默认只能看到审核通过的图片
        dto.setReviewStatus(PictureReviewStatus.PASS.getCode());
        Page<Picture> page = pictureService.page(new Page<>(current, size),pictureService.getQueryWrapper(dto));
        return Result.success(pictureService.getPictureVOPage(page,request));
    }
    
//    @PostMapping("/list/page/vo/redis/cache")
//    public Result<Page<PictureVO>> listPictureVOByPageRedisCache(@RequestBody PictureQueryDTO dto, HttpServletRequest request) {
//        long current = dto.getCurrent();
//        long size = dto.getPageSize();
//        throwIf(size > 20,ErrorCode.OPERATION_ERROR,"用户查询记录不能超过20条");
//        // 普通用户默认只能看到审核通过的图片
//        dto.setReviewStatus(PictureReviewStatus.PASS.getCode());
//        String queryCondition = JSONUtil.toJsonStr(dto);
//        String hashKey = DigestUtils.md5DigestAsHex(queryCondition.getBytes());
//        String redisKey = String.format("sheapicture:listPictureVOByPage:%s",hashKey);
//        String cache = stringRedisTemplate.opsForValue().get(redisKey);
//        if (cache != null) {
//            // 如果缓存命中，直接返回缓存结果
//            Page<PictureVO> cachePage = JSONUtil.toBean(cache, Page.class);
//            return Result.success(cachePage);
//        }
//        // 如果缓存未命中，查询数据库并缓存结果
//        Page<Picture> page = pictureService.page(new Page<>(current, size),pictureService.getQueryWrapper(dto));
//        // 随机过期时间，防止缓存雪崩
//        int cacheExpireTime = 300 + RandomUtil.randomInt(0, 300);
//        stringRedisTemplate.opsForValue().set(redisKey, JSONUtil.toJsonStr(page), cacheExpireTime, TimeUnit.SECONDS);
//        return Result.success(pictureService.getPictureVOPage(page,request));
//    }

    @PostMapping("/list/page/vo/cache")
    public Result<Page<PictureVO>> listPictureVOByPageWithCache(@RequestBody PictureQueryDTO dto, HttpServletRequest request) {
        return Result.success(pictureService.listPictureVOByPageWithCache(dto, request));
    }

    @PostMapping("/edit")
    public Result<Boolean> editPicture(@RequestBody PictureEditDTO dto, HttpServletRequest request) {
        if (dto == null || dto.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Picture picture = new Picture();
        BeanUtils.copyProperties(dto, picture);
        picture.setTags(JSONUtil.toJsonStr(dto.getTags()));
        // 设置编辑时间（用户）
        picture.setEditTime(new Date());
        // 数据校验
        pictureService.validPicture(picture);
        User loginUser = userService.getLoginUser(request);
        // 填充审核参数
        pictureService.fillReviewParams(picture, loginUser);
        Long id = dto.getId();
        Picture oldPicture = pictureService.getById(id);
        throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR,"图片不存在");
        // 仅本人或者管理员可以编辑
        if (!oldPicture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = pictureService.updateById(picture);
        throwIf(!result, ErrorCode.OPERATION_ERROR);
        return Result.success(true);
    }

    @GetMapping("/tag_category")
    public Result<PictureTagCategoryVO> listPictureTagCategory() {
        PictureTagCategoryVO vo = new PictureTagCategoryVO();
        List<String> tagList = Arrays.asList("热门","生活","高清","艺术","校园","背景","简历","创意");
        List<String> categoryList = Arrays.asList("模板","电商","表情包","素材","海报");
        vo.setTagList(tagList);
        vo.setCategoryList(categoryList);
        return Result.success(vo);
    }

    @PostMapping("/review")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Result<Boolean> reviewPicture(@RequestBody PictureReviewDTO dto, HttpServletRequest request) {
        throwIf(dto == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        pictureService.reviewPicture(dto, loginUser);
        return Result.success(true);
    }

    @PostMapping("/upload/batch")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Result<Integer> uploadPictureByBatch(@RequestBody PictureUploadBatchDTO dto, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return Result.success(pictureService.uploadPictureByBatch(dto, loginUser));
    }
}
