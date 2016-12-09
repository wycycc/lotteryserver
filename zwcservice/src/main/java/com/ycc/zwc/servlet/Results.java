package com.ycc.zwc.servlet;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/9.
 */
public class Results {
    /*************** 彩种信息 ****************/

    public static final String SSQ = "118";
    public static final String FC3D = "116";
    public static final String QLC = "117";

    public static Map<String, String> lotteryMap = new HashMap<String, String>();

    static {
        lotteryMap.put(SSQ, "双色球|2,4,7|21:30");
        lotteryMap.put(FC3D, "福彩3D||20:30");
        lotteryMap.put(QLC, "七乐彩|1,3,5|21:30");
    }

    public static final String RES_HEADER = "<header><messengerid>20091113101533000001</messengerid><timestamp>20091113101533</timestamp><transactiontype>T%</transactiontype><digest>D%</digest><compress>DES</compress><agenterid>1000002</agenterid></header>";

    /**
     * 返回信息body
     */
    public static final String RES_BODY = "<oelement><errorcode>0</errorcode><errormsg>操作成功</errormsg></oelement>E%";
    /**
     * 玩法可销售期信息
     */
    public static final String RES_SALES_INFORMATION = "<elements><element><lotteryid>LID%</lotteryid><lotteryname>LN%</lotteryname><issue>I%</issue><lasttime>T%</lasttime></element></elements>";

    /**
     * 返回账户详情
     */
    public static final String RES_USER_ACCOUNT_DETAIL = "<elements><element><accountname>13200000000</accountname><accountid>10000000</accountid><accounvalues>100</accounvalues><investvalues>100</investvalues><cashvalues>100</cashvalues></element></elements>";
    /**
     * 用户注册
     */
    public static final String RES_USER_REGISTE = "<elements><element><actvalue>0</actvalue></element></elements>";

    /**
     * 返回投注
     *
     * serialid tradevalue actvalue
     */
    public static final String RES_USER_BETTING = "<elements><element><serialid>000000001</serialid><tradevalue>10</tradevalue><actvalue>10</actvalue></element></elements>";

}