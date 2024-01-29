package com.lsh.mavikarga.service;

import com.lsh.mavikarga.domain.User;
import com.lsh.mavikarga.dto.ShowUserToAdminDto;
import com.lsh.mavikarga.dto.ShowUserToAdminDtoList;
import com.lsh.mavikarga.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@Transactional
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }

    // 회원이름으로 User 찾음
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(userRepository.findByUsername(username));
    }

    // Role='ROLE_USER' 인 유저 찾음
    public Page<User> findAllRoleUserPaged(Pageable pageable) {
        return userRepository.findAllByRole("ROLE_USER", pageable);
    }

    // ShowUserToAdminDtoList 생성
    public ShowUserToAdminDtoList createShowUserToAdminDtoList(int page, int size) {
        ShowUserToAdminDtoList showUserToAdminDtoList = new ShowUserToAdminDtoList();
        Page<User> allUsers = findAllRoleUserPaged(PageRequest.of(page, size, Sort.by("username").ascending()));

        // 전체 페이지 수
        showUserToAdminDtoList.setTotalPages(allUsers.getTotalPages());

        // ShowUserToAdminDto 생성
        for (User user : allUsers) {
//            log.info("user = {}, {}, {}", user.getId(), user.getUsername(), user.getEmail());
            ShowUserToAdminDto showUserToAdminDto = new ShowUserToAdminDto(user.getId(), user.getUsername(), user.getEmail(), user.getCreatedTime());
            showUserToAdminDtoList.getShowUserToAdminDtoList().add(showUserToAdminDto);
        }

        return showUserToAdminDtoList;
    }

}
