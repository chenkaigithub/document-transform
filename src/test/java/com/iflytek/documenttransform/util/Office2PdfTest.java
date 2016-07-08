package com.iflytek.documenttransform.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.Assert;
import org.junit.Test;

import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.iflytek.documenttransform.config.Constant;
import com.iflytek.documenttransform.store.FastdfsFileServer;
import com.iflytek.documenttransform.tranform.Office2PdfTransform;
import com.iflytek.documenttransform.tranform.OpenOfficeConnectionPool;
import com.iflytek.documenttransform.tranform.Pdf2SwfTransform;

public class Office2PdfTest {
    String fileName = "甘肃省智慧教育统一认证平台建设方案V0.2.docx";
    String filePath = "/Users/suenlai/Desktop/甘肃省智慧教育统一认证平台建设方案V0.2.docx";

    @Test
    public void testOffice2Pdf() throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(filePath);
        String officeFileId = FastdfsFileServer.INSTANCE.saveUploadFile(fis, fileName);

        // String officeFileId = "group1:M00/00/03/rBAQf1dhPl2APHqyAE9FF959o-U82.docx";
        System.out.println("officeFileId:" + officeFileId);
        Office2PdfTransform office2Pdf = new Office2PdfTransform();
        String pdfFileId = office2Pdf.transform(officeFileId,null);
        System.out.println("pdfFileId:" + pdfFileId);
        String pdfFileName =
                FastdfsFileServer.INSTANCE.fileMateValue(pdfFileId, Constant.FASTDFS_FILENAME_KEY);
        System.out.println(pdfFileName);
        Assert.assertEquals(pdfFileName, FileNameUtils.getBaseName(fileName) + ".pdf");


        //
        testPdf2Swf(pdfFileId);

        FastdfsFileServer.INSTANCE.remove(officeFileId);
        FastdfsFileServer.INSTANCE.remove(pdfFileId);

    }

    public void testPdf2Swf(String pdfFileId) {
        Pdf2SwfTransform pdf2SwfTransform = new Pdf2SwfTransform();
        String swfFileId = pdf2SwfTransform.transform(pdfFileId,null);

        System.out.println("swfFileId:" + swfFileId);
        String swfFileName =
                FastdfsFileServer.INSTANCE.fileMateValue(swfFileId, Constant.FASTDFS_FILENAME_KEY);
        Assert.assertEquals(swfFileName, FileNameUtils.getBaseName(fileName) + ".swf");

        FastdfsFileServer.INSTANCE.remove(swfFileId);
    }

    @Test
    public void testOpenofficeConnection() {
        OpenOfficeConnection connection = OpenOfficeConnectionPool.INSTANCE.borrowObject();
        Assert.assertEquals(true, connection.isConnected());
        OpenOfficeConnectionPool.INSTANCE.returnObject(connection);
    }

}
