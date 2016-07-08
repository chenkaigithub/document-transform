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
public class Pdf2ImageTransform implements Transform {
    private static final Logger LOGGER       = LoggerFactory.getLogger(Pdf2ImageTransform.class);
    private static final int    DEFAULT_PAGE = 1;



    /*
     * (non-Javadoc)
     * @see com.iflytek.documenttransform.tranform.Transform#transform(java.lang.String)
     */
    public String transform(String pdfFileId, Map<String, Object> parameter) {
        // ffmpeng.exe -i source.mp4 -c:v libx264 -crf 24 destination.flv
        String jpegFileId = null;

        File pdfFileTemp = null;
        File jpegFileTemp = null;
        File tempDir = null;
        FileInputStream fis = null;
        String flvFileName = null;
        try {
            pdfFileTemp = File.createTempFile(UUIDUtils.getUUID(), ".pdf");
            tempDir = new File(System.getProperty("java.io.tmpdir") + File.separatorChar
                    + UUIDUtils.getUUID() + File.separatorChar);
            tempDir.mkdirs();
            pdfFileTemp.deleteOnExit();

            byte[] flvFileData = FastdfsFileServer.INSTANCE.get(pdfFileId);

            flvFileName = FastdfsFileServer.INSTANCE.fileMateValue(pdfFileId,
                    Constant.FASTDFS_FILENAME_KEY);

            FileUtils.writeByteArrayToFile(pdfFileTemp, flvFileData);

            int page =
                    parameter.get("page") == null ? DEFAULT_PAGE : (Integer) parameter.get("page");


            StringBuilder fullCmd = new StringBuilder();
            fullCmd.append(Config.SWFTOOLS_EXEC_PDF2JPEG);
            fullCmd.append("\t");
            fullCmd.append(pdfFileTemp.getAbsolutePath());
            fullCmd.append("\t");
            fullCmd.append(tempDir.getAbsolutePath() + File.separatorChar);
            fullCmd.append("\t");
            fullCmd.append(" -j ");
            fullCmd.append(" -f ");
            fullCmd.append(page);
            fullCmd.append(" -l ");
            fullCmd.append(page);



            LOGGER.info("run sh:\n" + fullCmd + "\n");
            // 执行命令
            Process process = java.lang.Runtime.getRuntime()
                    .exec(new String[] {"sh", "-c", fullCmd.toString()});
            new ProcessStreamPrinter("STDOUT", process.getInputStream(), LOGGER).asyncPrint();
            new ProcessStreamPrinter("STDERROR", process.getErrorStream(), LOGGER).asyncPrint();
            process.waitFor();

            jpegFileTemp = new File(System.getProperty("java.io.tmpdir"), "-0000.jpg");


            fis = new FileInputStream(jpegFileTemp);


            jpegFileId = FastdfsFileServer.INSTANCE.saveUploadFile(fis,
                    FileNameUtils.changeFileExt(flvFileName, "jpeg"));
            LOGGER.info("转换PDF[fileId=" + pdfFileId + "]到JPEG[fileId=" + jpegFileId + "]成功");
            return jpegFileId;
        } catch (IOException e) {
            LOGGER.error("转换PDF[fileId=" + pdfFileId + "]到JPEG[fileId=" + jpegFileId + "]失败", e);
            throw new TransformException("转换PDF到JPEG失败", e);
        } catch (InterruptedException e) {
            LOGGER.error("转换PDF[fileId=" + pdfFileId + "]到JPEG[fileId=" + jpegFileId + "]失败", e);
            throw new TransformException("转换PDF文档到JPEG失败", e);
        } finally {
            IOUtils.closeQuietly(fis);
            FileUtils.deleteQuietly(pdfFileTemp);
            FileUtils.deleteQuietly(jpegFileTemp);
        }
    }

}
