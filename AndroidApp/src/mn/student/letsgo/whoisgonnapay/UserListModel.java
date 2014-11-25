package mn.student.letsgo.whoisgonnapay;

public class UserListModel {
	
	public String nickname;
	public String icon;
	int userNumber;
 
    public int getUserNumber() {
		return userNumber;
	}

	public void setUserNumber(int userNumber) {
		this.userNumber = userNumber;
	}

	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getId() {
		return icon;
	}
	public void setId(String id) {
		this.icon = id;
	}

}
