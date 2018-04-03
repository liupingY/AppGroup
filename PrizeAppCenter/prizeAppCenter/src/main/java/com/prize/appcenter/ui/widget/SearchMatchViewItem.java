package com.prize.appcenter.ui.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.prize.app.BaseApplication;
import com.prize.app.beans.ClientInfo;
import com.prize.app.download.AppManagerCenter;
import com.prize.app.net.datasource.base.AppsItemBean;
import com.prize.app.util.MTAUtil;
import com.prize.appcenter.R;
import com.prize.appcenter.ui.dialog.DownDialog;
import com.prize.appcenter.ui.util.AIDLUtils;
import com.prize.appcenter.ui.util.ToastUtils;
import com.prize.appcenter.ui.util.UILimageUtil;
import com.prize.appcenter.ui.util.UIUtils;
import com.prize.appcenter.ui.widget.progressbutton.AnimDownloadProgressButton;

/**
 * 及时搜索头item
 */
public class SearchMatchViewItem extends LinearLayout {
    public CustomImageView itemImg;
    public TextView itemName;
    public TextView ad_flag_Tv;
    private TextView game_size_tv;
    public AnimDownloadProgressButton mProgressNoGiftButton;
    private DownDialog mDownDialog;

    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public SearchMatchViewItem(Context context) {
        super(context);
        mContext = context;
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        View view = inflate(context, R.layout.head_match_listview_item, this);
        findViewById(view);
    }

    private void findViewById(View view) {
        itemImg = (CustomImageView) view.findViewById(R.id.game_iv);
        ad_flag_Tv = (TextView) view.findViewById(R.id.ad_flag_Tv);
        itemName = (TextView) view.findViewById(R.id.game_name_tv);
        game_size_tv = (TextView) view.findViewById(R.id.game_size_tv);
        mProgressNoGiftButton = (AnimDownloadProgressButton) view.findViewById(R.id.game_download_btn);
    }

    public void setData(final AppsItemBean itemBean) {
        if (itemBean == null)
            return;
        ad_flag_Tv.setVisibility(View.GONE);
        if (itemBean.isAdvertise) {
            ad_flag_Tv.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(itemBean.largeIcon)) {
            ImageLoader.getInstance().displayImage(itemBean.largeIcon, itemImg,
                    UILimageUtil.getUILoptions(), null);
        } else {
            if (itemBean.iconUrl != null) {
                ImageLoader.getInstance().displayImage(itemBean.iconUrl,
                        itemImg, UILimageUtil.getUILoptions(), null);
            }
        }
        game_size_tv.setText(itemBean.apkSizeFormat);
        itemName.setText(itemBean.name.trim());
        mProgressNoGiftButton.setGameInfo(itemBean);
        mProgressNoGiftButton.enabelDefaultPress(true);
        mProgressNoGiftButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemBean==null)return;
                if(itemBean.isAdvertise){
                    MTAUtil.onMatchSearchClick(itemBean.name);
                }
                AnimDownloadProgressButton downloadBtn = (AnimDownloadProgressButton) v;
                final int state = AIDLUtils.getGameAppState(
                        itemBean.packageName, itemBean.id,
                        itemBean.versionCode);
                switch (state) {
                    case AppManagerCenter.APP_STATE_UNEXIST:
                    case AppManagerCenter.APP_STATE_UPDATE:
                    case AppManagerCenter.APP_STATE_DOWNLOAD_PAUSE:
                        if (ClientInfo.getAPNType(BaseApplication.curContext) == ClientInfo.NONET) {
                            ToastUtils.showToast(R.string.nonet_connect);
                            return;
                        }
                        if (BaseApplication.isDownloadWIFIOnly()
                                && ClientInfo.getAPNType(BaseApplication.curContext) != ClientInfo.WIFI) {
                            mDownDialog = new DownDialog(mContext, R.style.add_dialog);
                            mDownDialog.show();
                            mDownDialog.setmOnButtonClic(new DownDialog.OnButtonClic() {

                                @Override
                                public void onClick(int which) {
                                    dismissDialog();
                                    switch (which) {
                                        case 0:
                                            break;
                                        case 1:
                                            UIUtils.downloadApp(itemBean);
                                            if (state == AppManagerCenter.APP_STATE_UNEXIST) {
//                                                PrizeStatUtil.onClickBackParams(itemBean.backParams, itemBean.name, itemBean.packageName);
                                                AIDLUtils.upload360ClickDataNow(itemBean.backParams, itemBean.name, itemBean.packageName);
                                            }
                                            break;
                                    }
                                }
                            });

                        } else {
                            downloadBtn.onClick();
                            if (state == AppManagerCenter.APP_STATE_UNEXIST) {
//                                PrizeStatUtil.onClickBackParams(itemBean.backParams, itemBean.name, itemBean.packageName);
                                AIDLUtils.upload360ClickDataNow(itemBean.backParams, itemBean.name, itemBean.packageName);
                            }
                        }
                        break;

                    default:
                        downloadBtn.onClick();
                        break;
                }
            }
        });
    }

    public SearchMatchViewItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
    }


    private void dismissDialog() {
        if (mDownDialog != null && mDownDialog.isShowing()) {
            mDownDialog.dismiss();
            mDownDialog = null;
        }
    }
}
