package com.shea.picture.sheapicture.manager.upload;

import cn.hutool.core.io.FileUtil;
import com.shea.picture.sheapicture.config.CosClientConfig;
import com.shea.picture.sheapicture.exception.ErrorCode;
import com.shea.picture.sheapicture.manager.CosManager;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static com.shea.picture.sheapicture.exception.ThrowUtils.throwIf;

/**
 * 文件上传实现类
 * @author : Shea.
 * @since : 2026/4/19 19:22
 */
@Service
public class FilePictureUpload extends PictureUploadTemplate{

    public FilePictureUpload(CosClientConfig cosClientConfig, CosManager cosManager) {
        super(cosClientConfig, cosManager);
    }

    @Override
    protected void processFile(Object inputSource, File file) throws Exception {
        MultipartFile multipartFile = (MultipartFile) inputSource;
        multipartFile.transferTo(file);
    }

    @Override
    protected String getOriginalFilename(Object inputSource) {
        MultipartFile multipartFile = (MultipartFile) inputSource;
        return multipartFile.getOriginalFilename();
    }

    @Override
    protected void validPicture(Object inputSource) {
        MultipartFile multipartFile = (MultipartFile) inputSource;
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
