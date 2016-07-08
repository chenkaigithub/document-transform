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

public class Mp42FlvFromHttpTest {
    String fileName =
            "双语全程特朗普外交政策演讲 标清 (Donald Trump Speaks on Foreign Policy in Washington, DC).mp4";
    String filePath =
            "/Users/suenlai/Downloads/双语全程特朗普外交政策演讲 标清 (Donald Trump Speaks on Foreign Policy in Washington, DC).mp4";

    @Test
    public void testTransformFromHttp() throws IOException, URISyntaxException {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1; i++) {
            FileInputStream fis = new FileInputStream(filePath);
            String mp4FileId = FastdfsFileServer.INSTANCE.saveUploadFile(fis, fileName);

            URL url =
                    new URL("http://172.16.16.141:8081/transformation/mp42flv?fileId=" + mp4FileId);
            // URL url = new URL("http://127.0.0.1:8081/transformation/mp42flv?fileId=" +
            // mp4FileId);
            String response = IOUtils.toString(url.openStream(), Charset.forName("utf-8"));
            System.out.println(response);
            JSONObject json = JSONObject.parseObject(response);

            String flvFileId = json.getString("data");
            System.out.println(flvFileId);

            String flvFileName = FastdfsFileServer.INSTANCE.fileMateValue(flvFileId,
                    Constant.FASTDFS_FILENAME_KEY);
            byte[] data = FastdfsFileServer.INSTANCE.get(flvFileId);
            FileUtils.writeByteArrayToFile(
                    new File("/Users/suenlai/Desktop/", "测试PDF" + i + ".flv"), data);
            Assert.assertEquals(FileNameUtils.getBaseName(flvFileName),
                    FileNameUtils.getBaseName(fileName));


            FastdfsFileServer.INSTANCE.remove(mp4FileId);
            FastdfsFileServer.INSTANCE.remove(flvFileId);
        }
        long spendTime = System.currentTimeMillis() - start;
        System.out.println("sum spend" + spendTime / 1000.0 + " second in this testcase");
    }
}
