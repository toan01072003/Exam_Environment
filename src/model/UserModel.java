package model;

public class UserModel {
	private int id;
	private String username;
	private String pwd;
	private String email;
	private String name;
	private String gender;
	private String status;
	private String dob;
	private int role;
	public UserModel(int id, String username, String pwd, String email, String name, String gender, String status,
			String dob, int role) {
		super();
		this.id = id;
		this.username = username;
		this.pwd = pwd;
		this.email = email;
		this.name = name;
		this.gender = gender;
		this.status = status;
		this.dob = dob;
		this.role = role;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDob() {
		return dob;
	}
	public void setDob(String dob) {
		this.dob = dob;
	}
	public int getRole() {
		return role;
	}
	public void setRole(int role) {
		this.role = role;
	}
	
}
