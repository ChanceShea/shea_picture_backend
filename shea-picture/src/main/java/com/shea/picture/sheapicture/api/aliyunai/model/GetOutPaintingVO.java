package com.shea.picture.sheapicture.api.aliyunai.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询扩图任务响应类
 * @author : Shea.
 * @since : 2026/4/24 08:30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetOutPaintingVO {

    /**
     * 请求唯一标识，可用于请求明细溯源和问题排查
     */
    @JsonProperty("request_id")
    private String requestId;

    /**
     * 输出的任务信息
     */
    private Output output;

    /**
     * 图像统计信息
     */
    private Usage usage;

    /**
     * 输出任务信息内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Output {

        /**
         * 任务ID，查询有效期24小时
         */
        @JsonProperty("task_id")
        private String taskId;

        /**
         * 任务状态
         * PENDING：任务排队中
         * RUNNING：任务处理中
         * SUCCEEDED：任务执行成功
         * FAILED：任务执行失败
         * CANCELED：任务已取消
         * UNKNOWN：任务不存在或状态未知
         */
        @JsonProperty("task_status")
        private String taskStatus;

        /**
         * 任务提交时间
         */
        @JsonProperty("submit_time")
        private String submitTime;

        /**
         * 任务计划执行时间
         */
        @JsonProperty("scheduled_time")
        private String scheduledTime;

        /**
         * 任务完成时间
         */
        @JsonProperty("end_time")
        private String endTime;

        /**
         * 输出图像URL地址
         */
        @JsonProperty("output_image_url")
        private String outputImageUrl;

        /**
         * 任务结果统计
         */
        @JsonProperty("task_metrics")
        private TaskMetrics taskMetrics;

        /**
         * 请求失败的错误码，请求成功时不会返回此参数
         */
        private String code;

        /**
         * 请求失败的详细信息，请求成功时不会返回此参数
         */
        private String message;
    }

    /**
     * 任务结果统计内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskMetrics {

        /**
         * 总的任务数
         */
        private Integer TOTAL;

        /**
         * 任务状态为成功的任务数
         */
        private Integer SUCCEEDED;

        /**
         * 任务状态为失败的任务数
         */
        private Integer FAILED;
    }

    /**
     * 图像统计信息内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Usage {

        /**
         * 图像数量
         */
        @JsonProperty("image_count")
        private Integer imageCount;
    }
}