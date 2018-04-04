package com.android.launcher3;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.launcher3.bean.Theme;
import com.android.launcher3.bean.Wallpaper;
import com.android.launcher3.lq.DefaultConfig;
import com.android.launcher3.lq.FindDefaultResoures;
import com.lqsoft.lqtheme.LqShredPreferences;
import com.lqsoft.lqtheme.LqThemeParser;

//add by zhouerlong

public class ThemeListView extends GridView implements View.OnClickListener {

	public ThemeListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	private Launcher mLauncher;
	public static String themeName;
	private static HashMap themeNameMap = new HashMap<>();
	private String name;
	// 静态变量
	// 数据库相关
	private static final Uri STORE_LOCAL_THEME_URI = Uri
			.parse("content://com.nqmobile.live.base.dataprovider" + "/"
					+ "theme_local");
	private static final String ThemeLocalTable_THEME_ID = "themeId";// 主题id
	private static final String ThemeLocalTable_THEME_ICON_PATH = "iconPath";
	private static final String ThemeLocalTable_THEME_PATH = "themePath";
	private static final String ThemeLocalTable_THEME_NAME = "name";
	private static final String ThemeLocalTable_THEME_SELECT = "isSelected";
	 private 	String mCurrentThemeId=null;

	private static final Uri TABLE_URI = Uri
			.parse("content://com.nqmobile.live.base.dataprovider" + "/"
					+ "download");
	public static final String DOWNLOAD_RES_ID = "resId";
	public static final String _ID = "_id";
	public static final String DOWNLOAD_IS_FINISH = "is_finish";
	public static final int DOWNLOAD_FINISH = 1;
	private static final int i = 0;

	public static String BROADCAST_INTENTFILTER = "lqstore_download_compelete";// 商店的主题或者壁纸下载完成会发这个广播
	public static String BROADCAST_INTENTFILTER_DELETE = "lqstore_delete_compelete";// 商店的主题或者壁纸下载完成会发这个广播

	public static String KEY_RESOURCE_PREVIEW = "key_resource_preview";// 预览图
	public static String KEY_RESOURCE_NAME = "key_resource_name";// 资源名字
	public static String KEY_RESOURCE_PATH = "key_resource_path";// 资源路径
	public static String KEY_RESOURCE_ID = "key_resource_id";// 资源唯一id
	public static String KEY_RESOURCE_TYPE = "key_resource_type";// 资源类型是主题还是壁纸int类型
																	// 0：主题
	HorizontalListView mHorizontalListView; // ，1：壁纸

	/**
	 * 主题集合
	 */
	private List<Theme> mThemeItems;
	private ThemesAdapter mThemeAdater;
	/**
	 * 加载主题任务类
	 */
	LoadThemeTask task = new LoadThemeTask();

	public static int THEME_MAX = 5;

	/**
	 * 加载固定的项 默认主题，更多（主题商店入口） 初始化适配器
	 * 
	 * @param context
	 * @param resource
	 */
	public void init(Context context, int resouce,boolean referenceView) {
		// setDefultTheme();
		themeNameMap = Launcher.themeNames;
		mThemeItems = new ArrayList<Theme>();
		Theme more = new Theme();
		// more.thumb = this.getContext().getDrawable(R.drawable.more);
		more.themeName = mLauncher.getString(R.string.more);
		more.id = "more";
		mThemeItems.add(more);
		

		Theme easy_launcher = new Theme();
		easy_launcher.themeName = mLauncher.getString(R.string.easy_launcher);
		easy_launcher.id = "easy";
		boolean easy_item = Utilities.supportEasyLauncher();
		if (easy_item) {
//			mThemeItems.add(easy_launcher);
		}
		if(referenceView) {
		mThemeAdater = new ThemesAdapter(context, resouce);
		this.setAdapter(mThemeAdater);
		new LoadThemeTask().execute();
		this.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				if (mCurrentThemeId != null) {
				updateThemeSelect(mCurrentThemeId);
				}
				
			}
		});
		}

	}
	
	public void end() {
		postDelayed(new Runnable() {
			
			@Override
			public void run() {
				int i=indexofId(mCurrentThemeId);
				Bitmap screen1 = ImageUtils.screenshot(mLauncher);
				
				Theme theme = mThemeItems.get(i);
				String path = theme.strThemePath.substring(0, theme.strThemePath.lastIndexOf("/"));
				String icon_str = path+"/"+theme.id+"_"+"icon"+".png";
				String preview0 = path+"/"+theme.id+"_"+"preview0"+".png";
				final String preview1 = path+"/"+theme.id+"_"+"preview1"+".png";
				ImageUtils.savePNG1_After(screen1, preview0);
				Bitmap  icon = ImageUtils.resize(screen1, 218, 388);
				ImageUtils.savePNG1_After(icon, icon_str);
				mLauncher.getworkspace().snapToPage(1);
				if(i!=-1) {
					i++;
				}
				final int m = i;
				postDelayed(new Runnable() {
					
					@Override
					public void run() {
						Bitmap screen2 = ImageUtils.screenshot(mLauncher);
						ImageUtils.savePNG1_After(screen2, preview1);
						mLauncher.getworkspace().snapToPage(0);
						postDelayed(new Runnable() {
							
							@Override
							public void run() {
								if(m<mThemeItems.size()) {
									updateTheme(mThemeItems.get(m));
									}else {
								        Toast.makeText(mLauncher, "所有主题截取完毕", Toast.LENGTH_LONG).show();
									}
							}
						}, 4*1000);
					}
				}, 2*1000);
			}
		}, 1*1000);
	}
	
	
	public void release() {
		mThemeItems.clear();
		themeNameMap=null;
		mThemeAdater.notifyDataSetChanged();
		mThemeAdater=null;
	}
	
	public void reload() {
		init(getContext(),
				R.layout.themes_list_item,true);
	}

	public List<Theme> load() {
		return loadStoreThemes();
	}
	
	public void init(Context c) {
		init(c, R.layout.themes_list_item,true);
	}
	
	
	public boolean isNeedAdd(Theme item) {
		for(Theme t:mThemeItems) {
			if(item.id.equals(t.id)) {
				return false;
			}
		}
		return true;
	}

	public void bindAddThemeItem(Theme item) {
		if(!isNeedAdd(item)) {
			return;
		}
		mThemeItems.add(item);
		DisplayMetrics dm = new DisplayMetrics();
		mLauncher.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int w = (int) (dm.widthPixels / 4.2);
		// int w = 720 / 4;
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(w
				* mThemeItems.size(), LayoutParams.MATCH_PARENT);
		setLayoutParams(lp);
		setNumColumns(mThemeItems.size());
		this.mThemeAdater.notifyDataSetChanged();
	}

	public void bindRemove(String id) {
		for (Theme theme : mThemeItems) {
			if (theme.id.equals(id)) {
				mThemeItems.remove(theme);
				break;
			}
		}
		DisplayMetrics dm = new DisplayMetrics();
		mLauncher.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int w = (int) (dm.widthPixels / 4.2);
		// int w = 720 / 4;
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(w
				* mThemeItems.size(), LayoutParams.MATCH_PARENT);
		setLayoutParams(lp);
		setNumColumns(mThemeItems.size());
		mThemeAdater.notifyDataSetChanged();
	}

	// add by zhouerlong
	public void updateThemeSelect(View v) {

		Theme item = (Theme) v.getTag();

		if (item.id.equals("more")) {
			return;
		}
		for (int i = 0; i < this.getChildCount(); i++) {
			View child = this.getChildAt(i);
			child.findViewById(R.id.theme_id).setSelected(false);

		}
		v.setSelected(true);
	}

	/**
	 * @author Administrator 加载主题类
	 */
	class LoadThemeTask extends AsyncTask<Theme, Theme, List<Theme>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(List<Theme> result) {
			mThemeItems.addAll(result);

			DisplayMetrics dm = new DisplayMetrics();
			mLauncher.getWindowManager().getDefaultDisplay().getMetrics(dm);
			int w = (int) (dm.widthPixels / 4.2);
			// int w = 720 / 4;
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(w
					* mThemeAdater.getCount(), LayoutParams.MATCH_PARENT);
			setLayoutParams(lp);
			setNumColumns(mThemeAdater.getCount());
			mThemeAdater.notifyDataSetChanged();
			updateCurrentThemeFromDb();
			if (Utilities.supportTestTheme()) {

				mLauncher.getworkspace().snapToPage(0);
				postDelayed(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(mLauncher, "所有主题图片截取", Toast.LENGTH_LONG)
								.show();
						updateTheme(mThemeItems.get(1));
					}
				}, 10 * 1000);

			}
		}

		@Override
		protected List<Theme> doInBackground(Theme... params) {
			return loadStoreThemes();

		}

	}
	
	
	public void showDialog() {
		  final ProgressDialog dialog = new ProgressDialog(getContext());
	        dialog.setMessage(getContext().getString(R.string.dialog_loading));
	        dialog.setIndeterminate(false);
	        dialog.setCancelable(false);
	        dialog.show();
	}
	
	private void updateTheme(Theme info) {
		mCurrentThemeId = info.id;
		notifyThemeChange(info);
		notifyThemeDb(info);
	}

	/**
	 * 通知主题中心主题发生改变
	 * 
	 * @param pcg
	 *            改变后的主题包包名
	 */
	private void notifyThemeChange(Theme t) {

		Intent i = new Intent("appley_theme_ztefs");// 发送主题应用的广播
		i.putExtra("resId", t.getId());
		i.putExtra("themePath", t.getStrThemePath());
		mLauncher.sendBroadcast(i);
	}

	public static boolean isDownloadCompleted(String resId, Context c) {
		if (TextUtils.isEmpty(resId)) {
			return false;
		}
		boolean result = false;
		Cursor cursor = null;
		try {
			ContentResolver contentResolver = c.getContentResolver();
			cursor = contentResolver.query(TABLE_URI, null, DOWNLOAD_RES_ID
					+ " = ?", new String[] { resId }, _ID
					+ " DESC LIMIT 1 OFFSET 0");
			if (cursor != null && cursor.moveToNext()) {
				Long finish = cursor.getLong(cursor
						.getColumnIndex(DOWNLOAD_IS_FINISH));
				if (finish == DOWNLOAD_FINISH) {
					result = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return result;
	}

	public String findThemeId(String strPath) {
		if(strPath !=null) {
			for (int i = 0; i < mThemeAdater.getCount(); i++) {
				Theme info=	mThemeAdater.getItem(i);
				if(info.strThemePath!=null&&info.strThemePath.equals(strPath)) {
					mCurrentThemeId= info.id;
				}
			}
		}
		return mCurrentThemeId;
	}
	
	public void updateTHemeFromPath(String path) {
		String id =findThemeId(path);
		updateThemeSelect(id);
	}
	public void updateThemeSelect(String selectId) {
		if(selectId !=null) {
			if(!selectId.equals(mCurrentThemeId)) {
				mCurrentThemeId = selectId;
			}
			for (int i = 0; i < this.getChildCount(); i++) {
				View child = this.getChildAt(i).findViewById(R.id.theme_id);
				Theme info = (Theme) child.getTag();
				if(info.id.equals(selectId)) {
					child.setSelected(true);
				}else {
					child.setSelected(false);
				}
			}
		}
	}
	
	
	private int indexofId(String id) {
		for(int i=0;i<mThemeItems.size();i++) {
			if(id.equals(mThemeItems.get(i).id)) {
				return i;
			}
		}
		return -1;
	}
	
	public void updateCurrentThemeFromDb() {
		SQLSingleThreadExcutor.getInstance().execute(new Runnable() {

			@Override
			public void run() {

				Cursor cursor = mLauncher.getContentResolver().query(
						STORE_LOCAL_THEME_URI, null,
						ThemeLocalTable_THEME_SELECT + "=?",
						new String[] { "1" }, null);
				if (cursor != null && cursor.moveToNext()) {
						mCurrentThemeId= cursor.getString(cursor
							.getColumnIndex(ThemeLocalTable_THEME_ID));
					
				}
			}

		});
	}

	// 查询商店数据库获取主题
	private List<Theme> loadStoreThemes() {
		List<Theme> themes = new ArrayList<>();
		Cursor cursor = mLauncher.getContentResolver().query(
				STORE_LOCAL_THEME_URI, null, null, null, null);
		while (cursor != null && cursor.moveToNext()) {
			Theme theme = new Theme();
			theme.setId(cursor.getString(cursor
					.getColumnIndex(ThemeLocalTable_THEME_ID)));
			theme.setIconPreviewPath(cursor.getString(cursor
					.getColumnIndex(ThemeLocalTable_THEME_ICON_PATH)));
			theme.setStrThemePath(cursor.getString(cursor
					.getColumnIndex(ThemeLocalTable_THEME_PATH)));
			if (null != getThemeName(cursor.getString(cursor
					.getColumnIndex(ThemeLocalTable_THEME_ID)))) {
				theme.setThemeName(getThemeName(cursor.getString(cursor
						.getColumnIndex(ThemeLocalTable_THEME_ID))));
			} else {
				theme.setThemeName(cursor.getString(cursor
						.getColumnIndex(ThemeLocalTable_THEME_NAME)));
			}
			if (theme.getStrThemePath() == null) {
				continue;
			}
			File file = new File(theme.getStrThemePath());

			if (file != null && file.exists()
					&& isDownloadCompleted(theme.id, mContext)) {
				themes.add(theme);
			}
			
			if(file != null && file.exists() && file.getPath().contains(FindDefaultResoures.DEFALUT_THEME_PATH)) {
				themes.add(theme);
			}
		}
		try {
			if (cursor != null) {
				cursor.close();
			}
		} catch (Exception e) {
		}
		return themes;
	}

	public void setLauncher(Launcher launcher) {
		mLauncher = launcher;
	}

	class HodlerView {
		AsyncImageView themeView;
		TextView textView;
	}

	// add by zhouerlong
	class ThemesAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private int mResource;

		public ThemesAdapter(Context context, int resource) {
			this.mResource = resource;
		}

		@Override
		public Theme getItem(int position) {
			return mThemeItems.get(position);
		}
		private int mMax=7;

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			mInflater = LayoutInflater.from(getContext());
			HodlerView holder;
			Theme item = this.getItem(position);
			// add by zhouerlong
			if (convertView == null) {
				holder = new HodlerView();
				convertView = this.mInflater.inflate(mResource, null);
//				convertView.setLayerType(LAYER_TYPE_HARDWARE, null);
				holder.themeView = (AsyncImageView) convertView
						.findViewById(R.id.theme_id);
				holder.textView = (TextView) convertView
						.findViewById(R.id.title);
				convertView.setTag(holder);
			} else {
				holder = (HodlerView) convertView.getTag();
			}
			if (item.id != null && item.id.equals("more")) {
				holder.themeView.setImageDrawable(mLauncher
						.getDrawable(R.drawable.choose_more_theme));
			}
			if (item.id != null && item.id.equals("easy")) {
				holder.themeView.setImageDrawable(mLauncher
						.getDrawable(R.drawable.easy_launcher));
			}
			
			if (holder.themeView != null) {
				if (item.iconPreviewPath != null) {
					holder.themeView.loadImage(item.iconPreviewPath);
				}
				holder.themeView.setScaleType(ScaleType.FIT_CENTER);
				// add by zhouerlong
				holder.themeView.setOnClickListener(ThemeListView.this);
				holder.themeView.setTag(item);
			}
			if (holder.textView != null) {
				holder.textView.setText(item.themeName);
			}
			return convertView;
		}

		@Override
		public int getCount() {
			/*if(mThemeItems.size()>mMax) {
				return mMax;
			}*/
			return mThemeItems.size();
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

	}

	public void notifyThemeDb(final Theme theme) {

		SQLSingleThreadExcutor.getInstance().execute(new Runnable() {
			@Override
			public void run() {

				Cursor cursor = mLauncher.getContentResolver().query(
						STORE_LOCAL_THEME_URI, null,
						ThemeLocalTable_THEME_SELECT + "=?",
						new String[] { "1" }, null);
				if (cursor != null && cursor.moveToNext()) {
					String oldId = cursor.getString(cursor
							.getColumnIndex(ThemeLocalTable_THEME_ID));
					ContentValues cv = new ContentValues();
					cv.put(ThemeLocalTable_THEME_SELECT, 0);
					mLauncher.getContentResolver().update(
							STORE_LOCAL_THEME_URI, cv,
							ThemeLocalTable_THEME_ID + "=?",
							new String[] { oldId });
				}

				ContentValues cv = new ContentValues();
				cv.put(ThemeLocalTable_THEME_SELECT, 1);
				try{
					mLauncher.getContentResolver().update(STORE_LOCAL_THEME_URI,
							cv, ThemeLocalTable_THEME_ID + "=?",
							new String[] { theme.getId() });
				}catch(Exception e)
				{
				}
				try {
					if (cursor != null) {
						cursor.close();
					}
				} catch (Exception e) {
				}
			}
		});

	}

	public void setDefultTheme(){
		SQLSingleThreadExcutor.getInstance().execute(new Runnable() {

			@Override
			public void run() {
				ContentValues cv = new ContentValues();
				cv.put(ThemeLocalTable_THEME_SELECT, 1);
				mLauncher.getContentResolver().update(STORE_LOCAL_THEME_URI,
						cv, ThemeLocalTable_THEME_ID + "=?",
						new String[] {"default"});
			}});
	}

	@Override
	public void onClick(View itemView) {
		if (itemView != null) {
			itemView.setSelected(false);
		}

//		BlueTask b = new BlueTask(mLauncher, null, mLauncher.getWallpaperBg());
//		b.execute();
		updateThemeSelect(itemView);
		AsyncImageView view = (AsyncImageView) itemView;
		// add by zhouerlong
		Theme info = (Theme) view.getTag();
		if (info.id != null && info.id.equals("more")) {

			Intent i = new Intent();
        	i.setClassName("com.nqmobile.live.base", "com.nqmobile.livesdk.commons.ui.StoreMainActivity");
        	i.putExtra("fragment_index_to_show", 1);
			try {
				view.getContext().startActivity(i);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if(info.id != null && info.id.equals("easy")) {
			mLauncher.showEasyLauncher();
		}else {
			if(mCurrentThemeId!=null&&mCurrentThemeId.equals(info.id))  {
//    			Toast.makeText(getContext(), "切换的是同一主题", Toast.LENGTH_SHORT).show();
    			return;
			}
			
			final ProgressDialog progressDialog = new ProgressDialog(mLauncher,R.style.prize_dialog_style);
			progressDialog.setMessage(getResources().getString(R.string.theme_is_change));
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.show();
			new Thread(){
				@Override
				public void run() {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					progressDialog.dismiss();
				}
				
			}.start();
//			Utilities.setBlueWallpaper(this,mLauncher);
			mCurrentThemeId = info.id;
			notifyThemeChange(info);
			notifyThemeDb(info);
		}

	}

	private String getThemeName(String ID) {

		String t = "CN;TW;US";
		String t1 = "CN;HK;US";
		String t2 = "CN;TW;GB";
		List<String> languages = Arrays.asList(t.split(";"));
		int i = languages.indexOf(Launcher.locale);
		if (i == -1) {
			languages = Arrays.asList(t1.split(";"));
			i = languages.indexOf(Launcher.locale);
		}
		if (i == -1) {
			languages = Arrays.asList(t2.split(";"));
			i = languages.indexOf(Launcher.locale);
		}
		if (i != -1) {
			Iterator iter = themeNameMap.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				Object key = entry.getKey();
				String value = (String) entry.getValue();
				if (null!=value && key.equals(ID) && value.split(";").length >1) {
					return value.split(";")[i];
				}
			}
		}
		return null;
	}
}