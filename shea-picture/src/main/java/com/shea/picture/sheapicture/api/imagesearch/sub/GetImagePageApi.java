package com.shea.picture.sheapicture.api.imagesearch.sub;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import com.shea.picture.sheapicture.exception.BusinessException;
import com.shea.picture.sheapicture.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取以图搜图页面的地址
 * @author : Shea.
 * @since : 2026/4/22 16:07
 */
@Slf4j
public class GetImagePageApi {

    public static String getImagePageUrl(String imageUrl) {
        // 1. 准备请求参数
        Map<String,Object> formData = new HashMap<>();
        formData.put("image",imageUrl);
        formData.put("tn","pc");
        formData.put("from","pc");
        // 获取当前时间戳
        Long uptime = System.currentTimeMillis();
        // 构造请求地址
        String url = "https://graph.baidu.com/upload?uptime=" + uptime;
        try {
            // 2. 发送请求
            HttpResponse response = HttpRequest.post(url)
                    .form(formData)
                    .header("acs-token", RandomUtil.randomString(6))
                    .timeout(5000)
                    .execute();
            if (response.getStatus() != HttpStatus.HTTP_OK) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"接口调用失败");
            }
            // 解析响应
            String body = response.body();
            Map<String,Object> bean = JSONUtil.toBean(body, Map.class);

            // 3. 处理响应结果
            if (bean == null || !Integer.valueOf(0).equals(bean.get("status"))) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"接口调用失败");
            }
            Map<String, Object> data = (Map<String, Object>) bean.get("data");
            String rawUrl = (String) data.get("url");
            String searchResultUrl = URLUtil.decode(rawUrl, StandardCharsets.UTF_8);
            if (searchResultUrl == null) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"未返回有效结果地址");
            }
            return searchResultUrl;
        }catch (Exception e) {
            log.error("调用百度以图搜图接口失败",e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR,e.getMessage());
        }
    }

}
