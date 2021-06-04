package ftn.sbnz.model.job_offer;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import ftn.sbnz.model.user.JobSeeker;
import lombok.Data;
import lombok.NonNull;

@Entity
@Table(name = "job_offer_statistic")
@Data
public class JobOfferStatistic {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "date")
	@NonNull
	private Timestamp date;
	
	@OneToMany(cascade=CascadeType.ALL)
	private List<JobOfferDifference> jobOfferDifferences = new ArrayList<>();
	
	@ManyToOne
	private JobOffer jobOffer;
	
	@ManyToOne
	private JobSeeker jobSeeker;
	
}