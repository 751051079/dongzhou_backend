package com.smarterp.common.core.utils.mercado;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.smarterp.common.core.utils.StringUtils;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonUtilString {

    public static final String EMPTY = "";
    /**
     * 替换所有空格，留下一个
     */
    private static final String REPLACE_BLANK_ENTER = "\\s{2,}|\t|\r|\n";
    private static final Pattern REPLACE_P = Pattern.compile(REPLACE_BLANK_ENTER);

    /**
     * 使用正则表达式删除字符串中的空格、回车、换行符、制表符
     *
     * @param str
     * @return
     */
    public static String replaceAllBlank(String str) {
        String dest = "";
        if (StringUtils.isNotBlank(str)) {
            Matcher m = REPLACE_P.matcher(str);
            dest = m.replaceAll("");
        }
        dest = dest.replace(" \"", "\"")
                .replace("\" ", "\"")
                .replace(" :", ":")
                .replace(": ", ":")
                .replace(" {", "{")
                .replace("{ ", "{")
                .replace(" }", "}")
                .replace("} ", "}")
                .replace(" [", "[")
                .replace("[ ", "[")
                .replace(" ]", "]")
                .replace("] ", "]");
        return toJsonString(dest);
    }

    //去双引号
    private static String toJsonString(String s) {
        char[] temp = s.toCharArray();
        int n = temp.length;
        for (int i = 0; i < n; i++) {
            if (temp[i] == ':' && temp[i + 1] == '"') {
                for (int j = i + 2; j < n; j++) {
                    if (temp[j] == '"') {
                        if (temp[j + 1] != ',' && temp[j + 1] != '}') {
                            temp[j] = '”';
                        } else if (temp[j + 1] == ',' || temp[j + 1] == '}') {
                            break;
                        }
                    }
                }
            }
        }
        return new String(temp);
    }

    /**
     * 去除字符串中的空格、回车、换行符、制表符
     * \n 回车(\u000a)
     * \t 水平制表符(\u0009)
     * \s 空格(\u0008)
     * \r 换行(\u000d)
     *
     * @param source
     * @return
     */
    public static String replaceBlank(String source) {
        String ret = EMPTY;
        if (StringUtils.isNotBlank(source)) {
            ret = source.replaceAll(StringUtils.LF, EMPTY)
                    .replaceAll("\\s{2,}", EMPTY)
                    .replaceAll("\\t", EMPTY)
                    .replaceAll(StringUtils.CR, EMPTY);
        }
        return ret;
    }

    /**
     * 使用fastjson JSONObject格式化输出JSON字符串
     *
     * @param source
     * @return
     */
//    public static String formatJson(String source) {
//        JSONObject object = JSONObject.parseObject(source);
//        String pretty = JSON.toJSONString(object, SerializerFeature.PrettyFormat,
//                SerializerFeature.WriteMapNullValue,
//                SerializerFeature.WriteDateUseDateFormat);
//        return pretty;
//    }
//
//    public static String formatJsonOneRow(String source) {
//        JSONObject object = JSONObject.parseObject(source);
//        String pretty = JSON.toJSONString(object, SerializerFeature.PrettyFormat,
//                SerializerFeature.WriteMapNullValue,
//                SerializerFeature.WriteDateUseDateFormat);
//        return pretty;
//    }

//    public static void main(String[] args) {
//        String source = "{\"name\":\"JSON String\",\"age\":\"29\",\"address\":\"河南省 商丘师范学院，坐标: x, y\",\"addressTest\":\"无对应属性，不转换\"}";
//        System.out.println("-------formatJson---------");
//        System.out.println(formatJson(source));
//        System.out.println("-------replaceBlank---------");
//        System.out.println(replaceBlank(source));
//        System.out.println("-------replaceAllBlank---------");
//        System.out.println(replaceAllBlank(source));
//    }

}
