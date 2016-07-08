/**
 * Copyright © 2016 科大讯飞股份有限公司. All rights reserved.
 */
package com.iflytek.documenttransform.tranform;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iflytek.documenttransform.common.TransformException;
import com.iflytek.documenttransform.config.Config;
import com.iflytek.documenttransform.config.Constant;
import com.iflytek.documenttransform.store.FastdfsFileServer;
import com.iflytek.documenttransform.util.FileNameUtils;
import com.iflytek.documenttransform.util.UUIDUtils;

/**
 * @description：
 * 
 * @author suenlai
 * @date 2016年6月17日
 */
public class Mov2FlvTransform implements Transform {
    private static final Logger LOGGER = LoggerFactory.getLogger(Mov2FlvTransform.class);

    /*
     * (non-Javadoc)
     * 
     * @see com.iflytek.documenttransform.tranform.Transform#transform(java.lang.String)
     */
    public String transform(String movFileId, Map<String, Object> parameter) {
        // ffmpeng.exe -i source.mp4 -c:v libx264 -crf 24 destination.flv
        String flvFileId = null;

        File movFileTemp = null;
        File flvFileTemp = null;
        FileInputStream fis = null;
        String movFileName = null;
        try {
            movFileTemp = File.createTempFile(UUIDUtils.getUUID(), ".mov");
            flvFileTemp = File.createTempFile(UUIDUtils.getUUID(), ".flv");
            movFileTemp.deleteOnExit();
            flvFileTemp.deleteOnExit();

            byte[] movFileData = FastdfsFileServer.INSTANCE.get(movFileId);

            movFileName = FastdfsFileServer.INSTANCE.fileMateValue(movFileId,
                    Constant.FASTDFS_FILENAME_KEY);

            FileUtils.writeByteArrayToFile(movFileTemp, movFileData);


            StringBuilder fullCmd = new StringBuilder();
            fullCmd.append(Config.EXEC_FFMPEG);
            fullCmd.append(" -i  ");
            fullCmd.append(movFileTemp.getAbsolutePath());
            fullCmd.append(" -c:v libx264 -crf 24 -f flv");
            fullCmd.append(flvFileTemp.getAbsolutePath());


            LOGGER.info("run sh:\n" + fullCmd + "\n");
            // 执行命令
            Process process = java.lang.Runtime.getRuntime()
                    .exec(new String[] {"sh", "-c", fullCmd.toString()});
            new ProcessStreamPrinter("STDOUT", process.getInputStream(), LOGGER).asyncPrint();
            new ProcessStreamPrinter("STDERROR", process.getErrorStream(), LOGGER).asyncPrint();
            process.waitFor();

            fis = new FileInputStream(flvFileTemp);


            flvFileId = FastdfsFileServer.INSTANCE.saveUploadFile(fis,
                    FileNameUtils.changeFileExt(movFileName, "flv"));
            LOGGER.info("转换MOV[fileId=" + movFileId + "]到FLV[fileId=" + flvFileId + "]成功");
            return flvFileId;
        } catch (IOException e) {
            LOGGER.error("转换MOV[fileId=" + movFileId + "]到FLV[fileId=" + flvFileId + "]失败",e);
            throw new TransformException("转换MOV到FLV失败", e);
        } catch (InterruptedException e) {
            LOGGER.error("转换MOV[fileId=" + movFileId + "]到FLV[fileId=" + flvFileId + "]失败",e);
            throw new TransformException("转换MOV文档失败到flv失败", e);
        } finally {
            IOUtils.closeQuietly(fis);
            FileUtils.deleteQuietly(movFileTemp);
            FileUtils.deleteQuietly(flvFileTemp);
        }
    }

}
