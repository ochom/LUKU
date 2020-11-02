package com.lysofts.luku.models;

import java.util.List;
import java.util.Map;

public class UserProfile {
    String name,email, birthday,sex, interestedIn, phone, image;
    Map<String, Upload> uploads;
    Object[] chats;
    Object[] matchRequestsSent;
    Object[] matchRequestsReceived;
    Object[] matches;
    Object[] likesSent;
    Object[] likesReceived;
    Object[] dislikes;

    public UserProfile() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getInterestedIn() {
        return interestedIn;
    }

    public void setInterestedIn(String interestedIn) {
        this.interestedIn = interestedIn;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public Map<String, Upload> getUploads() {
        return uploads;
    }

    public void setUploads(Map<String, Upload> uploads) {
        this.uploads = uploads;
    }

    public Object[] getChats() {
        return chats;
    }

    public void setChats(Object[] chats) {
        this.chats = chats;
    }

    public Object[] getMatchRequestsSent() {
        return matchRequestsSent;
    }

    public void setMatchRequestsSent(Object[] matchRequestsSent) {
        this.matchRequestsSent = matchRequestsSent;
    }

    public Object[] getMatchRequestsReceived() {
        return matchRequestsReceived;
    }

    public void setMatchRequestsReceived(Object[] matchRequestsReceived) {
        this.matchRequestsReceived = matchRequestsReceived;
    }

    public Object[] getMatches() {
        return matches;
    }

    public void setMatches(Object[] matches) {
        this.matches = matches;
    }

    public Object[] getLikesSent() {
        return likesSent;
    }

    public void setLikesSent(Object[] likesSent) {
        this.likesSent = likesSent;
    }

    public Object[] getLikesReceived() {
        return likesReceived;
    }

    public void setLikesReceived(Object[] likesReceived) {
        this.likesReceived = likesReceived;
    }

    public Object[] getDislikes() {
        return dislikes;
    }

    public void setDislikes(Object[] dislikes) {
        this.dislikes = dislikes;
    }
}
