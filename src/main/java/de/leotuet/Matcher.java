package de.leotuet;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.leotuet.models.Group;
import de.leotuet.models.TutoringOffer;
import de.leotuet.models.TutoringOfferTimeSlot;
import de.leotuet.models.TutoringRequest;
import de.leotuet.models.TutoringRequestTimeSlot;
import de.leotuet.repository.GroupRepository;
import de.leotuet.repository.GroupStudentRepository;
import de.leotuet.repository.TutoringOfferRepository;
import de.leotuet.repository.TutoringOfferTimeSlotRepository;
import de.leotuet.repository.TutoringRequestRepository;
import de.leotuet.repository.TutoringRequestTimeSlotRepository;

public class Matcher {
	private final TutoringOfferRepository tutoringOfferRepository;
	private final TutoringOfferTimeSlotRepository tutoringOfferTimeSlotRepository;
	private final TutoringRequestRepository tutoringRequestRepository;
	private final TutoringRequestTimeSlotRepository tutoringRequestTimeSlotRepository;
	private final GroupRepository groupRepository;
	private final GroupStudentRepository groupStudentRepository;

	public Matcher(Connection conn) {
		this.tutoringOfferRepository = new TutoringOfferRepository(conn);
		this.tutoringOfferTimeSlotRepository = new TutoringOfferTimeSlotRepository(conn);
		this.tutoringRequestRepository = new TutoringRequestRepository(conn);
		this.tutoringRequestTimeSlotRepository = new TutoringRequestTimeSlotRepository(conn);
		this.groupRepository = new GroupRepository(conn);
		this.groupStudentRepository = new GroupStudentRepository(conn);
	}

	public void match() {
		groupStudentRepository.deleteAll();
		groupRepository.deleteAll();

		HashMap<String, List<TutoringOffer>> tutoringOffersMap = new HashMap<>();
		HashMap<String, List<TutoringRequest>> tutoringRequestsMap = new HashMap<>();

		tutoringRequestsMap.put("MathematikTechnik",
				tutoringRequestRepository.getAllBySpecializationAndSubject("t", "Mathematik"));
		tutoringOffersMap.put("MathematikTechnik",
				tutoringOfferRepository.getAllBySpecializationAndSubject("t", "Mathematik"));

		tutoringRequestsMap.put("Mathematik",
				tutoringRequestRepository.getAllExcludingSpecializationAndIncludingSubject("t",
						"Mathematik"));
		tutoringOffersMap.put("Mathematik",
				tutoringOfferRepository.getAllExcludingSpecializationAndIncludingSubject("t",
						"Mathematik"));

		var subjects = new ArrayList<>(List.of("Physik", "Deutsch", "Englisch", "Gesundheit", "PäPsy", "BWR"));

		for (String subject : subjects) {
			tutoringRequestsMap.put(subject, tutoringRequestRepository.getAllBySubject(subject));
			tutoringOffersMap.put(subject, tutoringOfferRepository.getAllBySubject(subject));
		}

		subjects.add("Mathematik");
		subjects.add("MathematikTechnik");

		HashMap<Integer, Integer> tutorAvailability = tutoringOfferRepository.getAllTutoringOfferCounts();

		while (!subjects.isEmpty()) {

			int leastOffers = Integer.MAX_VALUE;
			String leastSubject = "";

			for (String subject : subjects) {
				if (tutoringOffersMap.get(subject).size() < leastOffers) {
					leastOffers = tutoringOffersMap.get(subject).size();
					leastSubject = subject;
				}
			}

			List<TutoringOffer> tutoringOffers = tutoringOffersMap.get(leastSubject);
			tutoringOffers.sort((o1, o2) -> {
				int o1Count = tutorAvailability.get(o1.getTutorId());
				int o2Count = tutorAvailability.get(o2.getTutorId());
				return Integer.compare(o1Count, o2Count);
			});

			List<TutoringRequest> tutoringRequests = tutoringRequestsMap.get(leastSubject);

			if (tutoringOffers.isEmpty() || tutoringRequests.isEmpty()) {
				subjects.remove(leastSubject);
				continue;
			}

			while (!tutoringOffers.isEmpty()) {
				TutoringOffer tutoringOffer = tutoringOffers.remove(0);

				List<TutoringOfferTimeSlot> tutoringOfferTimeSlot = tutoringOfferTimeSlotRepository
						.getByOfferId(tutoringOffer.getId());

				if (tutoringOfferTimeSlot.isEmpty()) {
					continue;
				}

				HashMap<Integer, List<TutoringRequest>> matchedRequests = new HashMap<>();
				for (TutoringRequest tutoringRequest : tutoringRequests) {
					List<TutoringRequestTimeSlot> tutoringRequestTimeSlots = tutoringRequestTimeSlotRepository
							.getByRequestId(tutoringRequest.getId());

					if (tutoringRequestTimeSlots.isEmpty()) {
						continue;
					}

					for (TutoringOfferTimeSlot offerTimeSlot : tutoringOfferTimeSlot) {
						for (TutoringRequestTimeSlot requestTimeSlot : tutoringRequestTimeSlots) {
							if (offerTimeSlot.getTimeSlotId() == requestTimeSlot.getTimeSlotId()) {
								if (matchedRequests.containsKey(offerTimeSlot.getTimeSlotId())) {
									matchedRequests.get(offerTimeSlot.getTimeSlotId()).add(tutoringRequest);
								} else {
									matchedRequests.put(offerTimeSlot.getTimeSlotId(), new ArrayList<>(List.of(tutoringRequest)));
								}
							}
						}
					}
				}

				if (matchedRequests.isEmpty()) {
					continue;
				}

				int key = Collections
						.max(matchedRequests.entrySet(), (entry1, entry2) -> entry1.getValue().size() - entry2.getValue().size())
						.getKey();

				List<TutoringRequest> matchedRequestsList = matchedRequests.get(key);

				if (matchedRequestsList == null || matchedRequestsList.size() < 2) {
					continue;
				}

				Group group = groupRepository.create(tutoringOffer.getId(), key);
				int groupMemberCount = 0;

				List<TutoringOffer> tutorOffers = new ArrayList<>();
				for (TutoringOffer offer : tutoringOffers) {
					if (offer.getTutorId() == tutoringOffer.getTutorId()) {
						tutorOffers.add(offer);
					}
				}

				for (TutoringOffer offer : tutorOffers) {
					tutoringOffers.remove(offer);
				}

				while (groupMemberCount < 4 && !matchedRequestsList.isEmpty()) {
					TutoringRequest tutoringRequest = matchedRequestsList.remove(0);
					tutoringRequests.remove(tutoringRequest);
					groupStudentRepository.create(group.getId(), tutoringRequest.getId());
					groupMemberCount++;
				}
			}
		}
	}
}
