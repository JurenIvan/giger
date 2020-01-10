package hr.fer.zemris.opp.giger.web.rest.controller;

import hr.fer.zemris.opp.giger.service.BandService;
import hr.fer.zemris.opp.giger.web.rest.dto.*;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/band-administration")
@AllArgsConstructor
@PreAuthorize("hasAuthority('MUSICIAN')")
public class BandAdministrationController {

	private BandService bandService;

	@PostMapping("/create")
	public void createBand(@Valid @RequestBody BandCreationDto bandCreationDto) {
		bandService.createBand(bandCreationDto);
	}

	@PostMapping("/invite")
	public void inviteUser(@Valid @RequestBody MusicianBandDto musicianBandDto) {
		bandService.inviteMusician(musicianBandDto);
	}

	@PostMapping("/invite-as-backup")
	public void inviteBackUpMusician(@Valid @RequestBody MusicianBandDto musicianBandDto) {
		bandService.inviteBackUpMusician(musicianBandDto);
	}

	@GetMapping("/join/{bandId}")
	public void joinBand(@PathVariable Long bandId) {
		bandService.joinBand(bandId);
	}

	@GetMapping("/leave/{bandId}")
	public void leaveBand(@PathVariable Long bandId) {
		bandService.leaveBand(bandId);
	}

	@PostMapping("/kick")
	public void kickMusician(@Valid  @RequestBody KickDto kickDto) {
		bandService.kickMusician(kickDto);
	}

	@PostMapping("/edit")
	public void editProfile(@Valid @RequestBody BandEditProfileDto bandEditProfileDto) {
		bandService.editProfile(bandEditProfileDto);
	}

	@PostMapping("/change-leader")
	public void changeLeader(@Valid @RequestBody MusicianBandDto musicianBandDto) {
		bandService.changeLeader(musicianBandDto);
	}

	@GetMapping("/invites/{bandId}")
	public List<MusicianInvitationsDto> listInvitations(@PathVariable Long bandId) {
		return bandService.listInvitations(bandId, 0);
	}

	@GetMapping("/back-up-invites/{bandId}")
	public List<MusicianInvitationsDto> listBackUpInvitations(@PathVariable Long bandId) {
		return bandService.listInvitations(bandId, 1);
	}
}
