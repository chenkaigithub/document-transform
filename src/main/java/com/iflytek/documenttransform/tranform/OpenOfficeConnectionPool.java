/**
 * Copyright © 2016 科大讯飞股份有限公司. All rights reserved.
 */
package com.iflytek.documenttransform.tranform;

import java.net.ConnectException;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.iflytek.documenttransform.config.Config;

/**
 * @description：
 *
 * @author suenlai
 * @date 2016年6月14日
 */
public class OpenOfficeConnectionPool {
    public static final OpenOfficeConnectionPool INSTANCE        = new OpenOfficeConnectionPool(10);
    private Queue<OpenOfficeConnection>          connectionQueue = null;
    private int                                  initCount       = 5;

    private OpenOfficeConnectionPool(int initCount) {
        this.initCount = initCount;
        connectionQueue = new ArrayBlockingQueue<OpenOfficeConnection>(this.initCount);

        OpenOfficeConnection createdConnection = null;
        for (int i = 0; i < initCount; i++) {
            // connect to an OpenOffice.org instance running on port 8100
            createdConnection = createObject();
            connectionQueue.add(createdConnection);
        }
    }

    public OpenOfficeConnection borrowObject() {
        OpenOfficeConnection borrowingConnection = connectionQueue.poll();
        boolean connected = validateObject(borrowingConnection);

        if (!connected) {
            borrowingConnection = createObject();
        }
        return borrowingConnection;
    }

    protected void close() {
        OpenOfficeConnection c = null;
        do {
            c = connectionQueue.poll();
            c.disconnect();
        } while (c != null);
    }

    public OpenOfficeConnection createObject() {
        OpenOfficeConnection createdConnection =
                new SocketOpenOfficeConnection(Config.OPENOFFICE_HOST, Config.OPENOFFICE_PORT);
        try {
            createdConnection.connect();
        } catch (ConnectException e) {
            e.printStackTrace();
            System.exit(0);
        }
        return createdConnection;
    }

    protected int getInitCount() {
        return initCount;
    }

    public void returnObject(OpenOfficeConnection returningConnection) {
        boolean connected = validateObject(returningConnection);

        if (!connected) {
            returningConnection = createObject();
        }
        connectionQueue.offer(returningConnection);
    }

    protected boolean validateObject(OpenOfficeConnection connection) {
        if (connection == null) {
            return false;
        }
        return connection.isConnected();
    }
}
