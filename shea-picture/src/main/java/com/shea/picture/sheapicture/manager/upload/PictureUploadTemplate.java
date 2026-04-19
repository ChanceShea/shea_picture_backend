package com.shea.picture.sheapicture.manager.upload;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.shea.picture.sheapicture.config.CosClientConfig;
import com.shea.picture.sheapicture.domain.dto.filt.UploadPictureDTO;
import com.shea.picture.sheapicture.exception.BusinessException;
import com.shea.picture.sheapicture.exception.ErrorCode;
import com.shea.picture.sheapicture.manager.CosManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Date;
import java.util.UUID;

/**
 * 图片上传模板
 * @author : Shea.
 * @since : 2026/4/19 19:12
 */
@Slf4j
@RequiredArgsConstructor
public abstract class PictureUploadTemplate {

    private final CosClientConfig cosClientConfig;
    private final CosManager cosManager;

    /**
     * 上传图片
     * @param inputSource 文件
     * @param uploadPathPrefix 上传路径前缀
     * @return 上传图片DTO
     */
    public UploadPictureDTO uploadPicture(Object inputSource, String uploadPathPrefix) {
        // 校验图片
        validPicture(inputSource);
        // 图片上传地址
        String uuid = UUID.randomUUID().toString();
        String originalFilename = getOriginalFilename(inputSource);
        // 自己构造文件上传路径，而不是使用原始文件名称，增强安全性
        String uploadFileName = String.format("%s_%s.%s",
                DateUtil.formatDate(new Date()),uuid,
                FileUtil.getSuffix(originalFilename));
        String uploadPath = String.format("/%s/%s", uploadPathPrefix, uploadFileName);
        File file = null;
        try {
            // 创建临时文件，获取文件到服务器
            file = File.createTempFile(uploadPath,null);
            // 处理文件
            processFile(inputSource,file);
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath, file);
            // 获取图片信息对象
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            return buildRequest(uploadPath, originalFilename, file, imageInfo);
        } catch (Exception e) {
            log.error("图片上传失败,{}",uploadPath,e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"文件上传失败");
        } finally {
            // 清理临时文件
            deleteTemplateFile(file);
        }
    }

    /**
     * 处理文件
     *
     * @param inputSource 输入源
     * @param file
     */
    protected abstract void processFile(Object inputSource, File file) throws Exception;

    /**
     * 获取原始文件名
     * @param inputSource 输入源
     * @return 原始文件名
     */
    protected abstract String getOriginalFilename(Object inputSource);

    /**
     * 校验图片
     * @param inputSource 输入源
     */
    protected abstract void validPicture(Object inputSource);

    /**
     * 删除临时文件
     * @param file 文件
     */
    public void deleteTemplateFile(File file) {
        if (file != null) {
            if (!file.delete()) {
                log.error("file delete error,filepath:{}", file.getAbsolutePath());
            }
        }
    }


    /**
     * 构建上传图片DTO
     * @param uploadPath 上传路径
     * @param originalFilename 原始文件名
     * @param file 文件
     * @param imageInfo 图片信息
     * @return 上传图片DTO
     */
    private UploadPictureDTO buildRequest(String uploadPath, String originalFilename, File file, ImageInfo imageInfo) {
        // 封装返回结果
        // 解析结果并返回
        return UploadPictureDTO
                .builder()
                .url(cosClientConfig.getHost() + "/" + uploadPath)
                .picName(FileUtil.mainName(originalFilename))
                .picSize(FileUtil.size(file))
                .picHeight(imageInfo.getHeight())
                .picWidth(imageInfo.getWidth())
                .picScale(NumberUtil.round((double) imageInfo.getWidth() / imageInfo.getHeight(), 2).doubleValue())
                .picFormat(imageInfo.getFormat())
                .build();
    }
}
