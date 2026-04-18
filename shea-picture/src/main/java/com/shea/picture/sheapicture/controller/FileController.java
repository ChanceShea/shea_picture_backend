package com.shea.picture.sheapicture.controller;

import com.shea.picture.sheapicture.annotation.AuthCheck;
import com.shea.picture.sheapicture.common.Result;
import com.shea.picture.sheapicture.constant.UserConstant;
import com.shea.picture.sheapicture.exception.BusinessException;
import com.shea.picture.sheapicture.exception.ErrorCode;
import com.shea.picture.sheapicture.manager.CosManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @author : Shea.
 * @since : 2026/4/18 18:47
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    private final CosManager cosManager;

    public FileController(CosManager cosManager) {
        this.cosManager = cosManager;
    }

    @PostMapping("/test/upload")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Result<String> uploadFile(@RequestPart("file") MultipartFile multipartFile) {
        String fileName = multipartFile.getOriginalFilename();
        String filepath = String.format("/test/%s",fileName);
        File file = null;
        try {
            file = File.createTempFile(filepath,null);
            multipartFile.transferTo(file);
            cosManager.putObject(filepath,file);
            return Result.success(filepath);
        } catch (IOException e) {
            log.error("file upload error,filepath:{}",filepath);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"文件上传失败");
        } finally {
            if (file != null) {
                if (!file.delete()) {
                    log.error("file delete error,filepath:{}",filepath);
                }
            }
        }
    }
}
