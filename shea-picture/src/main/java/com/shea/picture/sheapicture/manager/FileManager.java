package com.shea.picture.sheapicture.manager;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.shea.picture.sheapicture.config.CosClientConfig;
import com.shea.picture.sheapicture.domain.dto.filt.UploadPictureDTO;
import com.shea.picture.sheapicture.exception.BusinessException;
import com.shea.picture.sheapicture.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.shea.picture.sheapicture.exception.ThrowUtils.throwIf;

/**
 * 通用文件管理器
 * @author : Shea.
 * @since : 2026/4/18 19:12
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Deprecated
public class FileManager {

    private final CosClientConfig cosClientConfig;
    private final CosManager cosManager;

    /**
     * 上传图片
     * @param multipartFile 文件
     * @param uploadPathPrefix 上传路径前缀
     * @return 上传图片DTO
     */
    public UploadPictureDTO uploadPicture(MultipartFile multipartFile,String uploadPathPrefix) {
        // 校验图片
        validPicture(multipartFile);
        // 图片上传地址
        String uuid = UUID.randomUUID().toString();
        String originalFilename = multipartFile.getOriginalFilename();
        // 自己凭借文件上传路径，而不是使用原始文件名称，增强安全性
        String uploadFileName = String.format("%s.%s.%s",
                DateUtil.formatDate(new Date()),uuid,
                FileUtil.getSuffix(originalFilename));
        String uploadPath = String.format("/%s/%s", uploadPathPrefix, uploadFileName);
        File file = null;
        try {
            file = File.createTempFile(uploadPath,null);
            multipartFile.transferTo(file);
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath, file);
            // 获取图片信息对象
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
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
        } catch (IOException e) {
            log.error("图片上传失败,{}",uploadPath);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"文件上传失败");
        } finally {
            // 清理临时文件
            deleteTemplateFile(file);
        }
    }

    public void deleteTemplateFile(File file) {
        if (file != null) {
            if (!file.delete()) {
                log.error("file delete error,filepath:{}", file.getAbsolutePath());
            }
        }
    }

    private void validPicture(MultipartFile multipartFile) {
        throwIf(multipartFile == null, ErrorCode.PARAMS_ERROR,"文件不能为空");
        // 校验文件大小
        long fileSize = multipartFile.getSize();
        final long ONE_M = 1024 * 1024;
        throwIf(fileSize > 2*ONE_M,ErrorCode.PARAMS_ERROR,"文件大小不能超过2MB");
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        // 允许上传的文件后缀列表
        final List<String> ALLOW_FORMAT_LIST = Arrays.asList("jpg", "jpeg", "webp", "png");
        throwIf(!ALLOW_FORMAT_LIST.contains(fileSuffix),ErrorCode.PARAMS_ERROR,"文件格式不支持");
    }

}
