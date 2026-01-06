package gr.hua.dit.ds.dsproject.repositories;

import gr.hua.dit.ds.dsproject.entities.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Integer> {
    List<Request> findByProjectId(Integer projectId);
    Optional<Request> findByProjectIdAndFreelancerId(int projectId, int freelancerId);

}