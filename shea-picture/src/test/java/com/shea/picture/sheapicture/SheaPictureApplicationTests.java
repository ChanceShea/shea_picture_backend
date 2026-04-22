package com.shea.picture.sheapicture;

import com.shea.picture.sheapicture.domain.entity.Picture;
import com.shea.picture.sheapicture.service.PictureService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class SheaPictureApplicationTests {

    @Autowired
    private PictureService pictureService;

    @Test
    void testColor() {
        List<Picture> list = pictureService.list();

    }

}
