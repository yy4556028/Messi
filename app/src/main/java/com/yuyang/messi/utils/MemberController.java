package com.yuyang.messi.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.yuyang.messi.bean.UserBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemberController {

    private static final String TAG = MemberController.class.getSimpleName();

    private static UserBean me;
    private Map<String, UserBean> friendMembers;
    private Map<String, UserBean> temporaryMembers;

    private static MemberController instance;

    public static MemberController getInstance() {
        if (instance == null) {
            instance = new MemberController();
        }
        return instance;
    }

    private MemberController() {
        friendMembers = Collections.synchronizedMap(new HashMap<String, UserBean>());
    }

    public void cleanAll() {
        cleanMe();
        friendMembers.clear();
    }

    public static void cleanMe() {
        if (me != null) {
            me = null;
        }
    }

    public static UserBean getMe() {
        return me;
    }

    public static String getMyRemoteIdStr() {
        UserBean me = getMe();
        if (me == null) return "";
        return me.getRemote_id() + "";
    }

    public static UserBean createMe(String memberString) {
        me = createMember(memberString);
        return me;
    }

    public UserBean findMemberById(String remote_id) {

        if (me.getRemote_id() == remote_id)
            return me;

        UserBean member = friendMembers.get(remote_id);
        if (member == null) {
            member = new UserBean();
            member.setRemote_id(remote_id);
        }
        return member;
    }

    public void updateMember(UserBean member) {
        if (member != null) {
            String memberId = member.getRemote_id();
            if (!friendMembers.containsKey(memberId)) {
                friendMembers.put(memberId, member);
            }
        }
    }

    public static UserBean createMember(String memberString) {

        if (TextUtils.isEmpty(memberString)) {
            return null;
        }
        UserBean userBean = new Gson().fromJson(memberString, UserBean.class);
        return userBean;
    }

    public List<UserBean> createMembers(List<String> memberList) {
        List<UserBean> members = new ArrayList<>();
        if (memberList != null) {

            for (int i = 0; i < memberList.size(); i++) {
                UserBean member = createMember(memberList.get(i));
                if (member != null) {
                    members.add(member);
                }
            }
        }
        return members;
    }

//    public void fetchMembersOfFriendRequestsToMeFromBackend(final SobrrResponseHandler handler) {
//        NetworkUtil.getFriendRequestToMe(friendRequestCursor, new SobrrResponseHandler() {
//            @Override
//            public void handle(int responseCode, JSONArray errors, JSONObject response) {
//                if (SobrrActionUtil.signOut) return;
//                try {
//                    if (response == null) return;
//                    JSONArray friendRequestArr = response.getJSONArray(NetworkConstants.BACKEND_FRIEND_REQUESTS);
//                    if (friendRequestArr.length() == 0) return;
//                    friendRequestCursor = response.getString(NetworkConstants.BACKEND_CURSOR);
//                    createMembersFromFriendships(friendRequestArr, FriendshipType.REQUESTED_TO_ME);
//                    updateFriendRequestMembers();
//                    List<DBFreeMember> requestList = fetchMembers(FriendshipType.REQUESTED_TO_ME);
//                    if (friendRequestArr.length() > 0 && requestList.size() > 0) {
//                        handler.handle(true);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }

//    public void getFriends(final SobrrResponseHandler handler) {
//        NetworkUtil.getFriendsWithHandler(friendCursor, new SobrrResponseHandler() {
//            @Override
//            public void handle(int responseCode, JSONArray errors, JSONObject response) throws JSONException {
//                if (SobrrErrorUtil.isErrorFree(responseCode, errors, response)) {
//                    JSONArray frArray = response.getJSONArray((NetworkConstants.BACKEND_FRIENDSHIPS));
//                    if (frArray.length() == 0) {
//                        finishFetching = true;
//                        handler.handle(true);
//                        initRecentContacts();
//                        return;
//                    } else {
//                        finishFetching = false;
//                        handler.handle(false);
//                    }
//                    friendCursor = response.getString(NetworkConstants.BACKEND_CURSOR);
//                    if (frArray.length() > 0) {
//                        for (int i = 0; i < frArray.length(); i++) {
//                            JSONObject fr = frArray.getJSONObject(i);
//                            FriendshipType friendshipType;
//                            String status = fr.getString(NetworkConstants.BACKEND_STATUS);
//                            if (status.equals("temporary")) {
//                                friendshipType = FriendshipType.TEMPORARY;
//                            } else {
//                                friendshipType = FriendshipType.IS_FRIEND;
//                            }
//                            JSONObject memberJSON = fr.getJSONObject(NetworkConstants.BACKEND_MEMBER);
//                            DBFreeMember m = createMember(memberJSON);
//                            if (m != null) {
//                                m.setFriendship(friendshipType);
//                                m.setFriendship_id(fr.getLong(NetworkConstants.BACKEND_ID));
//                                if (friendshipType == FriendshipType.TEMPORARY) {
//                                    m.setContinue_enabled(fr.getBoolean(NetworkConstants.BACKEND_CONTINUE_ENABLED));
//                                    m.setState_changed_at((long) fr.getDouble(NetworkConstants.BACKEND_ACTIVEAT));
//                                }
//                                if (!TextUtils.isEmpty(m.getBackground_url()) && SobrrActionUtil.getLruCacheInstance().getValueByUri(m.getBackground_url()) == null) {
//                                    SobrrActionUtil.getGlideInstance().load(m.getBackground_url()).fetch();
//                                }
//                            }
//                            update(m);
//                        }
//                    }
//                }
//
//                if (!finishFetching) {
//                    getFriends(handler);
//                }
//            }
//        });
//    }

//    public void queryMemberByUserName(final String username, final SobrrBaseCallback callback) {
//        if (!TextUtils.isEmpty(username)) {
//            Map<Long, DBFreeMember> members = MemberController.getAppContext().getFriendRelatedMembers();
//            for (Map.Entry<Long, DBFreeMember> entry : members.entrySet()) {
//                DBFreeMember member = entry.getValue();
//                if (username.equals(member.getUsername())) {
//                    callback.onSuccess(member);
//                    return;
//                }
//            }
//            NetworkUtil.getSearchFriends(username, new SobrrResponseHandler() {
//                @Override
//                public void handle(int responseCode, JSONArray errors, JSONObject response) throws JSONException {
//                    if (SobrrErrorUtil.isErrorFree(responseCode, errors, response)) {
//                        JSONArray memberJSONs = response.getJSONArray(NetworkConstants.BACKEND_SEARCH_MEMBERS);
//                        List<DBFreeMember> searchStrangers;
//                        searchStrangers = MemberController.getAppContext().createMembers(memberJSONs);
//                        for (DBFreeMember member : searchStrangers) {
//                            if (username.equals(member.getUsername())) {
//                                callback.onSuccess(member);
//                                break;
//                            }
//                        }
//                    } else {
//                        callback.onFailed();
//                        SobrrErrorUtil.showNetworkOrBackendError(responseCode, errors, response);
//                    }
//                }
//
//                @Override
//                public void handleNormalFailure() {
//                    callback.onFailed();
//                }
//            });
//        }
//    }
}