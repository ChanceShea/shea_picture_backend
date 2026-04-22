package com.shea.picture.sheapicture.api.imagesearch.sub;

import com.shea.picture.sheapicture.api.imagesearch.model.ImageSearchResult;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 图片搜索API门面类
 * @author : Shea.
 * @since : 2026/4/22 19:33
 */
@Slf4j
public class ImageSearchApiFacade {
    public static List<ImageSearchResult> searchImage(String imageUrl) {
        String imagePageUrl = GetImagePageApi.getImagePageUrl(imageUrl);
        String imageFirstUrl = GetImageFirstUrlApi.getImageFirstUrl(imagePageUrl);
        List<ImageSearchResult> imageList = GetImageListApi.getImageList(imageFirstUrl);
        return imageList;
    }
}
