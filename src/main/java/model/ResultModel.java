package model;

public class ResultModel {
	private int id;
	private String examId;
	private String userId;
	private Double score;
	public ResultModel(int id, String examId, String userId, Double score) {
		super();
		this.id = id;
		this.examId = examId;
		this.userId = userId;
		this.score = score;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getExamId() {
		return examId;
	}
	public void setExamId(String examId) {
		this.examId = examId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Double getScore() {
		return score;
	}
	public void setScore(Double score) {
		this.score = score;
	}
}
