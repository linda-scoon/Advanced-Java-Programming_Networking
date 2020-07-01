import java.io.Serializable;

/**
 * @author Flourish
 *
 */
public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	private String userName;
	private String password;

	/**
	 * 
	 * @param userName
	 * @param password
	 */
	public User(String userName, String password) {
		this.userName = userName;
		this.password = password;
	}

	/**
	 * @return the userName
	 */
	public synchronized String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the password
	 */
	public synchronized String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the valid
	 */
	public boolean isValidUserName(String userName) {
		return (this.userName.equals(userName));
	}

	/**
	 * @return the valid
	 */
	public boolean isValidPassword(String password) {
		return (this.password.equals(password));
	}

	/**
	 * String rep of class
	 */
	@Override
	public String toString() {
		return "User [userName=" + userName + ", password=" + password + "]";
	}

	}
