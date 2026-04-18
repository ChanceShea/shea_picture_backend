package com.shea.picture.sheapicture.manager;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import com.shea.picture.sheapicture.config.CosClientConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * 通用cos管理器
 * @author : Shea.
 * @since : 2026/4/18 18:44
 */
@Component
@RequiredArgsConstructor
public class CosManager {
    private final CosClientConfig cosClientConfig;
    private final COSClient cosClient;

    /**
     * 上传文件
     * @param key 唯一键
     * @param file 文件
     * @return 上传结果
     */
    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(),key,file);
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 获取文件
     * @param key 唯一键
     * @return 文件
     */
    public COSObject getObject(String key) {
        return cosClient.getObject(cosClientConfig.getBucket(), key);
    }

    /**
     * 上传图片（附带图片信息）
     * @param key 唯一键
     * @param file 文件
     * @return 上传结果
     */
    public PutObjectResult putPictureObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(),key,file);
        // 对图片进行处理，获取图片的基本信息
        PicOperations picOperations = new PicOperations();
        picOperations.setIsPicInfo(1);
        putObjectRequest.setPicOperations(picOperations);
        return cosClient.putObject(putObjectRequest);
    }
}
