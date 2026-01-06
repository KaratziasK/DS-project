package gr.hua.dit.ds.dsproject.repositories;

import gr.hua.dit.ds.dsproject.entities.Client;
import gr.hua.dit.ds.dsproject.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {

    Optional<Client> findByUser(User user);

    Optional<Client> findByUserUsername(String username);
}
