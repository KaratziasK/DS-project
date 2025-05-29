package gr.hua.dit.ds.dsproject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendSignupEmailToClient(String to, String name) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Καλωσήρθες στην CollabWorks!");
        message.setText("Αγαπητέ/ή " + name + ",\n\n" +
                "Η εγγραφή σου στην CollabWorks ως Πελάτης ολοκληρώθηκε με επιτυχία!\n" +
                "Μπορείς να βρεις και να συνεργαστείς με κορυφαίους freelancers.\n\n" +
                "Καλή αρχή,\nΗ ομάδα της CollabWorks");
        mailSender.send(message);
    }

    public void sendSignupEmailToFreelancer(String to, String name) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Καλωσήρθες στην CollabWorks!");
        message.setText("Αγαπητέ/ή " + name + ",\n\n" +
                "Η εγγραφή σου στην CollabWorks ως Freelancer ολοκληρώθηκε με επιτυχία!\n" +
                "Ξεκίνα να βρίσκεις projects και νέες συνεργασίες άμεσα.\n\n" +
                "Καλή αρχή,\nΗ ομάδα της CollabWorks");
        mailSender.send(message);
    }
}
