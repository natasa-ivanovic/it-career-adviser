package ftn.sbnz.service;

import java.sql.Timestamp;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ftn.sbnz.dto.job_offer.JobOfferReviewDTO;
import ftn.sbnz.model.enums.ReviewStatus;
import ftn.sbnz.model.job_offer.JobOffer;
import ftn.sbnz.model.job_offer.JobOfferReview;
import ftn.sbnz.model.user.JobSeeker;
import ftn.sbnz.repository.job_offer.JobOfferReviewRepository;

@Service
public class JobOfferReviewService {

	private JobOfferReviewRepository repository;
	private JobOfferService offerService;
	private CompanyService companyService;
	private UserService userService;
	private KieSessionService kieSession;

	@Autowired
	public JobOfferReviewService(JobOfferReviewRepository repository, UserService userService, CompanyService companyService,
			JobOfferService offerService, KieSessionService kieSession) {
		this.repository = repository;
		this.userService = userService;
		this.kieSession = kieSession;
		this.companyService = companyService;
		this.offerService = offerService;
	}

	public void create(JobOfferReviewDTO dto, JobSeeker jobSeeker) {
		JobSeeker dbJobSeeker = (JobSeeker) userService.findByUsername(jobSeeker.getUsername());
		Calendar rightNow = Calendar.getInstance();
		JobOffer offer = offerService.getOffer(dto.getOfferId());
		JobOfferReview review = new JobOfferReview(dto, offer, dbJobSeeker, new Timestamp(rightNow.getTimeInMillis()));
		JobOfferReview created = repository.save(review);
		
		dbJobSeeker.getReviews().add(created);
		userService.save(dbJobSeeker);
	}
	
	public void approve(Long id) throws Exception {
		JobOfferReview review = repository.getOne(id);
		if (!review.getStatus().equals(ReviewStatus.PENDING))
			throw new Exception("Review already accepted or declined!");			
		review.setStatus(ReviewStatus.APPROVED);
		review = repository.save(review);

		executeSession(review);
	}

	public void decline(Long id) throws Exception {
		JobOfferReview review = repository.getOne(id);
		if (!review.getStatus().equals(ReviewStatus.PENDING))
			throw new Exception("Review already accepted or declined!");			
		review.setStatus(ReviewStatus.DECLINED);
		review = repository.save(review);
		
		executeSession(review);
	}
	
	private void executeSession(JobOfferReview review) {
		kieSession.insert(review);
		kieSession.setAgendaFocus("job-offer-status");
		kieSession.setAgendaFocus("company-status");
		kieSession.fireAllRules();
		
		companyService.updateDBFromRule(review.getJobOffer().getCompany());
		offerService.updateDBFromRule(review.getJobOffer());
	}
}