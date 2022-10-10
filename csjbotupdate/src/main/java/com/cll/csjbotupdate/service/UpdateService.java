package com.cll.csjbotupdate.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import androidx.annotation.Nullable;


import com.cll.csjbotupdate.AppUpdater;
import com.cll.csjbotupdate.UpdateConfig;
import com.cll.csjbotupdate.listener.UpdateListener;
import com.cll.csjbotupdate.model.UpdateModel;
import com.cll.csjbotupdate.task.CheckUpdateAsyncTask;
import com.cll.csjbotupdate.task.DownloadAsyncTask;
import com.cll.csjbotupdate.utils.AndroidUtil;
import com.cll.csjbotupdate.utils.JavaUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 检查更新服务
 *
 * @author cll
 * @date 2022/10/10
 */
public class UpdateService extends Service {

    public static final String EXTRA_KEY_HOST_URL = "host_url";

    private AsyncTask<String, Integer, String> downloadTask;
    private UpdateListener updateListener;
    private long lastUpdateTime;
    private String hostUpdateCheckUrl;

    private boolean serviceStarted;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Service已在运行
        if (this.serviceStarted) {
            return super.onStartCommand(intent, flags, startId);
        }

        AppUpdater.ServiceBridge.initUpdateService(this);

        this.hostUpdateCheckUrl = intent.getStringExtra(EXTRA_KEY_HOST_URL);
        // 启动检查更新异步任务
        new CheckUpdateAsyncTask(this).execute(this.hostUpdateCheckUrl);

        this.serviceStarted = true;

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        this.serviceStarted = false;
        if (downloadTask != null) {
            downloadTask.cancel(true);
        }
        super.onDestroy();
    }


    /**
     * 启动下载任务
     *
     * @param fileUrl
     * @param downloadPath
     * @param downloadFileName
     */
    public void startDownLoadTask(String fileUrl, String downloadPath, String downloadFileName) {
        downloadTask = new DownloadAsyncTask(this).execute(fileUrl, downloadPath, downloadFileName);
    }

    public void cancelDownload() {
        if (downloadTask != null) {
            downloadTask.cancel(true);
        }
    }


    /**
     * 注册检查更新回调接口
     */
    public void setUpdateListener(UpdateListener updateListener) {
        this.updateListener = updateListener;
    }

    public void checkUpdateResult(String hostVersionInfo) {
        if (JavaUtil.isEmptyString(hostVersionInfo) || hostVersionInfo.equals("error")) {
            this.updateListener.onCheckError("检查更新失败");
            return;
        }
        UpdateModel appUpdateModel = parseJson(hostVersionInfo);
        if (!UpdateConfig.isUpdateNavi) {
            if (AndroidUtil.getAppVersionCode(this) < appUpdateModel.getVersionCode()) {
                this.updateListener.onFoundNewVersion(appUpdateModel);
                return;
            }
        } else {
            //导航新版本
            if (UpdateConfig.naviLocalVersion < appUpdateModel.getVersionCode()) {
                this.updateListener.onFoundNewVersion(appUpdateModel);
                return;
            }
        }


        this.updateListener.onNoFoundNewVersion();
    }

    /**
     * 更新ProgressDialog或Notification进度
     *
     * @param values
     */
    public void updateProgress(Integer... values) {
        if (values[0] == DownloadAsyncTask.INT_FINISHED) {
            updateListener.onDownloadFinish();
        } else if (values[0] == DownloadAsyncTask.INT_ERROR) {
            updateListener.onDownloadFailed("下载失败");
        } else if (values[0] == DownloadAsyncTask.INT_CANCELED) {
            updateListener.onDownloadCanceled();
        } else if (System.currentTimeMillis() - lastUpdateTime > 100) {
            updateListener.onDownloading(values);
            lastUpdateTime = System.currentTimeMillis();
        }
    }


    private UpdateModel parseJson(String jsonStr) {
        UpdateModel appInfo = new UpdateModel();
        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            JSONObject rows = jsonObj.getJSONObject("rows");
            appInfo.setVersionCode(rows.getInt("versionCode"));
            appInfo.setVersionName(rows.getString("versionName"));
            appInfo.setFileSize(rows.getString("size"));
            appInfo.setApkUrl(rows.getString("fileUrl"));
//			是否是强制更新
//			appInfo.setRequired(jsonObj.getBoolean("required"));
            appInfo.setReleaseDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rows.getString("modifyByDate")));
            List<String> relaseNoteList = new ArrayList<>();
            String memo = rows.getString("memo");
//			JSONArray jsonArr = jsonObj.getJSONArray("releaseNotes");
//			for (int i=0; i<jsonArr.length(); i++) {
            relaseNoteList.add(memo);
//			}
            appInfo.setReleaseNoteList(relaseNoteList);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return appInfo;
    }


}



