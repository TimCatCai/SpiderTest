package timcat;

import org.apache.commons.lang3.StringUtils;

public class DataUtils {
    /**
     * 格式化Value的一条记录，格式为：（params[0],params[1],...params[params.length])
     * @param params 将要格式化的所有参数
     * @return 返回格式化过你的字符串
     */
    public static String formatOneTuple(String ... params){
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        for(int i = 0; i < params.length; i++){
            builder.append(params[i]);
            if(i != params.length - 1){
                builder.append(",");
            }
        }
        builder.append(")");
        return builder.toString();
    }

    public static String stringType(String data){
        return "'" + data + "'";
    }

    public static String dateFormat(int year, int month, int day){
        return year + "-" + addLeadingZero(month, "00") + "-" + addLeadingZero(day,"00");
    }

    public static String addLeadingZero(int data, String pattern){
        String result = String.valueOf(data);
        result = StringUtils.substring(pattern,0, pattern.length() - result.length()) + result;
        return result;
    }

    public static String [] getNameListFromJsonFile(String jsonString){
        String trimBrace = StringUtils.trim(jsonString);
        trimBrace = StringUtils.substring(trimBrace ,1, trimBrace.length() - 1);
        // 只要到达两个即可，减少时间损耗
        String [] jsonList = StringUtils.split(trimBrace,":", 2);
        String nameListString = null;
        if(jsonList.length >= 2){
            nameListString = jsonList[1];
            // 去掉双引号及最后的逗号
            nameListString = StringUtils.substring(nameListString, 1, nameListString.length() - 2);
        }
        String [] nameList = StringUtils.split(nameListString, ",");
        return nameList;
    }

    public static String convertSexCodeToSexString(int sexCode){
        String sexString;
        switch (sexCode){
            case 0:
                sexString = "男";
                break;
            case 1:
                sexString = "女";
                break;
            default:
                sexString = "Unknown";
        }
        return sexString;
    }


    public static String insertHead(String table){
        return "INSERT INTO "+ table +" VALUES";
    }
}
