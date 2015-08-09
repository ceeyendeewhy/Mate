package cindyliu96.test;

/**
 * Created by yunjie on 7/25/2015.
 */
public class User {
    private int id;
    private String name;
    private String nickname;
    private String dob;

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getDob() {
        return this.dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    @Override
    public String toString() {
        return name;
    }
}
