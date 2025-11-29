package huce.nguyentoan.job4u.controller;

import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import huce.nguyentoan.job4u.domain.User;
import huce.nguyentoan.job4u.domain.Response.ResCreateUserDTO;
import huce.nguyentoan.job4u.domain.Response.ResUpdateUserDTO;
import huce.nguyentoan.job4u.domain.Response.ResUserDTO;
import huce.nguyentoan.job4u.domain.Response.ResultPaginationDTO;
import huce.nguyentoan.job4u.service.UserService;
import huce.nguyentoan.job4u.util.annotation.ApiMessage;
import huce.nguyentoan.job4u.util.error.IdInvalidException;
import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/users/{id}")
    @ApiMessage("Lấy người dùng theo id thành công")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") long id) throws IdInvalidException{
        User fetchUser = this.userService.fetchUserById(id);
        if (fetchUser == null) {
            throw new IdInvalidException("Id không tồn tại, vui lòng kiểm tra lại");
            
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertToResUserDTO(fetchUser));
    }
    
    @GetMapping("/users")
    @ApiMessage("Lấy danh sách người dùng thành công")
    public ResponseEntity<ResultPaginationDTO> getAllUser(@Filter Specification<User> spec, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.fetchAllUser(spec, pageable));
    }

    @PostMapping("/users")
    @ApiMessage("Tạo người dùng mới thành công")
    public ResponseEntity<ResCreateUserDTO> createNewUser(@Valid @RequestBody User postmanUser) throws IdInvalidException{
        boolean isEmailExist = this.userService.isEmailExist(postmanUser.getEmail());
        if (isEmailExist) {
            throw new IdInvalidException("Email đã tồn tại, vui lòng sử dụng email khác");
        }
        String hashPassword = this.passwordEncoder.encode(postmanUser.getPassword());
        postmanUser.setPassword(hashPassword);
        User nUser = this.userService.handleCreateUser(postmanUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(nUser));
    }

    @PutMapping("/users")
    @ApiMessage("Cập nhật thông tin người dùng thành công")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User user) throws IdInvalidException{
        User nUser = this.userService.handleUpdateUser(user);
        if (nUser == null) {
            throw new IdInvalidException("Người dùng với id: " + user.getId() + " không tồn tại, vui lòng kiểm tra lại");
            
        }
        return ResponseEntity.ok(this.userService.convertToResUpdateUserDTO(nUser));
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Xóa người dùng thành công")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) throws IdInvalidException{
        User currentUser = this.userService.fetchUserById(id);
        if (currentUser == null) {
            throw new IdInvalidException("Id không tồn tại, vui lòng kiểm tra lại");
        }
        this.userService.handleDeleteUser(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/users/count-user")
    @ApiMessage("Đếm số user")
    public ResponseEntity<Long> countNormalUser() {
        return ResponseEntity.ok(this.userService.countNormalUser());
    }


}
