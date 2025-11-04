package huce.nguyentoan.job4u.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import huce.nguyentoan.job4u.domain.Role;
import huce.nguyentoan.job4u.domain.Response.ResultPaginationDTO;
import huce.nguyentoan.job4u.service.RoleService;
import huce.nguyentoan.job4u.util.annotation.ApiMessage;
import huce.nguyentoan.job4u.util.error.IdInvalidException;
import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;




@RestController
@RequestMapping("/api/v1")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    @ApiMessage("Tạo mới vai trò")
    public ResponseEntity<Role> create(@Valid @RequestBody Role role) throws IdInvalidException{
        //check name
        if (this.roleService.existByName(role.getName())) {
            throw new IdInvalidException("Vai trò đã tồn tại");
        }
        
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.createRole(role));
    }
    
    @PutMapping("/roles")
    @ApiMessage("Cập nhật vai trò")
    public ResponseEntity<Role> update(@RequestBody Role role) throws IdInvalidException{
        if (this.roleService.fetchById(role.getId()) == null) {
            throw new IdInvalidException("Vai trò không tồn tại");
        }
        
        return ResponseEntity.ok().body(this.roleService.updateRole(role));
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("Xoá vai trò")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException{
        if (this.roleService.fetchById(id) == null) {
            throw new IdInvalidException("Vai trò không tồn tại");
        }
        this.roleService.deleteRoleById(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/roles")
    @ApiMessage("Hiển thị vai trò")
    public ResponseEntity<ResultPaginationDTO> getRoles(@Filter Specification<Role> spec, Pageable pageable) {
        return ResponseEntity.ok(this.roleService.getAllRole(spec, pageable));
    }
    
    @GetMapping("/roles/{id}")
    @ApiMessage("Lấy vai trò theo id")
    public ResponseEntity<Role> getById(@PathVariable("id") long id) throws IdInvalidException{
        Role role = this.roleService.fetchById(id);
        if (role == null) {
            throw new IdInvalidException("Vai trò không tồn tại");
        }
        return ResponseEntity.ok().body(role);
    }
    
}
