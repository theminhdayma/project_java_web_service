package com.data.project_web_service.utils;

import com.data.project_web_service.model.entity.User;
import com.data.project_web_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class DeleteUnverifiedUsers {

    @Autowired
    private UserRepository userRepository;

    @Scheduled(fixedRate = 60 * 60 * 1000)
    @Transactional
    public void deleteUnverifiedUsers() {
        LocalDate threshold = LocalDate.now().minusDays(1);
        List<User> usersToDelete = userRepository.findUnverifiedUsersBefore(threshold);

        for (User user : usersToDelete) {
            userRepository.delete(user);
        }
    }
}
