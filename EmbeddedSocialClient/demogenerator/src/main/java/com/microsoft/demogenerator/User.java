package com.microsoft.demogenerator;

import com.microsoft.embeddedsocial.server.model.UserRequest;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    public String userHandle;
    public String authorization;

    public User(String userHandle, String sessionToken) {
        this.userHandle = userHandle;
        this.authorization = UserRequest.createSessionAuthorization(sessionToken);
    }

    @Override
    public String toString() {
        return "User handle: " + userHandle + ", Authorization: " + authorization;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(userHandle);
        out.writeString(authorization);
    }

    private User(Parcel in) {
        this.userHandle = in.readString();
        this.authorization = in.readString();
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
