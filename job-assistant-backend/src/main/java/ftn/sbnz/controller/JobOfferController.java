package ftn.sbnz.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ftn.sbnz.dto.job_offer.JobOfferDTO;
import ftn.sbnz.dto.user.JobSeekerDTO;
import ftn.sbnz.model.user.JobSeeker;
import ftn.sbnz.service.JobOfferService;

@RestController
@Validated
@RequestMapping(value = "/api/job-offer", produces = MediaType.APPLICATION_JSON_VALUE)
public class JobOfferController {
	
	private JobOfferService service;
	
	@Autowired
	public JobOfferController(JobOfferService service) {
		this.service = service;
	}
	
	@PostMapping("/follow/{id}")
	@PreAuthorize("hasRole('ROLE_USER')")
	public ResponseEntity<Object> follow(@PathVariable("id") String jobOfferRatingId) throws Exception {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		JobSeeker jobSeeker = (JobSeeker) auth.getPrincipal();
		Long position = service.follow(Long.parseLong(jobOfferRatingId), jobSeeker.getId());
		return new ResponseEntity<>(position, HttpStatus.OK);
	}
	
	@PostMapping("/unfollow/{id}")
	@PreAuthorize("hasRole('ROLE_USER')")
	public ResponseEntity<Object> unfollow(@PathVariable("id") String jobOfferRatingId) throws Exception {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		JobSeeker jobSeeker = (JobSeeker) auth.getPrincipal();
		service.unfollow(Long.parseLong(jobOfferRatingId), jobSeeker.getId());
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@GetMapping
	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	public ResponseEntity<List<JobOfferDTO>> getAll() {
		List<JobOfferDTO> dtos = service.getAll();
		return new ResponseEntity<>(dtos, HttpStatus.OK);
	}
	
	@GetMapping("/leaderboard/{id}")
	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	public ResponseEntity<List<JobSeekerDTO>> getLeaderboard(@PathVariable("id") String jobOfferId) throws Exception {
		List<JobSeekerDTO> dto = service.getLeaderboard(Long.parseLong(jobOfferId));
		return new ResponseEntity<>(dto, HttpStatus.OK);
	}
	
	@GetMapping("/following")
	@PreAuthorize("hasRole('ROLE_USER')")
	public ResponseEntity<List<JobOfferDTO>> getFollowingOffers() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		JobSeeker jobSeeker = (JobSeeker) auth.getPrincipal();
		List<JobOfferDTO> dto = service.getFollowingOffers(jobSeeker.getId());
		return new ResponseEntity<>(dto, HttpStatus.OK);
	}

}
