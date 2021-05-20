package ftn.sbnz.model.technology;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import ftn.sbnz.model.enums.SkillProficiency;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "technology_importances")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class TechnologyImportance {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "optional", unique = false, nullable = false)
	private boolean optional;
	
	@Column(name = "proficiency", unique = false, nullable = false)
	@NonNull
	private SkillProficiency proficiency;

	@Column(name = "importance", unique = false, nullable = false)
	private int importance;
	
	@ManyToOne
	private Technology technology;
}