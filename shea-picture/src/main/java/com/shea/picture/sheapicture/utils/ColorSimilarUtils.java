package com.shea.picture.sheapicture.utils;


import java.awt.*;

/**
 * 颜色相似度计算工具类
 * @author : Shea.
 * @since : 2026/4/22 20:39
 */
public class ColorSimilarUtils {

    private ColorSimilarUtils() {
    }

    public static double getSimilarity(Color c1, Color c2) {
        int red = c1.getRed();
        int green = c1.getGreen();
        int blue = c1.getBlue();
        int red2 = c2.getRed();
        int green2 = c2.getGreen();
        int blue2 = c2.getBlue();
        double dist = Math.sqrt(Math.pow(red - red2, 2) + Math.pow(green - green2, 2) + Math.pow(blue - blue2, 2));
        return 1 - dist / Math.sqrt(3 * Math.pow(255, 2));
    }

    public static double getSimilarity(String hex1, String hex2) {
        Color c1 = Color.decode(hex1);
        Color c2 = Color.decode(hex2);
        return getSimilarity(c1, c2);
    }
}
