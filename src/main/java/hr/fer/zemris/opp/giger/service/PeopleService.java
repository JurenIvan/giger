package hr.fer.zemris.opp.giger.service;

import hr.fer.zemris.opp.giger.config.errorHandling.GigerException;
import hr.fer.zemris.opp.giger.config.security.model.RegisterRequestDto;
import hr.fer.zemris.opp.giger.domain.Band;
import hr.fer.zemris.opp.giger.domain.Person;
import hr.fer.zemris.opp.giger.domain.Role;
import hr.fer.zemris.opp.giger.domain.SystemPerson;
import hr.fer.zemris.opp.giger.repository.BandRepository;
import hr.fer.zemris.opp.giger.repository.PersonRepository;
import hr.fer.zemris.opp.giger.repository.SystemPersonRepository;
import hr.fer.zemris.opp.giger.web.rest.dto.FindUsersDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.List;

import static hr.fer.zemris.opp.giger.config.errorHandling.ErrorCode.NO_SUCH_USER_EXCEPTION;

@Service
public class PeopleService {

    private EmailSender emailSender;
    private SystemPersonRepository systemPersonRepository;
    private PersonRepository peopleRepository;
    private BandRepository bandRepository;

    public PeopleService(EmailSender emailSender, SystemPersonRepository systemPersonRepository, PersonRepository peopleRepository, BandRepository bandRepository) {
        this.emailSender = emailSender;
        this.systemPersonRepository = systemPersonRepository;
        this.peopleRepository = peopleRepository;
        this.bandRepository = bandRepository;
    }

    @Value("${spring.security.security.BCrypt.secret}")
    private String SECRET_KEY;

    public boolean isUserNameAvailable(String username) {
        return peopleRepository.findByUsername(username).isEmpty();
    }

    public boolean isEmailAvailable(String email) {
        return systemPersonRepository.findByEmail(email).isEmpty();
    }

    public void saveUser(RegisterRequestDto registerRequestDto) throws Exception {
        if (!isEmailAvailable(registerRequestDto.getEmail())) {
            throw new Exception("email not available");
        }
        if (!isUserNameAvailable(registerRequestDto.getUsername())) {
            throw new Exception("username not available");
        }
        SystemPerson systemPerson = new SystemPerson();
        systemPerson.setEmail(registerRequestDto.getEmail());
        systemPerson.setPasswordHash(BCrypt.hashpw(registerRequestDto.getPassword(), BCrypt.gensalt(10)));
        systemPerson.setVerified(false);
        systemPerson.setLocked(false);
        systemPerson.addRole(Role.PERSON);
        systemPerson = systemPersonRepository.save(systemPerson);

        Person person = new Person();
        person.setId(systemPerson.getId());
        person.setPhoneNumber(registerRequestDto.getPhoneNumber());
        person.setUsername(registerRequestDto.getUsername());
        peopleRepository.save(person);
    }

    public boolean verifyEmail(String username, String token) {
        Person person = peopleRepository.findByUsername(username).orElseThrow(() -> new GigerException(NO_SUCH_USER_EXCEPTION));
        SystemPerson systemPerson = systemPersonRepository.findById(person.getId()).get();

        if (systemPerson.getVerified() != null && systemPerson.getVerified() == true)
            return true;
        if (systemPerson.getEmail().hashCode() == Integer.parseInt(token))
            systemPerson.setVerified(true);
        return systemPersonRepository.save(systemPerson).getVerified();
    }

    public void sendEmail(String email, String username) {
        emailSender.sendRegistrationConfirmationMessage(email, email.hashCode(), username);
    }

    public List<Person> findUsers(FindUsersDto findUsersDto) {
        List<Person> usersWithSimilarName = peopleRepository.findAllByUsernameLike(findUsersDto.getName());
        List<Band> bandsWithSimilarName = bandRepository.findAllByNameLike(findUsersDto.getName());
        //List<Musician> musiciansFromBands = musicianRepository.findAllByBandsIn(bandsWithSimilarName);

        //     usersWithSimilarName.addAll(musiciansFromBands.stream().map(Musician::getUser).collect(Collectors.toList()));
        return usersWithSimilarName;
    }
}