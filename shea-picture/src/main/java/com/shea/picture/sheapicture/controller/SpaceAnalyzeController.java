package com.shea.picture.sheapicture.controller;

import com.shea.picture.sheapicture.common.Result;
import com.shea.picture.sheapicture.domain.dto.space.*;
import com.shea.picture.sheapicture.domain.entity.Space;
import com.shea.picture.sheapicture.domain.entity.User;
import com.shea.picture.sheapicture.domain.vo.*;
import com.shea.picture.sheapicture.exception.ErrorCode;
import com.shea.picture.sheapicture.service.SpaceAnalyzeService;
import com.shea.picture.sheapicture.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.shea.picture.sheapicture.exception.ThrowUtils.throwIf;

/**
 * @author : Shea.
 * @since : 2026/4/25 12:48
 */
@RestController
@RequestMapping("/space/analyze")
@RequiredArgsConstructor
public class SpaceAnalyzeController {

    private final SpaceAnalyzeService spaceAnalyzeService;
    private final UserService userService;

    /**
     * 获取空间使用分析
     * @param dto 空间使用分析参数
     * @param request 请求
     * @return 空间使用分析结果
     */
    @PostMapping("/usage")
    public Result<SpaceUsageAnalyzeVO> getSpaceUsageAnalyzeVO(
            @RequestBody SpaceUsageAnalyzeDTO dto,
            HttpServletRequest request) {
        throwIf(dto == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        return Result.success(spaceAnalyzeService.getSpaceUsageAnalyze(dto,loginUser));
    }

    /**
     * 获取空间分类分析
     * @param dto 空间分类分析参数
     * @param request 请求
     * @return 空间分类分析结果
     */
    @PostMapping("/category")
    public Result<List<SpaceCategoryAnalyzeVO>> getSpaceCategoryAnalyze(
            @RequestBody SpaceCategoryAnalyzeDTO dto,
            HttpServletRequest request) {
        throwIf(dto == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        return Result.success(spaceAnalyzeService.getSpaceCategoryAnalyze(dto,loginUser));
    }

    /**
     * 获取空间标签分析
     * @param dto 空间标签分析参数
     * @param request 请求
     * @return 空间标签分析结果
     */
    @PostMapping("/tag")
    public Result<List<SpaceTagAnalyzeVO>> getSpaceTagAnalyze(
            @RequestBody SpaceTagAnalyzeDTO dto,
            HttpServletRequest request) {
        throwIf(dto == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        return Result.success(spaceAnalyzeService.getSpaceTagAnalyze(dto,loginUser));
    }

    /**
     * 获取空间大小分析
     * @param dto 空间大小分析参数
     * @param request 请求
     * @return 空间大小分析结果
     */
    @PostMapping("/size")
    public Result<List<SpaceSizeAnalyzeVO>> getSpaceSizeAnalyze(
            @RequestBody SpaceSizeAnalyzeDTO dto,
            HttpServletRequest request) {
        throwIf(dto == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        return Result.success(spaceAnalyzeService.getSpaceSizeAnalyze(dto,loginUser));
    }

    /**
     * 获取空间用户上传行为分析
     * @param dto 空间用户上传行为分析参数
     * @param request 请求
     * @return 空间用户分析结果
     */
    @PostMapping("/user")
    public Result<List<SpaceUserAnalyzeVO>> getSpaceUserAnalyze(
            @RequestBody SpaceUserAnalyzeDTO dto,
            HttpServletRequest request) {
        throwIf(dto == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        return Result.success(spaceAnalyzeService.getSpaceUserAnalyze(dto,loginUser));
    }

    /**
     * 获取空间排名分析
     * @param dto 空间排名分析参数
     * @param request 请求
     * @return 空间排名分析结果
     */
    @PostMapping("/rank")
    public Result<List<Space>> getSpaceRankAnalyze(
            @RequestBody SpaceRankAnalyzeDTO dto,
            HttpServletRequest request) {
        throwIf(dto == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        return Result.success(spaceAnalyzeService.getSpaceRankAnalyze(dto,loginUser));
    }
}
