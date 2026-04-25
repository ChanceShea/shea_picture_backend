package com.shea.picture.sheapicture.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shea.picture.sheapicture.domain.dto.space.*;
import com.shea.picture.sheapicture.domain.entity.Space;
import com.shea.picture.sheapicture.domain.entity.User;
import com.shea.picture.sheapicture.domain.vo.*;

import java.util.List;

/**
 * @author : Shea.
 * @since : 2026/4/24 16:33
 */
public interface SpaceAnalyzeService extends IService<Space>{

    SpaceUsageAnalyzeVO getSpaceUsageAnalyze(SpaceUsageAnalyzeDTO dto, User lognUser);

    List<SpaceCategoryAnalyzeVO> getSpaceCategoryAnalyze(SpaceCategoryAnalyzeDTO dto, User lognUser);

    List<SpaceTagAnalyzeVO> getSpaceTagAnalyze(SpaceTagAnalyzeDTO dto, User lognUser);

    List<SpaceSizeAnalyzeVO> getSpaceSizeAnalyze(SpaceSizeAnalyzeDTO dto, User lognUser);

    List<SpaceUserAnalyzeVO> getSpaceUserAnalyze(SpaceUserAnalyzeDTO dto, User lognUser);

    List<Space> getSpaceRankAnalyze(SpaceRankAnalyzeDTO dto, User lognUser);
}
