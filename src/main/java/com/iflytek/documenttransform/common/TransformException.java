/**
 * Copyright © 2016 科大讯飞股份有限公司. All rights reserved.
 */
package com.iflytek.documenttransform.common;

/**
 * @description：
 *
 * @author suenlai
 * @date 2016年6月14日
 */
public class TransformException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -2130130338705067153L;

    public TransformException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransformException(String message) {
        super(message);
    }

    public TransformException(Throwable cause) {
        super(cause);
    }


}
