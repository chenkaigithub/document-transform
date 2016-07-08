package com.iflytek.documenttransform.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.alibaba.fastjson.JSONObject;
import com.iflytek.documenttransform.store.FastdfsFileServer;

public class Pdf2SwfFromHttpTest {
    String fileName = "zookeeper.pdf";
    String filePath = "/Users/suenlai/Desktop/zookeeper.pdf";

    @Test
    public void testTransformFromHttp() throws IOException, URISyntaxException {
        FileInputStream fis = new FileInputStream(filePath);
        String pdfFileId = FastdfsFileServer.INSTANCE.saveUploadFile(fis, fileName);

        URL url = new URL("http://127.0.0.1:8080/transform/pdf2swf?fileId=" + pdfFileId);
        String response = IOUtils.toString(url.openStream(), Charset.forName("utf-8"));
        System.out.println(response);
        JSONObject json = JSONObject.parseObject(response);

        String swfFileId = json.getString("data");
        System.out.println(swfFileId);

        FastdfsFileServer.INSTANCE.remove(swfFileId);
        FastdfsFileServer.INSTANCE.remove(pdfFileId);

    }
}
