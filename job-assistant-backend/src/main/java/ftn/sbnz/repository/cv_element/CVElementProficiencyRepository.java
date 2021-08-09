package ftn.sbnz.repository.cv_element;

import org.springframework.data.jpa.repository.JpaRepository;

import ftn.sbnz.model.cv_element.CVElement;
import ftn.sbnz.model.cv_element.CVElementProficiency;
import ftn.sbnz.model.enums.SkillProficiency;

public interface CVElementProficiencyRepository extends JpaRepository<CVElementProficiency, Long> {
	
	public CVElementProficiency findOneByCvElementAndProficiency(CVElement cvElement, SkillProficiency proficiency);
}