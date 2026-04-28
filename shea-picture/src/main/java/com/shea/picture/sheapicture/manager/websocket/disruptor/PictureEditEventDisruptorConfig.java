package com.shea.picture.sheapicture.manager.websocket.disruptor;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import com.lmax.disruptor.dsl.Disruptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 图片编辑事件 Disruptor配置
 * @author : Shea.
 * @since : 2026/4/28 08:51
 */
@Configuration
@RequiredArgsConstructor
public class PictureEditEventDisruptorConfig {

    private final PictureEditEventWorkHandler pictureEditEventWorkHandler;

    @Bean("pictureEditEventDisruptor")
    public Disruptor<PictureEditEvent> pictureEditEventDisruptor() {
        int bufferSize = 1024 * 1024;
        Disruptor<PictureEditEvent> disruptor = new Disruptor<>(
                PictureEditEvent::new,
                bufferSize,
                ThreadFactoryBuilder.create()
                        .setNamePrefix("picture-edit-event-disruptor")
                        .build()
        );
        disruptor.handleEventsWithWorkerPool(pictureEditEventWorkHandler);
        disruptor.start();
        return disruptor;
    }
}
