package hr.fer.zemris.opp.giger.domain;

import javax.persistence.*;
import java.util.List;

import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;

@Entity
public class Organizer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String managerName; //todo think about it

    @OneToMany(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "organizer_id")
    private List<Gig> history;

    @ManyToMany(fetch = LAZY, cascade = ALL)
    @JoinTable(name = "review_organizer",
            joinColumns = {@JoinColumn(name = "fk_organizer")},
            inverseJoinColumns = {@JoinColumn(name = "fk_review")})
    private List<Review> reviews;

    public Organizer() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public List<Gig> getHistory() {
        return history;
    }

    public void setHistory(List<Gig> history) {
        this.history = history;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
