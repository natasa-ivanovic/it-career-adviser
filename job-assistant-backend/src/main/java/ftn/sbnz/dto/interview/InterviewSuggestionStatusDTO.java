package ftn.sbnz.dto.interview;

import java.util.Date;

import ftn.sbnz.model.enums.CVElementType;
import ftn.sbnz.model.enums.SkillProficiency;
import ftn.sbnz.model.interview.InterviewSuggestionStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InterviewSuggestionStatusDTO {
	
	private Long id;
	private boolean checked;
	private Date dateSuggested;
	private Date dateChecked;
	private Long interviewSuggestionId;
	private SkillProficiency proficiency;
	private int proficiencyValue;
	private CVElementType cvElement;
	private String subject;
	private String url;
	private String description;
	private boolean forbiddenMaterial;
//	private InterviewSuggestionDTO interviewSuggestion;
		
	public InterviewSuggestionStatusDTO(InterviewSuggestionStatus status) {
		this.id = status.getId();
		this.checked = status.isChecked();
		this.dateSuggested = status.getDateSuggested();
		this.dateChecked = this.checked ? status.getDateChecked() : null;
		this.interviewSuggestionId = status.getInterviewSuggestion().getId();
		this.proficiency = status.getInterviewSuggestion().getCvElementProficiency().getProficiency();
		this.proficiencyValue = status.getInterviewSuggestion().getCvElementProficiency().getProficiency().getValue();
		this.cvElement = status.getInterviewSuggestion().getCvElementProficiency().getCvElement().getType();
		this.subject = status.getInterviewSuggestion().getSubject();
		this.url = status.getInterviewSuggestion().getUrl();
		this.description = status.getInterviewSuggestion().getDescription();
	}
	
	public InterviewSuggestionStatusDTO(InterviewSuggestionStatus status, boolean forbiddenMaterial) {
		this.id = status.getId();
		this.checked = status.isChecked();
		this.dateSuggested = status.getDateSuggested();
		this.dateChecked = this.checked ? status.getDateChecked() : null;
		this.interviewSuggestionId = status.getInterviewSuggestion().getId();
		this.proficiency = status.getInterviewSuggestion().getCvElementProficiency().getProficiency();
		this.proficiencyValue = status.getInterviewSuggestion().getCvElementProficiency().getProficiency().getValue() - 1;
		this.cvElement = status.getInterviewSuggestion().getCvElementProficiency().getCvElement().getType();
		this.subject = status.getInterviewSuggestion().getSubject();
		this.url = status.getInterviewSuggestion().getUrl();
		this.description = status.getInterviewSuggestion().getDescription();
		this.forbiddenMaterial = forbiddenMaterial;
	}
}
