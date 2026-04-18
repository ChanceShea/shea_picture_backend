package com.shea.picture.sheapicture.controller;

import com.shea.picture.sheapicture.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : Shea.
 * @since : 2026/4/17 20:01
 */
@RestController
@RequestMapping("/")
public class MainController {

    @GetMapping("/health")
    public Result<String > health() {
        return Result.success("ok");
    }
}
