package ftn.sbnz.repository.job_offer;

import org.springframework.data.jpa.repository.JpaRepository;

import ftn.sbnz.model.job_offer.JobOffer;

public interface JobOfferRepository extends JpaRepository<JobOffer, Long> {

}