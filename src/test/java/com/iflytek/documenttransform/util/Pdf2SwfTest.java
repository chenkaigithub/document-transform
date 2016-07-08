package com.iflytek.documenttransform.util;

import java.io.FileNotFoundException;

import org.junit.Assert;
import org.junit.Test;

import com.iflytek.documenttransform.config.Constant;
import com.iflytek.documenttransform.store.FastdfsFileServer;
import com.iflytek.documenttransform.tranform.Office2PdfTransform;

public class Pdf2SwfTest {
    @Test
    public void testOffice2Pdf() throws FileNotFoundException {
        String fileName = "甘肃省智慧教育统一认证平台建设方案V0.2.docx";
        /**
         * String filePath = "/Users/suenlai/Desktop/甘肃省智慧教育统一认证平台建设方案V0.2.docx"; FileInputStream
         * fis = new FileInputStream(filePath); String officeFileId =
         * FastdfsFileServer.INSTANCE.saveUploadFile(fis, fileName);
         **/
        String officeFileId = "group1:M00/00/02/rBAQf1dg_wOAAgM1AE9FF959o-U24.docx";
        System.out.println("officeFileId:" + officeFileId);
        Office2PdfTransform office2Pdf = new Office2PdfTransform();
        String pdfFileId = office2Pdf.transform(officeFileId, null);
        System.out.println("pdfFileId:" + pdfFileId);
        String pdfFileName =
                FastdfsFileServer.INSTANCE.fileMateValue(pdfFileId, Constant.FASTDFS_FILENAME_KEY);
        System.out.println(pdfFileName);
        Assert.assertEquals(pdfFileName, FileNameUtils.getBaseName(fileName) + ".pdf");
    }
}
