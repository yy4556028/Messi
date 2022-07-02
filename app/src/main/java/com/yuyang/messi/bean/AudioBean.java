//package com.yuyang.messi.bean;
//
//import android.net.Uri;
//import android.os.Parcel;
//import android.os.Parcelable;
//import android.provider.MediaStore;
//
//public class AudioBean implements Parcelable {
//
//    private Long audioId;
//    private String title;
//    private Uri audioUri;
//    private int length;
//    private String image;
//    private String artist;
//
//    public AudioBean() {
//    }
//
//    protected AudioBean(Parcel in) {
//        if (in.readByte() == 0) {
//            audioId = null;
//        } else {
//            audioId = in.readLong();
//        }
//        title = in.readString();
//        audioUri = in.readParcelable(Uri.class.getClassLoader());
//        length = in.readInt();
//        image = in.readString();
//        artist = in.readString();
//    }
//
//    public Long getAudioId() {
//        return audioId;
//    }
//
//    public void setAudioId(Long audioId) {
//        this.audioId = audioId;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public Uri getAudioUri() {
//        if (this.audioId != null && this.audioUri == null) {
//            this.audioUri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, String.valueOf(this.audioId));
//        }
//
//        return this.audioUri;
//    }
//
//    public void setAudioUri(Uri audioUri) {
//        this.audioUri = audioUri;
//    }
//
//    public int getLength() {
//        return length;
//    }
//
//    public void setLength(int length) {
//        this.length = length;
//    }
//
//    public String getImage() {
//        return image;
//    }
//
//    public void setImage(String image) {
//        this.image = image;
//    }
//
//    public String getArtist() {
//        return artist;
//    }
//
//    public void setArtist(String artist) {
//        this.artist = artist;
//    }
//
//    public boolean equals(Object other) {
//        if (this == other) return true;
//        if (other == null || getClass() != other.getClass()) return false;
//
//        AudioBean that = (AudioBean) other;
//
//        return getAudioUri().equals(that.getAudioUri());
//    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        if (audioId == null) {
//            dest.writeByte((byte) 0);
//        } else {
//            dest.writeByte((byte) 1);
//            dest.writeLong(audioId);
//        }
//        dest.writeString(title);
//        dest.writeParcelable(audioUri, flags);
//        dest.writeInt(length);
//        dest.writeString(image);
//        dest.writeString(artist);
//    }
//
//    public static final Creator<AudioBean> CREATOR = new Creator<AudioBean>() {
//        @Override
//        public AudioBean createFromParcel(Parcel in) {
//            return new AudioBean(in);
//        }
//
//        @Override
//        public AudioBean[] newArray(int size) {
//            return new AudioBean[size];
//        }
//    };
//}
