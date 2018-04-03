///*******************************************
// *版权所有©2015,深圳市铂睿智恒科技有限公司
// *
// *内容摘要：
// *当前版本：
// *作	者：
// *完成日期：
// *修改记录：
// *修改日期：
// *版 本 号：
// *修 改 人：
// *修改内容：
//...
// *修改记录：
// *修改日期：
// *版 本 号：
// *修 改 人：
// *修改内容：
// *********************************************/
//
//package com.prize.app.net.datasource.base;
//
//import com.google.gson.Gson;
//import com.prize.app.beans.PageBean;
//import com.prize.app.constants.Constants;
//import com.prize.app.net.AppAbstractNetSource;
//import com.prize.app.net.datasource.base.AppGameCategoryData.Data;
//import com.prize.app.net.req.BaseResp;
//import com.prize.app.net.req.GetGameListResp;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;
//
//public class AppGameCategoryNetSource
//		extends
//		AppAbstractNetSource<AppGameCategoryData, Map<String, String>, GetGameListResp> {
//	private PageBean page = new PageBean();
//	private byte requestListTag = 0;
//	private String listCode;
//	private String rootType = null;
//
//	/**
//	 * 是否请求游戏
//	 *
//	 * @param rootType
//	 *            根类别 ： 1-软件 2-游戏
//	 */
//	public AppGameCategoryNetSource(String rootType) {
//		this.rootType = rootType;
//	}
//
//	@Override
//	protected Map<String, String> getRequest() {
//		// GetGameListReq req = new GetGameListReq();
//		Map<String, String> param = new HashMap<String, String>();
//		if (this.rootType != null) {
//			param.put("rootType", this.rootType);
//		}
//		return param;
//	}
//
//	@Override
//	protected Class<? extends BaseResp> getRespClass() {
//
//		// TODO Auto-generated method stub
//		return GetGameListResp.class;
//	}
//
//	@Override
//	protected AppGameCategoryData parseStrResp(String resp) {
//		AppGameCategoryData data = null;
//		try {
//			JSONObject areaList = new JSONObject(resp);
//			Iterator iterator = areaList.keys();
//			ArrayList<Data> mCategoriesParent = new ArrayList<Data>();
//			while (iterator.hasNext()) {
//				String typeName = (String) iterator.next();
//				String areaName = areaList.getString(typeName);
//				Data categories = new Gson().fromJson(areaName,
//						Data.class);
//				mCategoriesParent.add(categories);
//			}
//			data = new AppGameCategoryData();
//			data.data = mCategoriesParent;
//		} catch (JSONException e) {
//
//			e.printStackTrace();
//
//		}
//		return data;
//	}
//
//	@Override
//	public String getUrl() {
//
//		return Constants.GIS_URL + "/category/list";
//	}
//
//}
