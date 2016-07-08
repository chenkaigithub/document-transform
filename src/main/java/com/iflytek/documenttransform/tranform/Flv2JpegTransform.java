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
import org.apache.commons.lang3.StringUtils;
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
public class Flv2JpegTransform implements Transform {
    private static final Logger LOGGER            =
            LoggerFactory.getLogger(Flv2JpegTransform.class);
    private static final String DEFAULT_TIMESTAMP = "00:00:05";



    /*
     * (non-Javadoc)
     * @see com.iflytek.documenttransform.tranform.Transform#transform(java.lang.String)
     */
    public String transform(String flvFileId, Map<String, Object> parameter) {
        // ffmpeng.exe -i source.mp4 -c:v libx264 -crf 24 destination.flv
        String jpegFileId = null;

        File flvFileTemp = null;
        File jpegFileTemp = null;
        FileInputStream fis = null;
        String flvFileName = null;
        try {
            flvFileTemp = File.createTempFile(UUIDUtils.getUUID(), ".flv");
            jpegFileTemp = File.createTempFile(UUIDUtils.getUUID(), ".jpeg");
            flvFileTemp.deleteOnExit();
            jpegFileTemp.deleteOnExit();

            byte[] flvFileData = FastdfsFileServer.INSTANCE.get(flvFileId);

            flvFileName = FastdfsFileServer.INSTANCE.fileMateValue(flvFileId,
                    Constant.FASTDFS_FILENAME_KEY);

            FileUtils.writeByteArrayToFile(flvFileTemp, flvFileData);

            String timestamp = parameter.get("timestamp") == null ? null
                    : parameter.get("timestamp").toString();
            if (StringUtils.isEmpty(timestamp)) {
                timestamp = DEFAULT_TIMESTAMP;
            }

            StringBuilder fullCmd = new StringBuilder();
            // /Users/suenlai/Downloads/ffmpeg -i mp42.flv -f image2 -ss 3 -t 1 test.jpeg
            // -r 1 每秒一张
            // -t 截图1移
            // -ss 截图开始时间
            fullCmd.append(Config.EXEC_FFMPEG);
            fullCmd.append("  -i  ");
            fullCmd.append(flvFileTemp.getAbsolutePath());

            fullCmd.append(" -f image2 -ss ");
            fullCmd.append(timestamp);
            fullCmd.append(" -t 1 -r 1 ");
            fullCmd.append(jpegFileTemp.getAbsolutePath());
            FileUtils.deleteQuietly(jpegFileTemp);

            LOGGER.info("run sh:\n" + fullCmd + "\n");


            // 执行命令
            Process process = java.lang.Runtime.getRuntime()
                    .exec(new String[] {"sh", "-c", fullCmd.toString()});
            new ProcessStreamPrinter("STDOUT", process.getInputStream(), LOGGER).asyncPrint();
            new ProcessStreamPrinter("STDERROR", process.getErrorStream(), LOGGER).asyncPrint();
            process.waitFor();

            fis = new FileInputStream(jpegFileTemp);


            jpegFileId = FastdfsFileServer.INSTANCE.saveUploadFile(fis,
                    FileNameUtils.changeFileExt(flvFileName, "jpeg"));
            LOGGER.info("转换FLV[fileId=" + flvFileId + "]到JPEG[fileId=" + jpegFileId + "]成功");
            return jpegFileId;
        } catch (IOException e) {
            LOGGER.error("转换FLV[fileId=" + flvFileId + "]到JPEG[fileId=" + jpegFileId
                    + "]失败,请查看是否因为截图时间戳超过此视频的时长", e);
            throw new TransformException("转换FLV到JPEG失败", e);
        } catch (InterruptedException e) {
            LOGGER.error("转换FLV[fileId=" + flvFileId + "]到JPEG[fileId=" + jpegFileId + "]失败", e);
            throw new TransformException("转换FLV文档失败到JPEG失败", e);
        } finally {
            IOUtils.closeQuietly(fis);
            FileUtils.deleteQuietly(flvFileTemp);
            FileUtils.deleteQuietly(jpegFileTemp);
        }
    }

}
