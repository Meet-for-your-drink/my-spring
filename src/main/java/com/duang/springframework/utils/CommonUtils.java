package com.duang.springframework.utils;

/**
 * @author duang
 * @date 2021-04-18
 * @Describe
 */
public class CommonUtils {
    public static String lowHead(String str){
        char[] strArr = str.toCharArray();
        strArr[0] += 32;
        return String.valueOf(strArr);
    }
}
