package com.shea.picture.sheapicture.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用的删除请求类
 * @author : Shea.
 * @since : 2026/4/17 19:56
 */
@Data
public class DeleteRequest implements Serializable {

    private String id;
}
