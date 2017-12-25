package com.shsxt.xm.api.utils;


import com.shsxt.xm.api.exceptions.AuthExcetion;
import com.shsxt.xm.api.exceptions.ParamsExcetion;

public class AssertUtil {
	
	public static void isTrue(Boolean flag,String errorMsg) {
		if(flag){
			throw new ParamsExcetion(errorMsg);
		}
	}
	
	
	public static void isTrue(Boolean flag,String errorMsg,Integer errorCode) {
		if(flag){
			throw new ParamsExcetion(errorMsg,errorCode);
		}
	}
	public static void isNotLogin(Boolean flag,String errorMsg) {
		if(flag){
			throw new AuthExcetion(errorMsg);
		}
	}

	
}
