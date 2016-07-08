/***
 *
 */
package com.iflytek.documenttransform.store;

import java.io.IOException;
import java.io.InputStream;

/**
 * 文件存储读取查询
 *
 * @date 2016年6月8日 上午11:51:40
 * @author suenlai
 *
 */
public interface FileApi {
    /**
     *
     * @Description: 保存文件
     * @param data 文件字节数据
     * @param filename 带后缀名的文件名
     * @return 文件唯一标识(fileId)
     * @throws IOException
     */
    String saveFile(byte[] data, String filename) throws IOException;

    /**
     *
     * 保存输入流
     *
     * @param inputSteam 读取字节流
     * @param filename 带后缀名的文件名
     * @return 文件唯一标识,又称fileId
     * @throws Exception
     */
    String saveUploadFile(InputStream inputSteam, String filename) throws IOException;

    /**
     * 根据fileId获取字节数组
     *
     * @param fileId
     * @return
     * @throws Exception
     */
    byte[] get(String fileId) throws IOException;

    /**
     *
     * @Description: 获取fileId所指定的文件中的元信息中的metaKey所指的值
     * @param fileId
     * @param metaKey
     * @return metaKey所指的值
     */
    String fileMateValue(String fileId, String metaKey);

    /**
     * 根据fileId删除文件
     *
     * @param fileId
     * @throws Exception
     */
    void remove(String fileId) throws IOException;

}
