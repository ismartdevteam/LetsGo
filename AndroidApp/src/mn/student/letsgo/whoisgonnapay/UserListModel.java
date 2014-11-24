package mn.student.letsgo.whoisgonnapay;

public class UserListModel {
	
	public String nickname;
	public int icon;
	int userNumber;
 
    public int getUserNumber() {
		return userNumber;
	}

	public void setUserNumber(int userNumber) {
		this.userNumber = userNumber;
	}

	public UserListModel() {
    }
 
    public UserListModel(String name, int id) {
        this.nickname = name;
        this.icon = id;
    }
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public int getId() {
		return icon;
	}
	public void setId(int id) {
		this.icon = id;
	}

}
