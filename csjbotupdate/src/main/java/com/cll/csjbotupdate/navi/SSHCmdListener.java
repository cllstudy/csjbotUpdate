package com.cll.csjbotupdate.navi;

public interface SSHCmdListener {
    void onMessage(String message);

    void onComplete();

    void onError();
}
