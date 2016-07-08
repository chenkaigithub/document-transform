package com.iflytek.documenttransform.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import com.alibaba.fastjson.JSONObject;
import com.iflytek.documenttransform.config.Constant;
import com.iflytek.documenttransform.store.FastdfsFileServer;

public class Office2PdfFromHttpTest {
    String fileName = "甘肃省智慧教育统一认证平台建设方案V0.2.docx";
    String filePath = "/Users/suenlai/Desktop/甘肃省智慧教育统一认证平台建设方案V0.2.docx";

    @Test
    public void testTransformFromHttp() throws IOException, URISyntaxException {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1; i++) {
            FileInputStream fis = new FileInputStream(filePath);
            String officeFileId = FastdfsFileServer.INSTANCE.saveUploadFile(fis, fileName);

            URL url = new URL("http://172.16.16.141:8081/transformation/office2pdf?fileId=" + officeFileId);
            String response = IOUtils.toString(url.openStream(), Charset.forName("utf-8"));
            System.out.println(response);
            JSONObject json = JSONObject.parseObject(response);

            String pdfFileId = json.getString("data");
            System.out.println(pdfFileId);

            String pdfFileName = FastdfsFileServer.INSTANCE.fileMateValue(pdfFileId,
                    Constant.FASTDFS_FILENAME_KEY);
            byte[] data = FastdfsFileServer.INSTANCE.get(pdfFileId);
            FileUtils.writeByteArrayToFile(
                    new File("/Users/suenlai/Desktop/", "测试PDF" + i + ".pdf"), data);
            Assert.assertEquals(FileNameUtils.getBaseName(pdfFileName),
                    FileNameUtils.getBaseName(fileName));


           // FastdfsFileServer.INSTANCE.remove(officeFileId);
            //FastdfsFileServer.INSTANCE.remove(pdfFileId);
        }
        long spendTime = System.currentTimeMillis() - start;
        System.out.println("sum spend" + spendTime / 1000.0 + " second in this testcase");
    }
}
