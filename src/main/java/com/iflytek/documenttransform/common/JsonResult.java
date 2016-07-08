/**
 * Copyright © 2016 科大讯飞股份有限公司. All rights reserved.
 */
package com.iflytek.documenttransform.common;

import com.alibaba.fastjson.JSON;
import com.iflytek.documenttransform.config.Constant;

/**
 * @description：
 *
 * @author suenlai
 * @date 2016年6月14日
 */
public class JsonResult {
    /**
     * 成功为1
     */
    private byte   success = 1;
    private String msg;
    private Object data;


    public static JsonResult SuccessJsonResult(Object data) {
        return new JsonResult(Constant.JSON_RESULT_STATUS_SUCCESS, Constant.JSON_RESULT_MSG_SUCCESS,
                data);
    }

    public static JsonResult FailureJsonResult(String errorMsg, Object data) {
        return new JsonResult(Constant.JSON_RESULT_STATUS_FAILURE, errorMsg, data);
    }

    public static JsonResult FailureJsonResult(String errorMsg) {
        return new JsonResult(Constant.JSON_RESULT_STATUS_FAILURE, errorMsg, null);
    }

    private JsonResult(byte success, String msg, Object data) {
        super();
        this.success = success;
        this.msg = msg;
        this.data = data;
    }

    public static void main(String[] args) {
        System.out.println(JSON.toJSONString(JsonResult.SuccessJsonResult("2222")));
    }

    public byte getSuccess() {
        return success;
    }

    public String getMsg() {
        return msg;
    }

    public Object getData() {
        return data;
    }

}
