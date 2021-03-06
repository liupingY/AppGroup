package com.prize.music.ui.adapters;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;

import com.prize.app.constants.Constants;
import com.prize.music.cache.ImageInfo;
import com.prize.music.cache.ImageProvider;
import com.prize.music.ui.fragments.grid.QuickQueueFragment;
import com.prize.music.views.ViewHolderQueue;

/**
 * @author Andrew Neal
 */
public class QuickQueueAdapter extends SimpleCursorAdapter {

	private WeakReference<ViewHolderQueue> holderReference;

	private Context mContext;

	private ImageProvider mImageProvider;

	public QuickQueueAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags) {
		super(context, layout, c, from, to, flags);
		mContext = context;
		mImageProvider = ImageProvider.getInstance((Activity) mContext);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final View view = super.getView(position, convertView, parent);

		Cursor mCursor = (Cursor) getItem(position);
		// ViewHolderQueue
		final ViewHolderQueue viewholder;

		if (view != null) {

			viewholder = new ViewHolderQueue(view);
			holderReference = new WeakReference<ViewHolderQueue>(viewholder);
			view.setTag(holderReference.get());

		} else {
			viewholder = (ViewHolderQueue) convertView.getTag();
		}

		// Artist Name
		String artistName = mCursor.getString(QuickQueueFragment.mArtistIndex);

		// Album name
		String albumName = mCursor.getString(QuickQueueFragment.mAlbumIndex);

		// Track name
		String trackName = mCursor.getString(QuickQueueFragment.mTitleIndex);
		holderReference.get().mTrackName.setText(trackName);

		// Album ID
		String albumId = mCursor.getString(QuickQueueFragment.mAlbumIdIndex);

		ImageInfo mInfo = new ImageInfo();
		mInfo.type = Constants.TYPE_ALBUM;
		mInfo.size = Constants.SIZE_THUMB;
		mInfo.source = Constants.SRC_FIRST_AVAILABLE;
		mInfo.data = new String[] { albumId, artistName, albumName };
		mImageProvider.loadImage(viewholder.mAlbumArt, mInfo);

		mInfo = new ImageInfo();
		mInfo.type = Constants.TYPE_ARTIST;
		mInfo.size = Constants.SIZE_THUMB;
		mInfo.source = Constants.SRC_FIRST_AVAILABLE;
		mInfo.data = new String[] { artistName };
		mImageProvider.loadImage(viewholder.mArtistImage, mInfo);

		return view;
	}
}
