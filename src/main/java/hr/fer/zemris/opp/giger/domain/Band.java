package hr.fer.zemris.opp.giger.domain;

import hr.fer.zemris.opp.giger.domain.enums.GigType;
import hr.fer.zemris.opp.giger.domain.exception.ErrorCode;
import hr.fer.zemris.opp.giger.domain.exception.GigerException;
import hr.fer.zemris.opp.giger.web.rest.dto.BandCreationDto;
import hr.fer.zemris.opp.giger.web.rest.dto.BandDto;
import hr.fer.zemris.opp.giger.web.rest.dto.BandEditProfileDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static hr.fer.zemris.opp.giger.domain.exception.ErrorCode.NO_SUCH_MUSICIAN;
import static java.time.LocalDateTime.now;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Band {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@NotNull
	@Column(unique = true)
	private String name;
	private String bio;
	@NotNull
	private LocalDateTime formedDate;
	@Length(max = 10000)
	private String pictureUrl;
	private Location home;
	private double maxDistance;

	//todo members
	@ManyToOne(fetch = LAZY)
	private Musician leader;

	@ManyToMany(fetch = LAZY)
	private List<Musician> members;

	@ManyToMany(fetch = LAZY)
	private List<Musician> backUpMembers;

	@ManyToMany(fetch = LAZY)
	private List<Musician> invited;

	@ManyToMany(fetch = LAZY)
	private List<Musician> invitedBackUpMembers;

	@OneToMany(fetch = LAZY)
	@JoinColumn(name = "fk_band")
	private List<Post> posts;

	@ElementCollection(targetClass = GigType.class)
	@CollectionTable(name = "gig_type", joinColumns = @JoinColumn(name = "band_id"))
	@Column(name = "gig_type", nullable = false)
	@Enumerated(EnumType.STRING)
	private List<GigType> acceptableGigTypes;

	@OneToMany
	private List<Occasion> occasions;

	@ManyToMany(fetch = LAZY)
	private List<Gig> gigs;

	@ManyToMany(fetch = LAZY)
	private List<Gig> invitationGigs;


	public static Band createBand(BandCreationDto bandCreationDto, Musician loggedMusician) {
		Band band = new Band();

		band.setName(bandCreationDto.getName());
		band.setAcceptableGigTypes(bandCreationDto.getAcceptableGigTypes());
		band.setBio(bandCreationDto.getBio());
		band.setFormedDate(now().withNano(0));
		band.setHome(bandCreationDto.getHomeLocation());
		band.setPictureUrl(bandCreationDto.getPictureUrl());
		band.setLeader(loggedMusician);
		band.setMembers(newArrayList(loggedMusician));

		return band;
	}

	public void addMember(Musician musician) {
		members.add(musician);
		invited.remove(musician);
		backUpMembers.remove(musician);
	}

	public void inviteMember(Musician musician) {
		invited.add(musician);
	}


	public void inviteBackupMember(Musician musician) {
		invitedBackUpMembers.add(musician);
	}

	public void addBackUpMember(Musician musician) {
		backUpMembers.add(musician);
		invitedBackUpMembers.remove(musician);
	}

	public void removeMember(Long musicianId) {
		Optional<Musician> member = members.stream().filter(musician -> musician.getId().equals(musicianId)).findFirst();
		Optional<Musician> backUpMember = backUpMembers.stream().filter(musician -> musician.getId().equals(musicianId)).findFirst();

		if (member.isEmpty() && backUpMember.isEmpty())
			throw new GigerException(ErrorCode.NO_SUCH_MUSICIAN_IN_BAND);

		member.ifPresent(musician -> members.remove(musician));
		backUpMember.ifPresent(musician -> backUpMembers.remove(musician));
	}

	public void editProfile(BandEditProfileDto bandEditProfileDto) {
		if (bandEditProfileDto.getBio() != null) {
			this.bio = bandEditProfileDto.getBio();
		}
		if (bandEditProfileDto.getPictureUrl() != null) {
			this.pictureUrl = bandEditProfileDto.getPictureUrl();
		}
		if (bandEditProfileDto.getLocation() != null) {
			this.home = bandEditProfileDto.getLocation();
		}
		if (bandEditProfileDto.getMaxDistance() != null) {
			this.maxDistance = bandEditProfileDto.getMaxDistance();
		}
		removePost(bandEditProfileDto.getRemovePostIds());
	}

	private void removePost(List<Long> ids) {
		if (ids == null) return;
		HashMap<Long, Post> remaining = new HashMap<>();
		this.posts.forEach(e -> remaining.put(e.getId(), e));
		ids.forEach(remaining::remove);
		this.posts = new ArrayList<>(remaining.values());
	}

	public BandDto toDto() {
		return new BandDto(id, name, bio, pictureUrl, acceptableGigTypes);
	}

	public void removeBackUpMember(Long musicianId) {
		backUpMembers.remove(backUpMembers.stream().filter(musician -> musician.getId().equals(musicianId)).findFirst().orElseThrow(() -> new GigerException(NO_SUCH_MUSICIAN)));
	}

	public void addPost(Post post) {
		this.posts.add(post);
	}

	public void acceptGig(Occasion occasion, Gig gig) {
		invitationGigs.remove(gig);
		gigs.add(gig);
		occasions.add(occasion);
	}

	public void addInvitation(Gig gig) {
		invitationGigs.add(gig);
	}

	public void cancelGig(Gig gig) {
		invitationGigs.remove(gig);
	}
}