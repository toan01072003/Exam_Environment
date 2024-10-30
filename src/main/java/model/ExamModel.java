package model;

import java.util.List;

public class ExamModel {
	private String name;
	private List<QuestionModel> questions;
	
	public ExamModel(String name, List<QuestionModel> questions) {
		super();
		this.name = name;
		this.questions = questions;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<QuestionModel> getQuestions() {
		return questions;
	}
	public void setQuestions(List<QuestionModel> questions) {
		this.questions = questions;
	}
	
}
