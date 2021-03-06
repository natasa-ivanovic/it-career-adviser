package ftn.sbnz.service;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ftn.sbnz.dto.job_offer.JobOfferDTO;
import ftn.sbnz.dto.user.JobSeekerDTO;
import ftn.sbnz.model.company.Company;
import ftn.sbnz.model.job_offer.JobOffer;
import ftn.sbnz.model.job_offer.JobOfferRating;
import ftn.sbnz.model.user.JobSeeker;
import ftn.sbnz.model.user.JobSeekerRanking;
import ftn.sbnz.repository.job_offer.JobOfferRatingRepository;
import ftn.sbnz.repository.job_offer.JobOfferRepository;
import ftn.sbnz.repository.user.JobSeekerRankingRepository;
import ftn.sbnz.repository.user.JobSeekerRepository;

@Service
@Transactional
public class JobOfferService {

	private JobOfferRepository repository;
	private JobSeekerRepository jobSeekerRepository;
	private JobSeekerRankingRepository jobSeekerRankingRepository;
	private JobOfferRatingRepository jobOfferRatingRepository;
	private KieSessionService kieSession;

	@Autowired
	public JobOfferService(JobOfferRepository repository, JobSeekerRepository jobSeekerRepository,
			JobSeekerRankingRepository jobSeekerRankingRepository, JobOfferRatingRepository jobOfferRatingRepository,
			KieSessionService kieSession) {
		this.repository = repository;
		this.jobSeekerRepository = jobSeekerRepository;
		this.jobSeekerRankingRepository = jobSeekerRankingRepository;
		this.jobOfferRatingRepository = jobOfferRatingRepository;
		this.kieSession = kieSession;
	}

	public JobOffer getOffer(Long id) {
		return repository.getOneById(id);
	}

	public void save(JobOffer jo) {
		this.repository.save(jo);
	}

	public Long follow(Long jobOfferRatingId, Long userId) throws Exception {
		JobOfferRating jor = jobOfferRatingRepository.getOne(jobOfferRatingId);
		JobOffer jo = jor.getJobOffer();
		JobSeeker js = jobSeekerRepository.getOne(userId);
		// check if already following that offer
		JobSeekerRanking ranking = jobSeekerRankingRepository.findOneByJobSeekerAndJobOffer(js, jo);
		if (ranking != null) {
			throw new Exception("Already following this job offer!");
		}
		JobSeekerRanking jsr = new JobSeekerRanking();
		jsr.setJobOffer(jo);
		jsr.setJobSeeker(js);
		jsr.setRanking(jor.getRating());
		jsr = jobSeekerRankingRepository.save(jsr);
		System.out.println("jobseekerRanking created with id " + jsr.getId());
//		js.getOfferRankings().add(jsr);
//		jo.getRankings().add(jsr);
//		jobSeekerRepository.save(js);
//		repository.save(jo);
		kieSession.insert(jsr);
		kieSession.setAgendaFocus("job-offer-status");
		kieSession.fireAllRules();
		updateDBFromRule(jo);
		System.out.println(
				"Successfully followed job offer: " + jo.getPosition().getTitle() + " in " + jo.getCompany().getName());
		return jsr.getId();
	}

	public void unfollow(Long jobOfferRatingId, Long userId) throws Exception {
		JobOfferRating jor = jobOfferRatingRepository.getOne(jobOfferRatingId);
		JobOffer jo = jor.getJobOffer();
		JobSeeker js = jobSeekerRepository.getOne(userId);
		// check if not following that offer
		JobSeekerRanking ranking = jobSeekerRankingRepository.findOneByJobSeekerAndJobOffer(js, jo);
		if (ranking == null) {
			throw new Exception("Not following this job offer!");
		}
		js.getOfferRankings().remove(ranking);
		jo.getRankings().remove(ranking);
		repository.save(jo);
		jobSeekerRepository.save(js);
		jobSeekerRankingRepository.deleteById(ranking.getId());
	}

	public List<JobSeekerDTO> getLeaderboard(Long jobOfferId) throws Exception {
		JobOffer jo = getOffer(jobOfferId);
		if (jo == null) {
			throw new Exception("Invalid job offer id!");
		}

		List<JobSeekerRanking> rankings = jo.getRankings().stream()
				.sorted((item1, item2) -> Long.compare(item2.getRanking(), item1.getRanking()))
				.collect(Collectors.toList());

		List<JobSeekerDTO> followers = rankings.stream().map(el -> new JobSeekerDTO(el.getJobSeeker()))
				.collect(Collectors.toList());

		return followers;
	}

	public List<JobOfferDTO> getFollowingOffers(Long jobSeekerId) {
		JobSeeker js = jobSeekerRepository.findOneById(jobSeekerId);
		List<JobOfferDTO> dto = js.getOfferRankings().stream().map(el -> new JobOfferDTO(el.getJobOffer()))
				.collect(Collectors.toList());
		return dto;
	}

	public void updateDBFromRule(JobOffer jobOfferDb) {
		Collection<Object> offers = kieSession.getObjectsFromSession(JobOffer.class);
		for (Iterator<Object> it = offers.iterator(); it.hasNext();) {
			JobOffer jo = (JobOffer) it.next();
			if (jo.getId() == jobOfferDb.getId()) {
				if (!jo.getMedal().equals(jobOfferDb.getMedal())) {
					jobOfferDb.setMedal(jo.getMedal());
					this.save(jo);
				}
				return;
			}
		}
	}

	public void updateDBFromRule(Company company) {
		Collection<Object> offers = kieSession.getObjectsFromSession(JobOffer.class);
		for (Iterator<Object> it = offers.iterator(); it.hasNext();) {
			JobOffer jo = (JobOffer) it.next();
			if (jo.getCompany().getId() == company.getId()) {
				if (!jo.getCompany().getMedal().equals(company.getMedal())) {
					JobOffer offerDb = getOffer(jo.getId());
					if (!offerDb.getMedal().equals(jo.getMedal())) {
						offerDb.setMedal(jo.getMedal());
						this.save(offerDb);
					}
				}
			}
		}
	}

	public List<JobOfferDTO> getAll() {
		List<JobOffer> offers = repository.findAll();
		return offers.stream().map(this::toDTO).collect(Collectors.toList());
	}

	private JobOfferDTO toDTO(JobOffer jo) {
		return new JobOfferDTO(jo);
	}

}
