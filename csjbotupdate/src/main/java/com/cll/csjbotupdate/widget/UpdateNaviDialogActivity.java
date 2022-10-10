package com.cll.csjbotupdate.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cll.csjbotupdate.R;
import com.cll.csjbotupdate.utils.AndroidUtil;


public class UpdateNaviDialogActivity extends AppCompatActivity {
    private static final float WIDTH_PERCENT = 0.8F;
    private static Builder mBuilder;
    private TextView tvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_navi_dialog);
        this.resizeDialog();
        this.initView();
    }

    public UpdateNaviDialogActivity(UpdateNaviDialogActivity.Builder builder) {
        mBuilder = builder;
    }

    public UpdateNaviDialogActivity() {
    }

    private void initView() {
        TextView tvTitle = findViewById(R.id.tv_title);
        tvContent = findViewById(R.id.tv_msg);
        tvContent.setGravity(mBuilder.contentGravity);
        tvTitle.setText(mBuilder.title != null ? mBuilder.title : tvTitle.getText());
        tvContent.setText(mBuilder.content != null ? mBuilder.content : tvContent.getText());
    }

    public void show() {
        Intent dialogIntent = new Intent(mBuilder.context, UpdateNaviDialogActivity.class);
        dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mBuilder.context.startActivity(dialogIntent);
    }

    private String tempText = null;
    public void setText(String text) {
        tempText = text;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (tvContent != null) {
                    tvContent.setText(tempText);
                }
            }
        });

    }
    public void dismiss() {
        finish();
    }

    private void resizeDialog() {
        Window window = this.getWindow();
        int width = (int) (AndroidUtil.getScreenWidth(window.getContext()) * WIDTH_PERCENT);
        window.setGravity(Gravity.CENTER);
        window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(R.drawable.drawable_dialog_bg);

        setFinishOnTouchOutside(false);
    }

    public static class Builder {
        private Context context;
        private String title;
        private String content;
        private int contentGravity = Gravity.CENTER;

        public Builder(Context context) {
            this.context = context;
        }

        public UpdateNaviDialogActivity build() {
            return new UpdateNaviDialogActivity(this);
        }

        public void show() {
            build().show();
        }

        public void dismiss() {
            build().dismiss();
        }
        public UpdateNaviDialogActivity.Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public UpdateNaviDialogActivity.Builder setContent(String content) {
            this.content = content;
            build().setText(content);
            return this;
        }


        public UpdateNaviDialogActivity.Builder setContentGravity(int gravity) {
            this.contentGravity = gravity;
            return this;
        }
    }
}