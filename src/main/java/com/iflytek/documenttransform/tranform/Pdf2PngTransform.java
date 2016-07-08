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
public class Pdf2PngTransform implements Transform {
    private static final Logger LOGGER       = LoggerFactory.getLogger(Pdf2PngTransform.class);
    private static final int    DEFAULT_PAGE = 1;



    /*
     * (non-Javadoc)
     * 
     * @see com.iflytek.documenttransform.tranform.Transform#transform(java.lang.String)
     */
    public String transform(String pdfFileId, Map<String, Object> parameter) {
        // ffmpeng.exe -i source.mp4 -c:v libx264 -crf 24 destination.flv
        String pngFileId = null;

        File pdfFileTemp = null;
        File pngFileTemp = null;
        File tempDir = null;
        FileInputStream fis = null;
        String pdfFileName = null;
        try {
            pdfFileTemp = File.createTempFile(UUIDUtils.getUUID(), ".pdf");
            tempDir = new File(System
                    .getProperty("java.io.tmpdir") + File.separatorChar + UUIDUtils.getUUID()+File.separatorChar);
            tempDir.mkdirs();
            pdfFileTemp.deleteOnExit();

            byte[] pdfFileData = FastdfsFileServer.INSTANCE.get(pdfFileId);

            pdfFileName = FastdfsFileServer.INSTANCE.fileMateValue(pdfFileId,
                    Constant.FASTDFS_FILENAME_KEY);

            FileUtils.writeByteArrayToFile(pdfFileTemp, pdfFileData);

            int page =
                    parameter.get("page") == null ? DEFAULT_PAGE : (Integer) parameter.get("page");


            StringBuilder fullCmd = new StringBuilder();
            fullCmd.append(Config.SWFTOOLS_EXEC_PDF2PNG);
            fullCmd.append("\t");
            fullCmd.append(pdfFileTemp.getAbsolutePath());
            fullCmd.append("\t");
            fullCmd.append(tempDir.getAbsolutePath()+File.separatorChar);
            fullCmd.append("\t");
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

            pngFileTemp = new File(tempDir, String.format("-%06d.png", page));


            fis = new FileInputStream(pngFileTemp);


            pngFileId = FastdfsFileServer.INSTANCE.saveUploadFile(fis,
                    FileNameUtils.changeFileExt(pdfFileName, "png"));

            LOGGER.info("转换PDF[fileId=" + pdfFileId + "]到PNG[fileId=" + pngFileId + "]成功");
            return pngFileId;
        } catch (IOException e) {
            LOGGER.error("转换PDF[fileId=" + pdfFileId + "]到PNG[fileId=" + pngFileId + "]失败", e);
            throw new TransformException("转换PDF到PNG失败", e);
        } catch (InterruptedException e) {
            LOGGER.error("转换PDF[fileId=" + pdfFileId + "]到PNG[fileId=" + pngFileId + "]失败", e);
            throw new TransformException("转换PDF文档到PNG失败", e);
        } finally {
            IOUtils.closeQuietly(fis);
            FileUtils.deleteQuietly(pdfFileTemp);
            FileUtils.deleteQuietly(tempDir);

        }
    }

}
