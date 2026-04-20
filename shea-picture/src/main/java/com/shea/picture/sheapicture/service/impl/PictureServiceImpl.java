package com.shea.picture.sheapicture.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.shea.picture.sheapicture.domain.dto.filt.UploadPictureDTO;
import com.shea.picture.sheapicture.domain.dto.picture.PictureQueryDTO;
import com.shea.picture.sheapicture.domain.dto.picture.PictureReviewDTO;
import com.shea.picture.sheapicture.domain.dto.picture.PictureUploadBatchDTO;
import com.shea.picture.sheapicture.domain.dto.picture.PictureUploadDTO;
import com.shea.picture.sheapicture.domain.entity.Picture;
import com.shea.picture.sheapicture.domain.entity.User;
import com.shea.picture.sheapicture.domain.enums.PictureReviewStatus;
import com.shea.picture.sheapicture.domain.vo.PictureVO;
import com.shea.picture.sheapicture.domain.vo.UserVO;
import com.shea.picture.sheapicture.exception.BusinessException;
import com.shea.picture.sheapicture.exception.ErrorCode;
import com.shea.picture.sheapicture.manager.upload.FilePictureUpload;
import com.shea.picture.sheapicture.manager.upload.PictureUploadTemplate;
import com.shea.picture.sheapicture.manager.upload.UrlPictureUpload;
import com.shea.picture.sheapicture.mapper.PictureMapper;
import com.shea.picture.sheapicture.service.PictureService;
import com.shea.picture.sheapicture.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.shea.picture.sheapicture.exception.ThrowUtils.throwIf;

/**
 * @author xgw
 * @description 针对表【picture(图片)】的数据库操作Service实现
 * @createDate 2026-04-18 19:02:45
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
        implements PictureService {

    private final FilePictureUpload filePictureUpload;
    private final UrlPictureUpload urlPictureUpload;
    private final UserService userService;
    private final StringRedisTemplate stringRedisTemplate;
    private final Cache<String,String> LOCAL_CACHE = Caffeine.newBuilder()
            .initialCapacity(1024)
            .maximumSize(10_000L) // 最大缓存数量
            .expireAfterWrite(Duration.ofMinutes(10)) // 缓存过期时间
            .build();

    @Override
    public void validPicture(Picture picture) {
        throwIf(picture == null, ErrorCode.PARAMS_ERROR);
        Long id = picture.getId();
        String url = picture.getUrl();
        String introduction = picture.getIntroduction();
        throwIf(ObjectUtil.isNull(id), ErrorCode.PARAMS_ERROR, "id不能为空");
        if (StrUtil.isNotBlank(url)) {
            throwIf(url.length() > 1024, ErrorCode.PARAMS_ERROR, "url过长");
        }
        if (StrUtil.isNotBlank(introduction)) {
            throwIf(introduction.length() > 800, ErrorCode.PARAMS_ERROR, "简介过长");
        }
    }

    @Override
    public PictureVO uploadPicture(Object inputSource, PictureUploadDTO dto, User loginUser) {
        // 校验参数
        throwIf(loginUser == null, ErrorCode.NO_AUTH_ERROR);
        Long pictureId = null;
        if (dto != null) {
            pictureId = dto.getId();
        }
        // 如果是更新，判断图片是否存在
        if (pictureId != null) {
            Picture oldPicture = this.getById(pictureId);
            throwIf(oldPicture == null, ErrorCode.PARAMS_ERROR, "图片不存在");
            // 仅本人或管理员可以更新图片
            if (!Objects.equals(oldPicture.getUserId(), loginUser.getId()) && !userService.isAdmin(loginUser)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }
        // 上传图片，附带图片信息
        // 按照用户id划分目录
        String uploadPathPrefix = String.format("public/%s", loginUser.getId());
        // 根据inputSource的类型区分上传方式
        PictureUploadTemplate template = filePictureUpload;
        if (inputSource instanceof String) {
            template = urlPictureUpload;
        }
        UploadPictureDTO uploadPictureDTO = template.uploadPicture(inputSource, uploadPathPrefix);
        // 拷贝图片信息
        String picName = uploadPictureDTO.getPicName();
        if (dto != null && dto.getPicName() != null) {
            picName = dto.getPicName();
        }
        Picture picture = Picture
                .builder()
                .url(uploadPictureDTO.getUrl())
                .name(picName)
                .picSize(uploadPictureDTO.getPicSize())
                .picWidth(uploadPictureDTO.getPicWidth())
                .picHeight(uploadPictureDTO.getPicHeight())
                .picScale(uploadPictureDTO.getPicScale())
                .picFormat(uploadPictureDTO.getPicFormat())
                .userId(loginUser.getId())
                .build();
        this.fillReviewParams(picture, loginUser);
        // 如果pictureId不为空，表示更新，否则表示新增
        if (pictureId != null) {
            picture.setId(pictureId);
            picture.setEditTime(new Date());
        }
        boolean result = this.saveOrUpdate(picture);
        throwIf(!result, ErrorCode.OPERATION_ERROR, "图片上传失败，数据库操作失败");
        return PictureVO.objToVo(picture);
    }

    @Override
    public QueryWrapper<Picture> getQueryWrapper(PictureQueryDTO dto) {
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        if (dto == null) {
            return queryWrapper;
        }
        String searchText = dto.getSearchText();
        if (StrUtil.isNotBlank(searchText)) {
            queryWrapper
                    .and(qw -> qw.like("name", searchText))
                    .or()
                    .like("introduction", searchText);
        }
        queryWrapper.eq(ObjectUtil.isNotEmpty(dto.getId()), "id", dto.getId());
        queryWrapper.eq(ObjectUtil.isNotEmpty(dto.getUserId()), "userId", dto.getUserId());
        queryWrapper.eq(StrUtil.isNotEmpty(dto.getCategory()), "category", dto.getCategory());
        queryWrapper.eq(ObjectUtil.isNotEmpty(dto.getPicSize()), "picSize", dto.getPicSize());
        queryWrapper.eq(ObjectUtil.isNotEmpty(dto.getPicWidth()), "picWidth", dto.getPicWidth());
        queryWrapper.eq(ObjectUtil.isNotEmpty(dto.getPicHeight()), "picHeight", dto.getPicHeight());
        queryWrapper.eq(ObjectUtil.isNotEmpty(dto.getPicScale()), "picScale", dto.getPicScale());
        queryWrapper.eq(ObjectUtil.isNotEmpty(dto.getReviewStatus()), "reviewStatus", dto.getReviewStatus());
        queryWrapper.eq(ObjectUtil.isNotEmpty(dto.getReviewerId()), "reviewerId", dto.getReviewerId());
        queryWrapper.like(StrUtil.isNotEmpty(dto.getName()), "name", dto.getName());
        queryWrapper.like(StrUtil.isNotEmpty(dto.getIntroduction()), "introduction", dto.getIntroduction());
        queryWrapper.like(StrUtil.isNotEmpty(dto.getPicFormat()), "picFormat", dto.getPicFormat());
        queryWrapper.like(ObjectUtil.isNotEmpty(dto.getReviewMessage()), "reviewMessage", dto.getReviewMessage());
        List<String> tags = dto.getTags();
        if (CollUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // 排序
        queryWrapper.orderBy(StrUtil.isNotEmpty(dto.getSortField()), dto.getSortOrder().equals("ascend"), dto.getSortField());
        return queryWrapper;
    }

    @Override
    public PictureVO getPictureVO(Picture picture, HttpServletRequest request) {
        PictureVO pictureVO = PictureVO.objToVo(picture);
        Long userId = picture.getUserId();
        if (userId != null && userId > 0) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            pictureVO.setUser(userVO);
        }
        return pictureVO;
    }

    @Override
    public Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request) {
        List<Picture> records = picturePage.getRecords();
        Page<PictureVO> pictureVOPage = new Page<>(picturePage.getCurrent(), picturePage.getSize(), picturePage.getTotal());
        if (CollUtil.isEmpty(records)) {
            return pictureVOPage;
        }
        List<PictureVO> pictureVOList = records.stream()
                .map(PictureVO::objToVo)
                .collect(Collectors.toList());
        Set<Long> userIdSet = records.stream()
                .map(Picture::getUserId)
                .collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet)
                .stream()
                .collect(Collectors.groupingBy(User::getId));
        pictureVOList.forEach(vo -> {
            Long userId = vo.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            vo.setUser(userService.getUserVO(user));
        });
        pictureVOPage.setRecords(pictureVOList);
        return pictureVOPage;
    }

    @Override
    public void reviewPicture(PictureReviewDTO dto, User loginUser) {
        // 1. 校验参数
        throwIf(dto == null, ErrorCode.PARAMS_ERROR);
        Long id = dto.getId();
        Integer reviewStatus = dto.getReviewStatus();
        PictureReviewStatus reviewStatusEnum = PictureReviewStatus.getNameByCode(reviewStatus);
        String reviewMessage = dto.getReviewMessage();
        if (id == null || reviewStatusEnum == null || PictureReviewStatus.REVIEWING.equals(reviewStatusEnum)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        // 2. 判断图片是否存在
        Picture oldPicture = this.getById(id);
        throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR, "图片不存在");
        // 3. 校验审核状态是否重复
        if (oldPicture.getReviewStatus().equals(reviewStatus)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "审核状态未改变");
        }
        // 4. 数据库操作
        Picture picture = new Picture();
        BeanUtils.copyProperties(dto, picture);
        picture.setUserId(loginUser.getId());
        picture.setReviewTime(new Date());
        boolean result = this.updateById(picture);
        throwIf(!result, ErrorCode.OPERATION_ERROR, "图片审核失败");
    }

    @Override
    public void fillReviewParams(Picture picture, User loginUser) {
        // 管理员图片自动过审
        if (userService.isAdmin(loginUser)) {
            picture.setReviewStatus(PictureReviewStatus.PASS.getCode());
            picture.setReviewerId(loginUser.getId());
            picture.setReviewTime(new Date());
            picture.setReviewMessage("管理员图片自动过审");
        } else {
            // 非管理员更新图片，审核状态设为待审核
            picture.setReviewStatus(PictureReviewStatus.REVIEWING.getCode());
        }
    }

    @Override
    public Integer uploadPictureByBatch(PictureUploadBatchDTO dto, User loginUser) {
        // 校验参数
        throwIf(dto == null, ErrorCode.PARAMS_ERROR, "参数不能为空");
        Integer count = dto.getCount();
        throwIf(count > 30, ErrorCode.PARAMS_ERROR, "上传数量不能超过30");
        String namePrefix = dto.getNamePrefix();
        if (StrUtil.isBlank(namePrefix)) {
            namePrefix = dto.getSearchText();
        }
        // 抓取内容
        String fetchUrl = String.format("https://cn.bing.com/images/async?q=%s&mmasync=1", dto.getSearchText());
        Document document;
        try {
            document = Jsoup.connect(fetchUrl).get();
        } catch (IOException e) {
            log.error("获取资源失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取页面失败");
        }
        // 解析内容
        Element div = document.getElementsByClass("dgControl").first();
        if (ObjectUtil.isEmpty(div)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取元素失败");
        }
        Elements imgElementList = div.select("img.mimg");
        // 遍历元素，依次上传图片
        int successCount = 0;
        for (Element element : imgElementList) {
            String fileUrl = element.attr("src");
            if (StrUtil.isBlank(fileUrl)) {
                log.info("图片地址为空，跳过第{}张图片", successCount + 1);
            }
            // 处理图片地址，防止转义和对象存储冲突问题
            int questionMark = fileUrl.indexOf("?");
            if (questionMark > -1) {
                fileUrl = fileUrl.substring(0, questionMark);
            }
            try {
                // 上传图片
                PictureUploadDTO pictureUploadDTO = new PictureUploadDTO();
                pictureUploadDTO.setPicName(namePrefix + "_" + (successCount + 1));
                pictureUploadDTO.setUrl(fileUrl);
                PictureVO pictureVO = this.uploadPicture(fileUrl, pictureUploadDTO, loginUser);
                log.info("图片上传成功，图片ID：{}", pictureVO.getId());
                successCount++;
            } catch (Exception e) {
                log.error("图片上传失败，图片URL：{}", fileUrl, e);
            }
            if (successCount >= count) {
                break;
            }
        }
        return successCount;
    }

    @Override
    public Page<PictureVO> listPictureVOByPageWithCache(PictureQueryDTO dto, HttpServletRequest request) {
        long current = dto.getCurrent();
        long size = dto.getPageSize();
        throwIf(size > 20,ErrorCode.OPERATION_ERROR,"用户查询记录不能超过20条");
        // 普通用户默认只能看到审核通过的图片
        dto.setReviewStatus(PictureReviewStatus.PASS.getCode());
        String queryCondition = JSONUtil.toJsonStr(dto);
        String hashKey = DigestUtils.md5DigestAsHex(queryCondition.getBytes());
        // 先从本地缓存中查询，
        String cacheKey = String.format("sheapicture:listPictureVOByPage:%s",hashKey);
        String cacheValue = LOCAL_CACHE.getIfPresent(cacheKey);
        if (cacheValue != null) {
            // 如果缓存命中，直接返回缓存结果
            return JSONUtil.toBean(cacheValue, Page.class);
        }
        // 如果本地缓存未命中，查询分布式缓存
        String redisCache = stringRedisTemplate.opsForValue().get(cacheKey);
        if (redisCache != null) {
            // 如果分布式缓存命中，直接返回缓存结果，并将结果存入到本地缓存中
            Page<PictureVO> cachePage = JSONUtil.toBean(redisCache, Page.class);
            LOCAL_CACHE.put(cacheKey, redisCache);
            return cachePage;
        }
        Page<Picture> page = this.page(new Page<>(current, size), this.getQueryWrapper(dto));
        // 如果本地缓存和分布式缓存都未命中，查询数据库并缓存结果，然后将结果存入到本地缓存和Redis缓存中
        // 随机过期时间，防止缓存雪崩
        int cacheExpireTime = 300 + RandomUtil.randomInt(0, 300);
        stringRedisTemplate.opsForValue().set(cacheKey, JSONUtil.toJsonStr(page), cacheExpireTime, TimeUnit.SECONDS);
        LOCAL_CACHE.put(cacheKey, JSONUtil.toJsonStr(page));
        Page<PictureVO> result = this.getPictureVOPage(page, request);
        return result;
    }
}




