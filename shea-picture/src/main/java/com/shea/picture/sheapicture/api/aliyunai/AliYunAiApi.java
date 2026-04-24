package com.shea.picture.sheapicture.api.aliyunai;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.shea.picture.sheapicture.api.aliyunai.model.CreateOutPaintingVO;
import com.shea.picture.sheapicture.api.aliyunai.model.CreateOutPaintingDTO;
import com.shea.picture.sheapicture.api.aliyunai.model.GetOutPaintingVO;
import com.shea.picture.sheapicture.exception.BusinessException;
import com.shea.picture.sheapicture.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.shea.picture.sheapicture.exception.ThrowUtils.throwIf;

/**
 * @author : Shea.
 * @since : 2026/4/24 08:33
 */
@Slf4j
@Component
public class AliYunAiApi {

    @Value("${aliYunAi.apiKey}")
    private String apiKey;
    @Value("${aliYunAi.model}")
    private String model;

    public static final String CREATE_OUT_PAINTING_TASK_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/image2image/out-painting";

    private static final String GET_OUT_PAINTING_TASK_URL = "https://dashscope.aliyuncs.com/api/v1/tasks/%s";

    // 创建任务
    public CreateOutPaintingVO createOutPaintingTask(CreateOutPaintingDTO dto) {
        throwIf(dto == null, ErrorCode.OPERATION_ERROR,"扩图异常");

        dto.setModel(model);

        HttpRequest request = HttpRequest.post(CREATE_OUT_PAINTING_TASK_URL)
                .header("Authorization", "Bearer " + apiKey)
                .header("X-DashScope-Async", "enable")
                .header("Content-Type", "application/json")
                .body(JSONUtil.toJsonStr(dto));

        try(HttpResponse response = request.execute()) {
            if (!response.isOk()) {
                log.error("请求异常：{}", response.body());
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"AI扩图失败");
            }
            CreateOutPaintingVO bean = JSONUtil.toBean(response.body(), CreateOutPaintingVO.class);
            if (bean.getCode() != null) {
                String message = bean.getMessage();
                log.error("请求异常：{}", message);
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"AI扩图失败，" + message);
            }
            return bean;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 查询创建任务结果
    public GetOutPaintingVO getOutPaintingTask(String taskId) {
        if (StrUtil.isBlank(taskId)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"任务ID不能为空");
        }
        String url = String.format(GET_OUT_PAINTING_TASK_URL, taskId);
        try(
                HttpResponse response = HttpRequest.get(url)
                        .header("Authorization", "Bearer " + apiKey)
                        .execute()) {
            if (!response.isOk()) {
                log.error("请求异常：{}", response.body());
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"AI扩图失败");
            }
            return JSONUtil.toBean(response.body(), GetOutPaintingVO.class);
        }
    }
}
