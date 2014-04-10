package di.kdd.trends.classifier.statistics;

/**
 * Created by dimitris on 4/10/14.
 */
public class User {

    //User features
    private long userId;
    private long userFollowersCount;
    private long userFriendsCount;
    private boolean userVerified;
    private long userStatusesCount;
    private long userListedCount;

    public User(long userId, long userFollowersCount, long userFriendsCount, boolean userVerified, long userStatusesCount, long userListedCount) {

        this.userId = userId;
        this.userFollowersCount = userFollowersCount;
        this.userFriendsCount = userFriendsCount;
        this.userVerified = userVerified;
        this.userStatusesCount = userStatusesCount;
        this.userListedCount = userListedCount;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setUserFollowersCount(long userFollowersCount) {
        this.userFollowersCount = userFollowersCount;
    }

    public void setUserFriendsCount(long userFriendsCount) {
        this.userFriendsCount = userFriendsCount;
    }

    public void setUserVerified(boolean userVerified) {
        this.userVerified = userVerified;
    }

    public void setUserStatusesCount(long userStatusesCount) {
        this.userStatusesCount = userStatusesCount;
    }

    public void setUserListedCount(long userListedCount) {
        this.userListedCount = userListedCount;
    }

    public long getUserFollowersCount() {
        return userFollowersCount;
    }

    public long getUserFriendsCount() {
        return userFriendsCount;
    }

    public boolean isUserVerified() {
        return userVerified;
    }

    public long getUserStatusesCount() {
        return userStatusesCount;
    }

    public long getUserListedCount() {
        return userListedCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (userId != user.userId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (userId ^ (userId >>> 32));
    }
}
