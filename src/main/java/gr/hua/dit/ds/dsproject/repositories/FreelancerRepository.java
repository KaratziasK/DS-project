package gr.hua.dit.ds.dsproject.repositories;

import gr.hua.dit.ds.dsproject.entities.Freelancer;
import gr.hua.dit.ds.dsproject.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FreelancerRepository extends JpaRepository<Freelancer, Integer> {

    Optional<Freelancer> findByUserUsername(String username);

    Optional<Freelancer> findByUser(User user);
}
