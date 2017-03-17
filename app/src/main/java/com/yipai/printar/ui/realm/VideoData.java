package com.yipai.printar.ui.realm;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by liuchuanliang on 2017/3/17.
 */

public class VideoData extends RealmObject{

            @PrimaryKey
            private String ImagePath;
            private String VideoPath;

            private RealmList<VideoData> realmList;


            public String getImagePath() {
                        return ImagePath;
            }

            public void setImagePath(String imagePath) {
                        ImagePath = imagePath;
            }

            public String getVideoPath() {
                        return VideoPath;
            }

            public void setVideoPath(String videoPath) {
                        VideoPath = videoPath;
            }

            public RealmList<VideoData> getRealmList() {
                        return realmList;
            }

            public void setRealmList(RealmList<VideoData> realmList) {
                        this.realmList = realmList;
            }
}
