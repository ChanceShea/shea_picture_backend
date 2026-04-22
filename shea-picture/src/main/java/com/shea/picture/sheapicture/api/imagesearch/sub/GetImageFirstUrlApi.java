package com.shea.picture.sheapicture.api.imagesearch.sub;

import com.shea.picture.sheapicture.exception.BusinessException;
import com.shea.picture.sheapicture.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : Shea.
 * @since : 2026/4/22 19:01
 */
@Slf4j
public class GetImageFirstUrlApi {

    public static String getImageFirstUrl(String url) {
        try {
            Document document = Jsoup.connect(url)
                    .timeout(5000)
                    .get();
            Elements scripts = document.getElementsByTag("script");
            for (Element script : scripts) {
                String content = script.html();
                if (content.contains("\"firstUrl\"")) {
                    Pattern pattern = Pattern.compile("\"firstUrl\"\\s*:\\s*\"(.*?)\"");
                    Matcher matcher = pattern.matcher(content);
                    if (matcher.find()) {
                        String firstUrl = matcher.group(1);
                        firstUrl = firstUrl.replace("\\", "");
                        return firstUrl;
                    }
                }
            }
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未找到url");
        }catch (Exception e) {
            log.error("获取图片失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取图片失败");
        }
    }

}
