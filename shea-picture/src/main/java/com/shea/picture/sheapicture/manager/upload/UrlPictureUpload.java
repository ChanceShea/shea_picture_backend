package com.shea.picture.sheapicture.manager.upload;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.shea.picture.sheapicture.config.CosClientConfig;
import com.shea.picture.sheapicture.exception.BusinessException;
import com.shea.picture.sheapicture.exception.ErrorCode;
import com.shea.picture.sheapicture.manager.CosManager;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static com.shea.picture.sheapicture.exception.ThrowUtils.throwIf;

/**
 * URL图片上传实现类
 *
 * @author : Shea.
 * @since : 2026/4/19 21:19
 */
@Service
public class UrlPictureUpload extends PictureUploadTemplate {

    public UrlPictureUpload(CosClientConfig cosClientConfig, CosManager cosManager) {
        super(cosClientConfig, cosManager);
    }

    @Override
    protected void processFile(Object inputSource, File file) throws Exception {
        String fileUrl = (String) inputSource;
        HttpUtil.downloadFile(fileUrl, file);
    }

    @Override
    protected String getOriginalFilename(Object inputSource) {
        return FileUtil.mainName((String) inputSource);
    }

    @Override
    protected void validPicture(Object inputSource) {
        String fileUrl = (String) inputSource;
        // 校验非空
        throwIf(StrUtil.isBlank(fileUrl), ErrorCode.PARAMS_ERROR, "文件Url不能为空");
        // 校验URL格式
        try {
            new URL(fileUrl);
        } catch (MalformedURLException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件Url格式不正确");
        }
        // 校验URL的协议
        throwIf(!fileUrl.startsWith("http://") && !fileUrl.startsWith("https://"), ErrorCode.PARAMS_ERROR, "文件Url协议不支持");
        // 发送HEAD请求验证文件是否存在
        try (HttpResponse resp = HttpUtil.createRequest(Method.HEAD, fileUrl).execute()) {
            // 未正常返回，无需执行其他逻辑
            if (resp.getStatus() != HttpStatus.HTTP_OK) {
                return;
            }
            // 校验文件类型
            String contentType = resp.header("Content-Type");
            if (StrUtil.isNotBlank(contentType)) {
                final List<String> ALLOW_CONTENT_TYPES = Arrays.asList("image/jpeg", "image/png", "image/webp", "image/jpg");
                throwIf(!ALLOW_CONTENT_TYPES.contains(contentType.toLowerCase()), ErrorCode.PARAMS_ERROR, "文件类型不支持");
            }
            // 校验文件大小
            String length = resp.header("Content-Length");
            if (StrUtil.isNotBlank(length)) {
                try {
                    long fileSize = Long.parseLong(length);
                    final long ONE_M = 1024 * 1024;
                    throwIf(fileSize > 2 * ONE_M, ErrorCode.PARAMS_ERROR, "文件大小不能超过2MB");
                } catch (NumberFormatException e) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小格式异常");
                }
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件Url无法访问");
        }
    }
}
