package huce.nguyentoan.job4u.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import huce.nguyentoan.job4u.domain.User;
import huce.nguyentoan.job4u.domain.dto.ResCreateUserDTO;
import huce.nguyentoan.job4u.domain.dto.ResUpdateUserDTO;
import huce.nguyentoan.job4u.domain.dto.ResUserDTO;
import huce.nguyentoan.job4u.domain.dto.ResultPaginationDTO;
import huce.nguyentoan.job4u.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User fetchUserById(long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        return null;
    }

    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO resUser = new ResUserDTO();
        resUser.setId(user.getId());
        resUser.setName(user.getName());
        resUser.setEmail(user.getEmail());
        resUser.setGender(user.getGender());
        resUser.setAddress(user.getAddress());
        resUser.setAge(user.getAge());
        resUser.setCreatedAt(user.getCreatedAt());
        resUser.setUpdatedAt(user.getUpdatedAt());
        return resUser;
    }

    public ResultPaginationDTO fetchAllUser(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pageUser.getContent());

        //remove sensitive data
        List<ResUserDTO> listUser = pageUser.getContent()
                .stream().map(item -> new ResUserDTO(
                    item.getId(),
                    item.getName(),
                    item.getEmail(),
                    item.getGender(),
                    item.getAddress(),
                    item.getAge(),
                    item.getCreatedAt(),
                    item.getUpdatedAt()))
                .collect(Collectors.toList());
        rs.setResult(listUser);
        return rs;
    }

    public User handleCreateUser(User user){
        return this.userRepository.save(user);
    }

    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO resUser = new ResCreateUserDTO();
        resUser.setId(user.getId());
        resUser.setName(user.getName());
        resUser.setEmail(user.getEmail());
        resUser.setGender(user.getGender());
        resUser.setAddress(user.getAddress());
        resUser.setAge(user.getAge());
        resUser.setCreatedAt(user.getCreatedAt());
        return resUser;
    }

    public User handleUpdateUser(User reqUser) {
        User currentUser = this.fetchUserById(reqUser.getId());
        if (currentUser != null) {
            currentUser.setAddress(reqUser.getAddress());
            currentUser.setGender(reqUser.getGender());
            currentUser.setName(reqUser.getName());
            currentUser.setAge(reqUser.getAge());
            currentUser = this.userRepository.save(currentUser);
        }
        return currentUser;
    }

    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
        ResUpdateUserDTO resUser = new ResUpdateUserDTO();
        resUser.setId(user.getId());
        resUser.setName(user.getName());
        resUser.setGender(user.getGender());
        resUser.setAddress(user.getAddress());
        resUser.setAge(user.getAge());
        resUser.setUpdatedAt(user.getUpdatedAt());
        return resUser;
    }

    public void handleDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    public void updateUserToken(String token, String email) {
        User currentUser = this.handleGetUserByUsername(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }
}
