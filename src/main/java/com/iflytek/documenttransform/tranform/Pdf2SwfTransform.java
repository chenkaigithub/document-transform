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

public class Pdf2SwfTransform implements Transform {
    private static final Logger LOGGER = LoggerFactory.getLogger(Pdf2SwfTransform.class);

    public String transform(String pdfFileId, Map<String, Object> parameter) {
        String swfFileId = null;
        File pdfFileTemp = null;
        File swfFileTemp = null;
        FileInputStream fis = null;
        String pdfFileName = null;
        try {
            pdfFileTemp = File.createTempFile(UUIDUtils.getUUID(), ".pdf");
            swfFileTemp = File.createTempFile(UUIDUtils.getUUID(), ".swf");
            pdfFileTemp.deleteOnExit();
            swfFileTemp.deleteOnExit();

            byte[] pdfFileData = FastdfsFileServer.INSTANCE.get(pdfFileId);

            pdfFileName = FastdfsFileServer.INSTANCE.fileMateValue(pdfFileId,
                    Constant.FASTDFS_FILENAME_KEY);

            FileUtils.writeByteArrayToFile(pdfFileTemp, pdfFileData);


            StringBuilder fullCmd = new StringBuilder();

            fullCmd.append(Config.SWFTOOLS_EXEC_PDF2SWF);
            fullCmd.append(" -T 9 ");

            fullCmd.append(pdfFileTemp.getAbsolutePath());
            fullCmd.append("\t");
            fullCmd.append("-o ");
            fullCmd.append(swfFileTemp.getAbsolutePath());


            LOGGER.info("run sh:\n" + fullCmd + "\n");
            // 执行命令
            Process process = java.lang.Runtime.getRuntime()
                    .exec(new String[] {"sh", "-c", fullCmd.toString()});
            new ProcessStreamPrinter("STDOUT", process.getInputStream(), LOGGER).asyncPrint();
            new ProcessStreamPrinter("STDERROR", process.getErrorStream(), LOGGER).asyncPrint();
            process.waitFor();

            fis = new FileInputStream(swfFileTemp);


            swfFileId = FastdfsFileServer.INSTANCE.saveUploadFile(fis,
                    FileNameUtils.changeFileExt(pdfFileName, "swf"));
            LOGGER.info("转换PDF[fileId=" + pdfFileId + "]到SWF[fileId=" + swfFileId + "]成功");
            return swfFileId;
        } catch (IOException e) {
            LOGGER.error("转换PDF[fileId=" + pdfFileId + "]到SWF[fileId=" + swfFileId + "]失败", e);
            throw new TransformException("转换PDF到SWF失败", e);
        } catch (InterruptedException e) {
            LOGGER.error("转换PDF[fileId=" + pdfFileId + "]到SWF[fileId=" + swfFileId + "]失败", e);
            throw new TransformException("转换PDF文档失败到SWF失败", e);
        } finally {
            IOUtils.closeQuietly(fis);
            FileUtils.deleteQuietly(pdfFileTemp);
            FileUtils.deleteQuietly(swfFileTemp);
        }
    }

}
