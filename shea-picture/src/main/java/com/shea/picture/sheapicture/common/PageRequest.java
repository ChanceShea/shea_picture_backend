package com.shea.picture.sheapicture.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : Shea.
 * @since : 2026/4/17 19:55
 */
@Data
public class PageRequest implements Serializable {

    private int current = 1;

    private int pageSize = 10;

    private String sortField;

    private String sortOrder = "descend";
}
