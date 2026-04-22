package com.shea.picture.sheapicture.api.imagesearch.sub;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.shea.picture.sheapicture.api.imagesearch.model.ImageSearchResult;
import com.shea.picture.sheapicture.exception.BusinessException;
import com.shea.picture.sheapicture.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author : Shea.
 * @since : 2026/4/22 19:19
 */
@Slf4j
public class GetImageListApi {

    public static List<ImageSearchResult> getImageList(String url) {
        try {
            HttpResponse response = HttpUtil.createGet(url).execute();
            int status = response.getStatus();
            String body = response.body();
            if (status == 200) {
                return processResponse(body);
            } else {
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"调用接口失败");
            }
        }catch (Exception e) {
            log.error("获取图片列表失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"获取图片列表失败");
        }
    }

    private static List<ImageSearchResult> processResponse(String body) {
        JSONObject jsonObject = new JSONObject(body);
        if (!jsonObject.containsKey("data")) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口返回数据中不包含图片列表");
        }
        JSONObject data = jsonObject.getJSONObject("data");
        if (!data.containsKey("list")) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口返回数据中不包含图片列表");
        }
        JSONArray list = data.getJSONArray("list");
        return JSONUtil.toList(list,ImageSearchResult.class);
    }

}
