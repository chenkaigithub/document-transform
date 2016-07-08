package com.iflytek.documenttransform.util;

import java.util.UUID;

public class UUIDUtils {

	public static String getUUID(){
		String id = UUID.randomUUID().toString().replace("-","");
		return id;
	}
}
