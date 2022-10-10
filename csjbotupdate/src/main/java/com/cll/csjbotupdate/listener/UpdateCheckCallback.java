package com.cll.csjbotupdate.listener;

/**
 * 更新监听接口
 * @author cll
 *
 */
public interface UpdateCheckCallback {
	
	void onSuccess(boolean hasNew);
	void onFailure(String msg);

}
