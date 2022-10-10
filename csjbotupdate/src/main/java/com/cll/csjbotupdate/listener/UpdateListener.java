package com.cll.csjbotupdate.listener;


import com.cll.csjbotupdate.model.UpdateModel;

/**
 * 更新监听接口
 * @author cll
 *
 */
public interface UpdateListener {
	void onFoundNewVersion(UpdateModel updateInfo);
	void onNoFoundNewVersion();
	void onCheckError(String errorMsg);

	void onDownloading(Integer... values);
	void onDownloadFinish();
	void onDownloadCanceled();
	void onDownloadFailed(String errorMsg);
}
