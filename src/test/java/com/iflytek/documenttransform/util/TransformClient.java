/**
 * Copyright © 2016 科大讯飞股份有限公司. All rights reserved.
 */
package com.iflytek.documenttransform.util;

import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.iflytek.documenttransform.store.FastdfsFileServer;

/**
 * @description：文档转换服务JAVA客户端
 * @author suenlai
 * @see <code>com.iflytek.documenttransform.TransformServer</code>
 * @date 2016年6月17日
 */
public class TransformClient {
    /**
     * 
     * @see <code>com.iflytek.documenttransform.TransformServer</code>
     */
    private String transformServerHost = "127.0.0.1:8080";

    /**
     * 
     * @Description: 生成文件的图片快照
     * @param fileId 目前只只持pdf和flv的后缀名的文件
     * @return 可以预览的格式文件存储在fastdfs中的文件id
     */
    public String generateSnapshotImage(String fileId) {
        String fileName = FastdfsFileServer.INSTANCE.fileMateValue(fileId, "fileName");
        String extension = FilenameUtils.getExtension(fileName).toLowerCase();
        if (StringUtils.isEmpty(extension)) {
            throw new RuntimeException(fileId + "指定的文件元信息不含带后缀名的文件名");
        }

        String snapshotFileId = null;
        switch (extension) {
            case "pdf":
                snapshotFileId = snapshot4Pdf(fileId, 1);
                break;
            case "flv":
                snapshotFileId = snapshot4Flv(fileId, "00:00:04");
                break;
            default:
                throw new RuntimeException("暂不支持当前格式(" + extension + ")的快照格式抓取格");
        }
        return snapshotFileId;

    }

    /**
     * 
     * @Description: 根据相应的后缀名转换成相应的可以预览的格式文件,
     * @param fileId 待转换的文件ID
     * @return 可以预览的格式文件存储在fastdfs中的文件id
     */
    public String generatePreviewFile(String fileId) {
        String fileName = FastdfsFileServer.INSTANCE.fileMateValue(fileId, "fileName");
        String extension = FilenameUtils.getExtension(fileName).toLowerCase();
        if (StringUtils.isEmpty(extension)) {
            throw new RuntimeException("当前文件元信息中不存在带后缀名的文件名");
        }
        String previewFileId = null;
        switch (extension) {
            case "docx":
                previewFileId = office2Pdf(fileId, false);
                break;
            case "doc":
                previewFileId = office2Pdf(fileId, false);
                break;
            case "xls":
                previewFileId = office2Pdf(fileId, false);
                break;
            case "xlsx":
                previewFileId = office2Pdf(fileId, false);
                break;
            case "ppt":
                previewFileId = office2Pdf(fileId, false);
                break;
            case "pptx":
                previewFileId = office2Pdf(fileId, false);
                break;
            case "pdf":
                previewFileId = pdf2Swf(fileId, false);
                break;
            case "mp4":
                previewFileId = mp42flv(fileId, false);
                break;
            case "mov":
                previewFileId = mov2flv(fileId, false);
            default:
                throw new RuntimeException("暂不支持当前格式(" + extension + ")的预览格式转换,");
        }
        return previewFileId;
    }

    private String office2Pdf(String officeFileId, boolean deleteSourceFile) {
        String pdfFileId = null;
        try {
            URL url = new URL(
                    this.transformServerHost + "/transform/office2pdf?fileId=" + officeFileId);
            String response = IOUtils.toString(url.openStream(), "UTF-8");
            JSONObject json = JSONObject.parseObject(response);
            pdfFileId = json.getString("data");
            return pdfFileId;
        } catch (IOException ex) {
            throw new RuntimeException(
                    "请求转换服务失败,server:" + this.transformServerHost + ",officeFileId:" + officeFileId,
                    ex);
        }
    }

    private String pdf2Swf(String pdfFileId, boolean deleteSourceFile) {
        String swfFileId = null;
        try {
            URL url = new URL(this.transformServerHost + "/transform/pdf2swf?fileId=" + pdfFileId);
            String response = IOUtils.toString(url.openStream(), "UTF-8");
            JSONObject json = JSONObject.parseObject(response);
            swfFileId = json.getString("data");
            return swfFileId;
        } catch (IOException ex) {
            throw new RuntimeException(
                    "请求转换服务失败,server:" + this.transformServerHost + ",pdfFileId:" + pdfFileId, ex);
        }
    }

    private String mp42flv(String mp4FileId, boolean deleteSourceFile) {
        String flvFileId = null;
        try {
            URL url = new URL(this.transformServerHost + "/transform/mp42flv?fileId=" + mp4FileId);
            String response = IOUtils.toString(url.openStream(), "UTF-8");
            JSONObject json = JSONObject.parseObject(response);
            flvFileId = json.getString("data");
            if (deleteSourceFile) {
                deleteSourceFile(mp4FileId);
            }
            return flvFileId;
        } catch (IOException ex) {
            throw new RuntimeException(
                    "请求转换服务失败,server:" + this.transformServerHost + ",mp4FileId:" + mp4FileId, ex);
        }
    }

    private String mov2flv(String movFileId, boolean deleteSourceFile) {
        String flvFileId = null;
        try {
            URL url = new URL(this.transformServerHost + "/transform/mov2flv?fileId=" + movFileId);
            String response = IOUtils.toString(url.openStream(), "UTF-8");
            JSONObject json = JSONObject.parseObject(response);
            flvFileId = json.getString("data");
            if (deleteSourceFile) {
                deleteSourceFile(movFileId);
            }
            return flvFileId;
        } catch (IOException ex) {
            throw new RuntimeException(
                    "请求转换服务失败,server:" + this.transformServerHost + ",movFileId:" + movFileId, ex);
        }
    }

    /**
     * 
     * @Description:
     * @param flvFileId
     * @param timestamp 格式为 hh:mm:ss 分别为时分秒
     * @return
     */
    private String snapshot4Flv(String flvFileId, String timestamp) {
        String snapshotFileId = null;
        try {
            URL url = new URL(this.transformServerHost + "/transform/snap4flv?fileId=" + flvFileId
                    + "&timestamp=" + timestamp);
            String response = IOUtils.toString(url.openStream(), "UTF-8");
            JSONObject json = JSONObject.parseObject(response);
            snapshotFileId = json.getString("data");
            return snapshotFileId;
        } catch (IOException ex) {
            throw new RuntimeException(
                    "请求转换服务失败,server:" + this.transformServerHost + ",flvFileId:" + flvFileId, ex);
        }
    }

    /**
     * 
     * @Description: 提取pdf页的快照
     * @param pdfFileId 生成的image
     * @param page 指定生成哪一页快照
     * @return
     */
    private String snapshot4Pdf(String pdfFileId, int page) {
        String snapshotFileId = null;
        try {
            URL url = new URL(this.transformServerHost + "/transform/snap4pdf?fileId=" + pdfFileId
                    + "&page=" + page);
            String response = IOUtils.toString(url.openStream(), "UTF-8");
            JSONObject json = JSONObject.parseObject(response);
            snapshotFileId = json.getString("data");
            return snapshotFileId;
        } catch (IOException ex) {
            throw new RuntimeException(
                    "请求转换服务失败,server:" + this.transformServerHost + ",pdfFileId:" + pdfFileId, ex);
        }
    }

    private void deleteSourceFile(String sourceFileId) {
        FastdfsFileServer.INSTANCE.remove(sourceFileId);
    }

    public void setTransformServerHost(String transformServerHost) {
        this.transformServerHost = transformServerHost;
    }
}
