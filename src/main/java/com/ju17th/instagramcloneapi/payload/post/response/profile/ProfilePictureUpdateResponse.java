package com.ju17th.instagramcloneapi.payload.post.response.profile;

public class ProfilePictureUpdateResponse {
    String imagePath;

    public ProfilePictureUpdateResponse(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
