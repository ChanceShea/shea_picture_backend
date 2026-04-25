package com.shea.picture.sheapicture.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shea.picture.sheapicture.domain.dto.space.*;
import com.shea.picture.sheapicture.domain.entity.Picture;
import com.shea.picture.sheapicture.domain.entity.Space;
import com.shea.picture.sheapicture.domain.entity.User;
import com.shea.picture.sheapicture.domain.vo.*;
import com.shea.picture.sheapicture.exception.BusinessException;
import com.shea.picture.sheapicture.exception.ErrorCode;
import com.shea.picture.sheapicture.exception.ThrowUtils;
import com.shea.picture.sheapicture.mapper.SpaceMapper;
import com.shea.picture.sheapicture.service.PictureService;
import com.shea.picture.sheapicture.service.SpaceAnalyzeService;
import com.shea.picture.sheapicture.service.SpaceService;
import com.shea.picture.sheapicture.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.shea.picture.sheapicture.exception.ThrowUtils.throwIf;

/**
 * @author : Shea.
 * @since : 2026/4/24 16:33
 */
@Service
@RequiredArgsConstructor
public class SpaceAnalyzeServiceImpl extends ServiceImpl<SpaceMapper, Space> implements SpaceAnalyzeService {

    private final UserService userService;
    private final SpaceService spaceService;
    private final PictureService pictureService;

    /**
     * 获取空间使用分析
     * @param dto 空间使用分析参数
     * @param loginUser 登录用户
     * @return 空间使用分析结果
     */
    @Override
    public SpaceUsageAnalyzeVO getSpaceUsageAnalyze(SpaceUsageAnalyzeDTO dto, User loginUser) {
        // 校验参数
        // 全空间或公共图库，需要从Picture表查询
        if (dto.isQueryAll() || dto.isQueryPublic()) {
            // 校验权限，仅管理员可访问
            checkSpaceAnalyzeAuth(dto, loginUser);
            // 统计图库使用空间
            QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("picSize");
            // 补充查询范围
            fillAnalyzeQueryWrapper(dto, queryWrapper);
            List<Object> objects = pictureService.getBaseMapper().selectObjs(queryWrapper);
            long usedCount = objects.size();
            long usedSize = objects.stream().mapToLong(obj -> (Long) obj).sum();
            return SpaceUsageAnalyzeVO.builder()
                    .usedSize(usedSize)
                    .usedCount(usedCount)
                    .build();
        }
        // 特定空间可以从Space表查询
        throwIf(dto.getSpaceId() == null || dto.getSpaceId() <= 0,ErrorCode.PARAMS_ERROR);
        Space space = spaceService.getById(dto.getSpaceId());
        throwIf(space == null, ErrorCode.PARAMS_ERROR, "空间不存在");
        checkSpaceAnalyzeAuth(dto, loginUser);
        return SpaceUsageAnalyzeVO.builder()
                .maxSize(space.getMaxSize())
                .maxCount(space.getMaxCount())
                .usedSize(space.getTotalSize())
                .usedCount(space.getTotalCount())
                .sizeUsageRatio(space.getTotalSize() * 1.0 / space.getMaxSize())
                .countUsageRatio(space.getTotalCount() * 1.0 / space.getMaxCount())
                .build();
    }


    /**
     * 获取空间分类分析
     * @param dto 空间分类分析参数
     * @param loginUser 登录用户
     * @return 空间分类分析结果
     */
    @Override
    public List<SpaceCategoryAnalyzeVO> getSpaceCategoryAnalyze(SpaceCategoryAnalyzeDTO dto, User loginUser) {
        throwIf(dto == null, ErrorCode.PARAMS_ERROR, "参数为空");
        // 校验权限
        checkSpaceAnalyzeAuth(dto, loginUser);
        // 构造查询条件
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        fillAnalyzeQueryWrapper(dto, queryWrapper);
        queryWrapper.select("category","count(*) as count","sum(picSize) as totalSize")
                .groupBy("category");
        List<Map<String, Object>> maps = pictureService.getBaseMapper().selectMaps(queryWrapper);
        return maps.stream().map(map ->
                SpaceCategoryAnalyzeVO.builder()
                        .category((String) map.get("category"))
                        .count(((Number) map.get("count")).longValue())
                        .totalSize(((Number) map.get("totalSize")).longValue())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<SpaceTagAnalyzeVO> getSpaceTagAnalyze(SpaceTagAnalyzeDTO dto, User loginUser) {
        // 校验参数
        throwIf(dto == null,ErrorCode.PARAMS_ERROR);
        // 校验权限
        checkSpaceAnalyzeAuth(dto, loginUser);
        // 构造查询条件
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        fillAnalyzeQueryWrapper(dto, queryWrapper);
        // 查询图片标签
        queryWrapper.select("tags");
        List<String> tagsJsonList = pictureService
                .getBaseMapper()
                .selectObjs(queryWrapper)
                .stream()
                .filter(ObjectUtil::isNotNull)
                .map(Object::toString)
                .collect(Collectors.toList());
        // 解析标签并统计图片数量
        Map<String, Long> tageCountMap = tagsJsonList.stream()
                // 将数组扁平化，并统计标签出现次数
                // ["Java","Python"],["Java","C"] ===> ["Java","Python","Java","PHP"]
                .flatMap(tagsJson -> JSONUtil.toList(tagsJson, String.class).stream())
                .collect(Collectors.groupingBy(tag -> tag, Collectors.counting()));
        // 转换为响应对象
        return tageCountMap.entrySet().stream()
                .sorted(Comparator.comparingLong(Map.Entry::getValue))
                .map(entry -> new SpaceTagAnalyzeVO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * 获取空间大小分析
     * @param dto 空间大小分析参数
     * @param loginUser 登录用户
     * @return 空间大小分析结果
     */
    @Override
    public List<SpaceSizeAnalyzeVO> getSpaceSizeAnalyze(SpaceSizeAnalyzeDTO dto, User loginUser) {
        throwIf(dto == null, ErrorCode.PARAMS_ERROR, "参数为空");
        // 校验权限
        checkSpaceAnalyzeAuth(dto, loginUser);
        // 构造查询条件
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        fillAnalyzeQueryWrapper(dto, queryWrapper);
        // 查询图片大小
        queryWrapper.select("picSize");
        List<Long> picSizeList = pictureService.getBaseMapper().selectObjs(queryWrapper)
                .stream()
                .map(size -> (Long) size)
                .collect(Collectors.toList());
        // 定义分段范围
        Map<String,Long> sizeRangeMap = new LinkedHashMap<>();
        sizeRangeMap.put("0-100K", picSizeList.stream().filter(size -> size <= 100 * 1024).count());
        sizeRangeMap.put("100K-500K", picSizeList.stream().filter(size -> size > 100 * 1024 && size <= 500 * 1024).count());
        sizeRangeMap.put("500K-1M", picSizeList.stream().filter(size -> size > 500 * 1024 && size <= 1024 * 1024).count());
        sizeRangeMap.put("1M+", picSizeList.stream().filter(size -> size > 1024 * 1024).count());
        return sizeRangeMap.entrySet().stream()
                .map(entry -> new SpaceSizeAnalyzeVO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * 获取空间用户上传行为分析
     * @param dto 空间用户分析参数
     * @param loginUser 登录用户
     * @return 空间用户分析结果
     */
    @Override
    public List<SpaceUserAnalyzeVO> getSpaceUserAnalyze(SpaceUserAnalyzeDTO dto, User loginUser) {
        throwIf(dto == null, ErrorCode.PARAMS_ERROR, "参数为空");
        // 校验权限
        checkSpaceAnalyzeAuth(dto, loginUser);
        // 构造查询条件
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        fillAnalyzeQueryWrapper(dto, queryWrapper);
        // 查询图片上传行为
        queryWrapper.eq(ObjectUtil.isNotEmpty(dto.getUserId()),"userId",dto.getUserId());
        String timeDimension = dto.getTimeDimension();
        switch (timeDimension) {
            case "day" :
                queryWrapper.select("DATE_FORMAT(createTime,'%Y-%m-%d') as period","count(*) as count");
                break;
            case "week" :
                queryWrapper.select("YEARWEEK(createTime) as period", "count(*) as count");
                break;
            case "month" :
                queryWrapper.select("DATE_FORMAT(createTime,'%Y-%m') as period","count(*) as count");
                break;
            default:
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "时间维度参数错误");
        }
        // 分组排序
        queryWrapper.groupBy("period")
                .orderByAsc("period");
        List<Map<String, Object>> maps = pictureService.getBaseMapper().selectMaps(queryWrapper);
        return maps.stream().map(map ->
                SpaceUserAnalyzeVO.builder()
                        .period(map.get("period").toString())
                        .count(((Number) map.get("count")).longValue())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 获取空间排名分析
     * @param dto 空间排名分析参数
     * @param loginUser 登录用户
     * @return 空间排名分析结果
     */
    @Override
    public List<Space> getSpaceRankAnalyze(SpaceRankAnalyzeDTO dto, User loginUser) {
        // 校验参数
        throwIf(dto == null, ErrorCode.PARAMS_ERROR, "参数为空");
        // 校验用户权限
        throwIf(!userService.isAdmin(loginUser), ErrorCode.NO_AUTH_ERROR, "用户没有权限");
        // 构造查询条件
        QueryWrapper<Space> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id","spaceName","userId","totalSize")
                .orderByDesc("totalSize")
                .last("limit " + dto.getTopN());
        return spaceService.list(queryWrapper);
    }


    /**
     * 校验空间分析权限
     *
     * @param dto       空间分析参数
     * @param loginUser 登录用户
     */
    private void checkSpaceAnalyzeAuth(SpaceAnalyzeDTO dto, User loginUser) {
        Long spaceId = dto.getSpaceId();
        boolean queryPublic = dto.isQueryPublic();
        boolean queryAll = dto.isQueryAll();
        if (queryAll || queryPublic) {
            throwIf(!userService.isAdmin(loginUser), ErrorCode.NO_AUTH_ERROR, "用户没有权限");
        } else {
            ThrowUtils.throwIf(spaceId == null, ErrorCode.PARAMS_ERROR);
            Space space = spaceService.getById(spaceId);
            ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
            spaceService.checkSpaceAuth(space, loginUser);
        }
    }

    /**
     * 填充空间分析查询条件
     *
     * @param dto          空间分析参数
     * @param queryWrapper 查询条件
     */
    private void fillAnalyzeQueryWrapper(SpaceAnalyzeDTO dto, QueryWrapper<Picture> queryWrapper) {
        Long spaceId = dto.getSpaceId();
        boolean queryAll = dto.isQueryAll();
        boolean queryPublic = dto.isQueryPublic();
        if (queryAll) {
            return;
        }
        if (queryPublic) {
            queryWrapper.isNull("spaceId");
            return;
        }
        if (spaceId != null) {
            queryWrapper.eq("spaceId", spaceId);
            return;
        }
        throw new BusinessException(ErrorCode.PARAMS_ERROR, "未指定查询范围");
    }

}
