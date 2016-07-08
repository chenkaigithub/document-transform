/**
 * Copyright © 2016 科大讯飞股份有限公司. All rights reserved.
 */
package com.iflytek.documenttransform.store;

import java.io.InputStream;
import java.net.InetSocketAddress;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerGroup;
import org.csource.fastdfs.TrackerServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iflytek.documenttransform.common.TransformException;
import com.iflytek.documenttransform.config.Config;

/**
 *
 * @description：文件存储的fastdfs实现 其中文件唯一标识由 $(groupName):${fileName}组成
 *
 * @author suenlai
 * @date 2016年6月8日
 */
public class FastdfsFileServer implements FileApi {

    private static final Logger           LOGGER   =
            LoggerFactory.getLogger(FastdfsFileServer.class);
    public static final FastdfsFileServer INSTANCE = new FastdfsFileServer();

    private FastdfsFileServer() {
        setConnectTimeout(Config.FASTDFS_CONFIG_CONNECT_TIMEOUT);
        setNetworkTimeout(Config.FASTDFS_CONFIG_NETWORK_TIMEOUT);
        setCharset(Config.FASTDFS_CONFIG_CHARSET);
        setTrackerHttpPort(Config.FASTDFS_CONFIG_TRACKER_HTTP_PORT);
        setTrackerServers(Config.FASTDFS_CONFIG_TRACKER_SERVERS);
        setAntiStealToken(Config.FASTDFS_CONFIG_ANTISTEAL_TOKEN);
        setHttpSecretKey(Config.FASTDFS_CONFIG_HTTPSECRET_KEY);
    }

    public String saveFile(byte[] data, String fileName) {
        String fileId = null;
        try {

            TrackerClient tracker = new TrackerClient();
            TrackerServer trackerServer = tracker.getConnection();
            StorageServer storageServer = null;

            StorageClient storageClient = new StorageClient(trackerServer, storageServer);
            NameValuePair nvp[] = new NameValuePair[] {new NameValuePair("fileName", fileName)};
            String fileIds[] = null;

            fileIds = storageClient.upload_file(data, FilenameUtils.getExtension(fileName), nvp);

            fileId = fileIds[0] + ":" + fileIds[1];
        } catch (Exception e) {
            throw new TransformException("保存文件失败", e);
        }
        return fileId;
    }

    public String saveUploadFile(InputStream inputSteam, String fileName) {
        String fileId = null;
        try {
            TrackerClient tracker = new TrackerClient();
            TrackerServer trackerServer = tracker.getConnection();
            StorageServer storageServer = null;
            StorageClient storageClient = new StorageClient(trackerServer, storageServer);
            NameValuePair nvp[] = new NameValuePair[] {new NameValuePair("fileName", fileName)};
            String fileIds[] = storageClient.upload_file(IOUtils.toByteArray(inputSteam),
                    FilenameUtils.getExtension(fileName), nvp);
            fileId = fileIds[0] + ":" + fileIds[1];
        } catch (Exception e) {
            throw new TransformException("保存文件失败", e);
        } finally {
            IOUtils.closeQuietly(inputSteam);
        }
        return fileId;
    }

    public byte[] get(String fileId) {
        if (!fileId.contains(":")) {
            throw new RuntimeException("fieldId必须以\"卷号:文件名\"为格式");
        }

        String[] groupAndFileName = StringUtils.split(fileId, ":");

        try {
            TrackerClient tracker = new TrackerClient();
            TrackerServer trackerServer = tracker.getConnection();
            StorageServer storageServer = null;

            StorageClient storageClient = new StorageClient(trackerServer, storageServer);
            byte[] b = storageClient.download_file(groupAndFileName[0], groupAndFileName[1]);
            return b;
        } catch (Exception e) {
            throw new TransformException("获取文件数据失败", e);
        }
    }

    public String fileMateValue(String fileId, String metaKey) {
        if (!fileId.contains(":")) {
            throw new RuntimeException("fieldId必须以\"卷号:文件名\"为格式");
        }
        if (StringUtils.isEmpty(metaKey)) {
            throw new RuntimeException("metaKey不能为空");
        }

        String[] groupAndFileName = StringUtils.split(fileId, ":");

        try {
            TrackerClient tracker = new TrackerClient();
            TrackerServer trackerServer = tracker.getConnection();
            StorageServer storageServer = null;

            StorageClient storageClient = new StorageClient(trackerServer, storageServer);
            NameValuePair nvps[] =
                    storageClient.get_metadata(groupAndFileName[0], groupAndFileName[1]);
            if (nvps != null && nvps.length > 0) {
                for (NameValuePair nvp : nvps) {
                    if (StringUtils.equalsIgnoreCase(nvp.getName(), metaKey)) {
                        return nvp.getValue();
                    }
                }
            }
        } catch (Exception e) {
            throw new TransformException("获取文件元信息失败", e);
        }
        return null;
    }

    public void remove(String fileId) {
        if (!fileId.contains(":")) {
            throw new RuntimeException("fieldId必须以\"卷号:文件名\"为格式");
        }
        String[] groupAndFileName = StringUtils.split(fileId, ":");
        try {

            TrackerClient tracker = new TrackerClient();
            TrackerServer trackerServer = tracker.getConnection();
            StorageServer storageServer = null;

            StorageClient storageClient = new StorageClient(trackerServer, storageServer);
            storageClient.delete_file(groupAndFileName[0], groupAndFileName[1]);
        } catch (Exception e) {
            throw new TransformException("删除文件失败", e);
        }
    }


    // setters code block

    public void setConnectTimeout(int connectTimeout) {
        // connect_timeout
        ClientGlobal.g_connect_timeout = connectTimeout * 1000;

    }

    public void setNetworkTimeout(int networkTimeout) {
        // network_timeout
        ClientGlobal.g_network_timeout = networkTimeout * 1000;

    }

    public void setCharset(String charset) {
        ClientGlobal.g_charset = charset;
    }

    public void setTrackerServers(String trackerServers) {
        // tracker_server
        String[] szTrackerServers = trackerServers.split(",");
        InetSocketAddress[] tracker_servers = new InetSocketAddress[szTrackerServers.length];
        for (int i = 0; i < szTrackerServers.length; i++) {
            String[] parts = szTrackerServers[i].split("\\:", 2);
            if (parts.length != 2) {
                FastdfsFileServer.LOGGER.error(
                        "the value of item \"tracker_server\" is invalid, the correct format is host:port");
                System.exit(0);
            }

            tracker_servers[i] =
                    new InetSocketAddress(parts[0].trim(), Integer.parseInt(parts[1].trim()));
        }
        ClientGlobal.g_tracker_group = new TrackerGroup(tracker_servers);
    }

    public void setTrackerHttpPort(int trackerHttpPort) {
        ClientGlobal.g_tracker_http_port = trackerHttpPort;
    }

    public void setAntiStealToken(boolean antiStealToken) {
        ClientGlobal.g_anti_steal_token = antiStealToken;
    }

    public void setHttpSecretKey(String httpSecretKey) {
        if (ClientGlobal.g_anti_steal_token) {
            ClientGlobal.g_secret_key = httpSecretKey;
        }

    }

}
