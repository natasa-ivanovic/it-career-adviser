package ftn.sbnz.dto.job_offer;

import ftn.sbnz.model.job_offer.JobOfferRating;
import ftn.sbnz.model.user.JobSeeker;
import ftn.sbnz.model.user.JobSeekerRanking;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JobOfferRatingDTO {

	private Long id;
	private String position;
	private String company;
	private int rating;
	private String description;
	private String category;
	private Long jobOfferId;
	private JobOfferDTO jobOffer;
	private boolean following;
	
	public JobOfferRatingDTO(JobOfferRating jobOfferRating, JobSeeker js, boolean following) {
		this.id = jobOfferRating.getId();
		this.jobOfferId = jobOfferRating.getJobOffer().getId();
		this.jobOffer = new JobOfferDTO(jobOfferRating.getJobOffer());
		this.position = jobOfferRating.getJobOffer().getPosition().getTitle();
		this.company = jobOfferRating.getJobOffer().getCompany().getName();
		this.description = jobOfferRating.getDescription();
		this.rating = jobOfferRating.getRating();
		this.category = jobOfferRating.getCategory().name();
		this.following = following;
	}

}
