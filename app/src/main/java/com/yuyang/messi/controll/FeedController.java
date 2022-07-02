package com.yuyang.messi.controll;

import android.os.Handler;
import android.os.Message;

import com.yuyang.messi.bean.DBFreeFeed;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FeedController {
    private static final String TAG = FeedController.class.getSimpleName();

    public static final int MSG_START_FEED_LOADING = 1;
    public static final int MSG_FINISH_FEED_LOADING = 2;

    private static final int FETCH_FEED_IMAGE_SIZE = 4;

    private List<DBFreeFeed> cacheFeeds = Collections.synchronizedList(new ArrayList<DBFreeFeed>());
    private DBFreeFeed currentFeed;

    protected Handler handler;

    private static FeedController feedController;

    public static FeedController getInstance() {
        if (feedController == null) {
            feedController = new FeedController();
        }
        return feedController;
    }

    private FeedController() {
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public DBFreeFeed getCurrentFeed() {
        return currentFeed;
    }

    public int getCount() {
        return cacheFeeds.size();
    }

    public DBFreeFeed getNextFeed() {
        if (currentFeed != null) {
            currentFeed.destroy();
            currentFeed = null;
        }
        if (cacheFeeds.size() < FETCH_FEED_IMAGE_SIZE) {
            loadMoreFeeds(cacheFeeds.size() == 0, cacheFeeds.size() == 0);
        }
        if (!cacheFeeds.isEmpty()) {
            currentFeed = cacheFeeds.remove(0);
        }
        return currentFeed;
    }

    public void loadMoreFeeds(boolean showLoadingUI, boolean forceReload) {
        if (forceReload) {
            clearFeeds();
        }
        if (cacheFeeds.size() >= 20) {
            Message msg = Message.obtain();
            msg.what = MSG_FINISH_FEED_LOADING;
            handler.dispatchMessage(msg);
            return;
        }
        loadRelatedFeeds(showLoadingUI, forceReload);
    }

    public void loadRelatedFeeds(boolean showLoadingUI, final boolean forceReload) {
//        final Set<String> filterParams = SobrrSharedPreferences.getVibingFilterParams();
//        final String filterTopic = SobrrSharedPreferences.getVibingFilterTopic();
//        if (showLoadingUI) {
//            Message msg = Message.obtain();
//            msg.what = MSG_START_FEED_LOADING;
//            handler.dispatchMessage(msg);
//        }
//        NetworkUtil.getRelatedFeeds(filterParams, filterTopic, fetchFeedsHandler(forceReload));
    }

//    public SobrrResponseHandler fetchFeedsHandler(final boolean forceReload) {
//        return new SobrrResponseHandler() {
//            @Override
//            public void handle(int responseCode, JSONArray errors, JSONObject response) {
//                try {
//                    if(SobrrErrorUtil.isErrorFree(responseCode, errors, response)) {
//                        fillFeedsFromResponse(response);
//                        String cursor = null;
//                        if(!response.isNull(NetworkConstants.BACKEND_CURSOR)) {
//                            cursor = response.getString(NetworkConstants.BACKEND_CURSOR);
//                        }
//                    } else {
//                        SobrrErrorUtil.showNetworkOrBackendError(responseCode, errors, response);
//                    }
//                    if (forceReload) {
//                        Message msg = Message.obtain();
//                        msg.what = MSG_FINISH_FEED_LOADING;
//                        handler.dispatchMessage(msg);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//    }

    protected void fillFeedsFromResponse(JSONObject response) {
//        int startFetchIndex = 0;
//        if (cacheFeeds.size() > 0) {
//            startFetchIndex = cacheFeeds.size();
//        }
//        try {
//            JSONArray feedsResponse = response.getJSONArray(NetworkConstants.BACKEND_FEEDS);
//            for (int i = 0; i < feedsResponse.length(); i++) {
//                JSONObject f = feedsResponse.getJSONObject(i);
//                DBFreeFeed newFeed = new DBFreeFeed(f);//FeedHelper.createFeed(f);
//                if (!isSameWithCurrentFeed(newFeed) &&
//                        newFeed.getMy_attitude() != null && (newFeed.getMy_attitude() == Attitude.Attitude_Null ||
//                        newFeed.getMy_attitude() == Attitude.Attitude_Cheers)) {
//                    cacheFeeds.add(newFeed);
//                }
//            }
//
//            for (; startFetchIndex < cacheFeeds.size(); startFetchIndex++) {
//                DBFreeFeed fd = cacheFeeds.get(startFetchIndex);
//                fetchFeedPicAndAvatar(fd);
//                getLocationInfoText(fd);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    private boolean isSameWithCurrentFeed(DBFreeFeed feed) {
        if (currentFeed != null && currentFeed.getRemote_id().equals(feed.getRemote_id())) {
            return true;
        }
        return false;
    }

    protected void fetchFeedPicAndAvatar(final DBFreeFeed feed) {
//        String avatarUrl = UrlUtil.transformToSmallPicUrl(feed.getUserBean().getAvatar_url());
//        SobrrActionUtil.getGlideInstance().load(avatarUrl).fetch();
//
//        SobrrActionUtil.getGlideInstance().load(feed.getPicture()).fetch();
//        SobrrActionUtil.getGlideInstance().load(feed.getThumbnail()).fetch();
//
//        if(feed.isVideoFeed() && DeviceUtil.isWifiConnected()){
//            FileCacheUtil.fetchFeedVideo(feed);
//        }
    }

    public static void reportFeed(long feedId) {
//        NetworkUtil.reportFeed(feedId, new SobrrResponseHandler() {
//            @Override
//            public void handle(int responseCode, JSONArray errors, JSONObject result) {
//                if (!SobrrErrorUtil.isErrorFree(responseCode, errors, result)) {
//                    short errorCode = SobrrErrorUtil.getErrorCode(responseCode, errors, result);
//                    if (SobrrErrorUtil.isResourceExpiredOrNotExistsError(errorCode)) {
//                        SobrrErrorUtil.showInvalidFeedErrorBanner(errorCode);
//                    } else {
//                        SobrrErrorUtil.showNetworkOrServiceError(responseCode);
//                    }
//                }
//            }
//        });
    }

    public static void deleteFeed(long feedId) {
//        NetworkUtil.deleteFeed(feedId, new SobrrResponseHandler() {
//            @Override
//            public void handle(int responseCode, JSONArray errors, JSONObject result) {
//                if (!SobrrErrorUtil.isErrorFree(responseCode, errors, result)) {
//                    short errorCode = SobrrErrorUtil.getErrorCode(responseCode, errors, result);
//                    if (SobrrErrorUtil.isResourceExpiredOrNotExistsError(errorCode)) {
//                        SobrrErrorUtil.showInvalidFeedErrorBanner(errorCode);
//                    } else {
//                        SobrrErrorUtil.showNetworkOrServiceError(responseCode);
//                    }
//                }
//            }
//        });
    }

    public void resetAllFeeds() {
        clearFeeds();
        loadMoreFeeds(true, true);
    }

    public void clearFeeds() {
        if (cacheFeeds.size() > 0) {
            cacheFeeds.clear();
        }
        if (currentFeed != null) {
            currentFeed.destroy();
            currentFeed = null;
        }
    }

}