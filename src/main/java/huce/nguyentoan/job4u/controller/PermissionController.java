package huce.nguyentoan.job4u.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import huce.nguyentoan.job4u.domain.Permission;
import huce.nguyentoan.job4u.domain.Response.ResultPaginationDTO;
import huce.nguyentoan.job4u.service.PermissionService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;




@RestController
@RequestMapping("/api/v1")
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    @ApiMessage("Tạo mới quyền hạn")
    public ResponseEntity<Permission> create(@Valid @RequestBody Permission permission) throws IdInvalidException{
        //check exist
        if (this.permissionService.isPermissionExist(permission)) {
            throw new IdInvalidException("Quyền hạn đã tồn tại");
        }
        
        return ResponseEntity.status(HttpStatus.CREATED).body(this.permissionService.createPermission(permission));
    }
    
    @PutMapping("/permissions")
    @ApiMessage("Cập nhật quyền hạn")
    public ResponseEntity<Permission> update(@Valid @RequestBody Permission permission) throws IdInvalidException{
        //check exist by id
        if (this.permissionService.fetchById(permission.getId()) == null) {
            throw new IdInvalidException("Quyền hạn không tồn tại");
        }
        
        //check exist by module, apiPath, method
        if (this.permissionService.isPermissionExist(permission)) {
            throw new IdInvalidException("Quyền hạn đã tồn tại");
        }
        return ResponseEntity.ok().body(this.permissionService.updatePermission(permission));
    }

    @GetMapping("/permissions")
    @ApiMessage("Hiển thị quyền hạn")
    public ResponseEntity<ResultPaginationDTO> getPermissions(@Filter Specification<Permission> spec, Pageable pageable) {
        return ResponseEntity.ok(this.permissionService.getAllPermission(spec, pageable));
    }

    @DeleteMapping("/permissions")
    @ApiMessage("Xoá quyền hạn")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        //check exist by id
        if (this.permissionService.fetchById(id) == null) {
            throw new IdInvalidException("Quyền hạn không tồn tại");
        }
        this.permissionService.deletePermission(id);
        return ResponseEntity.ok().body(null);
    }
}
