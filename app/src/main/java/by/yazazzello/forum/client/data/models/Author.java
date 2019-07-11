package by.yazazzello.forum.client.data.models;

/**
 *
 */

public class Author {
    private final int userId;
    private final String nick;
    private final String lvl;
    private final String imgSrc;

    public long getUserId() {
        return userId;
    }

    public String getNick() {
        return nick;
    }

    public String getLvl() {
        return lvl;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public Author(String userId, String nick, String lvl, String imgSrc) {
        this.userId = Integer.valueOf(userId);
        this.nick = nick;
        this.lvl = lvl;
        this.imgSrc = imgSrc;
    }

    @Override
    public String toString() {
        return "Author{" +
                "userId=" + userId +
                ", nick='" + nick + '\'' +
                ", lvl='" + lvl + '\'' +
                ", imgSrc='" + imgSrc + '\'' +
                '}';
    }
}
