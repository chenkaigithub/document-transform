package com.iflytek.documenttransform.tranform;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;
import com.iflytek.documenttransform.common.TransformException;
import com.iflytek.documenttransform.config.Constant;
import com.iflytek.documenttransform.store.FastdfsFileServer;
import com.iflytek.documenttransform.util.FileNameUtils;
import com.iflytek.documenttransform.util.UUIDUtils;

public class Office2PdfTransform implements Transform {
    private static final Logger LOGGER = LoggerFactory.getLogger(Office2PdfTransform.class);

    public String transform(String officeFileId, Map<String, Object> parameter) {
        String pdfFileId = null;
        File officeFileTemp = null;
        File pdfFileTemp = null;
        FileInputStream fis = null;
        OpenOfficeConnection connection = null;
        try {
            byte[] officeFileData = FastdfsFileServer.INSTANCE.get(officeFileId);
            String officeFileName = FastdfsFileServer.INSTANCE.fileMateValue(officeFileId,
                    Constant.FASTDFS_FILENAME_KEY);
            officeFileTemp = File.createTempFile(UUIDUtils.getUUID(),
                    "." + FileNameUtils.getFileExt(officeFileName));
            pdfFileTemp = File.createTempFile(UUIDUtils.getUUID(), ".pdf");
            officeFileTemp.deleteOnExit();
            pdfFileTemp.deleteOnExit();


            FileUtils.writeByteArrayToFile(officeFileTemp, officeFileData);

            connection = OpenOfficeConnectionPool.INSTANCE.borrowObject();

            // convert
            DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
            converter.convert(officeFileTemp, pdfFileTemp);

            fis = new FileInputStream(pdfFileTemp);

            pdfFileId = FastdfsFileServer.INSTANCE.saveUploadFile(fis,
                    FileNameUtils.changeFileExt(officeFileName, "pdf"));
            LOGGER.info("转换office[fileId=" + officeFileId + "]到PDF[fileId=" + pdfFileId + "]成功");
            return pdfFileId;
        } catch (Exception e) {
            LOGGER.error("转换office[fileId=" + officeFileId + "]到PDF[fileId=" + pdfFileId + "]失败",
                    e);
            throw new TransformException("转换office文档失败到PDF失败", e);
        } finally {
            OpenOfficeConnectionPool.INSTANCE.returnObject(connection);
            IOUtils.closeQuietly(fis);
            FileUtils.deleteQuietly(officeFileTemp);
            FileUtils.deleteQuietly(pdfFileTemp);
        }
    }

}
