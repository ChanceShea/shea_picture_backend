package com.shea.picture.sheapicture.api.aliyunai.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : Shea.
 * @since : 2026/4/24 08:24
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateOutPaintingVO {

    private Output output;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Output {

        /**
         * 任务Id
         */
        private String taskId;

        /**
         * 任务状态
         * PENDING：排队中
         * RUNNING：处理中
         * SUSPENDED：挂起
         * SUCCEEDED：执行成功
         * FAILED：执行失败
         * UNKNOWN：任务不存在或状态未知
         */
        private String taskStatus;
    }

    /**
     * 接口错误码
     * <p>接口成功请求不会返回该字段</p>
     */
    private String code;

    /**
     * 接口错误信息
     * <p>接口成功请求不会返回该字段</p>
     */
    private String message;

    /**
     * 请求唯一标识
     */
    private String requestId;
}
