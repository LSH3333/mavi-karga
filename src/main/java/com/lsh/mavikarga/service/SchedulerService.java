package com.lsh.mavikarga.service;

import com.lsh.mavikarga.domain.User;
import com.lsh.mavikarga.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class SchedulerService {

    private final UserRepository userRepository;

    public SchedulerService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 매일 자정에 마지막 로그인한지 6개월 지난 유저 데이터 삭제
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void deleteOldUserInformation() {
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
        List<User> usersToDelete = userRepository.findUsersWithLastLoggedInOlderThanSixMonths(sixMonthsAgo);

        for (User user : usersToDelete) {
            user.delete();
            userRepository.save(user);
        }

    }

//    @Scheduled(cron = "0 0/1 * * * ?") // Run every minute
//    @Transactional
//    public void deleteOldUserInformationTest() {
//        log.info("deleteOldUserInformationTest");
//        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
//        List<User> usersToDelete = userRepository.findUsersWithLastLoggedInOlderThanSixMonths(oneMinuteAgo);
//
//        for (User user : usersToDelete) {
//            log.info("scheduler user = {}", user.getUsername());
//            user.delete();
//            userRepository.save(user);
//        }
//    }

}
