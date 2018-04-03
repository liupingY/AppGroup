/*******************************************
 *版权所有©2015,深圳市铂睿智恒科技有限公司
 *
 *内容摘要：
 *当前版本：1.0.0
 *作	者：yiyi
 *完成日期：2015年7月23日
 *修改记录：
 *修改日期：
 *版 本 号：
 *修 改 人：
 *修改内容：
 ...
 *修改记录：
 *修改日期：
 *版 本 号：
 *修 改 人：
 *修改内容：
 *********************************************/
package com.prize.cloud.task;

import java.io.File;

import org.xutils.http.RequestParams;

import android.content.Context;
import android.text.TextUtils;

import com.prize.appcenter.R;
import com.prize.cloud.bean.Respond;
import com.prize.cloud.task.pojo.LoginInfo;
import com.prize.cloud.util.Utils;
import com.prize.custmerxutils.XExtends;

/**
 * 修改个人信息，头像除外
 * 
 * @author yiyi
 * @version 1.0.0
 */
public class ProfileTask extends NetTask<Void> {
	private String userId, passport;
	private String realname;
	private int gender = -1;
	private String icon;

	public ProfileTask(Context ctx, TaskCallback<Void> taskCallback,
			String userId, String passport) {
		super(ctx, taskCallback);
		this.userId = userId;
		this.passport = passport;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	@Override
	public void doExecute() {

		String url = HOST + "/cloud/account/profile";
		RequestParams params = new RequestParams(url);
		params.setConnectTimeout(NETWORK_TIMEOUT);
		params.addHeader("KOOBEE", "dido");
		params.addHeader("passport", passport);
		params.addBodyParameter("userId", userId);
		if (!TextUtils.isEmpty(realname))
			params.addBodyParameter("realname", realname);
		if (gender != -1)
			params.addBodyParameter("gender", gender + "");
		if (!TextUtils.isEmpty(icon))
			// params.addParameter("file", new File(icon));
			params.addBodyParameter("file", new File(icon), null);

		XExtends.http().post(params, new RespondCallback() {

			@Override
			public void onCancelled(CancelledException cex) {

			}

			@Override
			public void onFinished() {

			}

			@Override
			public void onSuccess(Respond respond) {
				if (!respond.getMsg().equals(NETWORK_OK)) {
					if (mContext != null) {
						onTaskError(0, mContext.getResources().getString(R.string.net_error));
					}
					return;
				}
				LoginInfo info = respond.convert(LoginInfo.class);
				if (info == null) {
					return;
				}
				Utils.saveInfo(mContext, info);
				onTaskSuccess(null);
			}

			@Override
			public void onError(int errorCode, String msg) {
				if (errorCode == NetTask.ERROR_TIMEOUT) {
					onTaskError(errorCode, mContext.getString(R.string.timeout));
				} else if (errorCode == NetTask.ERROR_NETWORK) {
					onTaskError(0, mContext.getResources().getString(R.string.net_error));
				} else if (errorCode == NetTask.ERROR_FAILURE) {
					onTaskError(errorCode, mContext.getString(R.string.failure));
				} else {
					onTaskError(errorCode, msg);
				}
			}

		});

	}

}
