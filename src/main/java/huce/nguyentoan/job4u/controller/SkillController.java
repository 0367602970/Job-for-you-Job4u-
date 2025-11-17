package huce.nguyentoan.job4u.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import huce.nguyentoan.job4u.domain.Skill;
import huce.nguyentoan.job4u.domain.Response.ResultPaginationDTO;
import huce.nguyentoan.job4u.service.SkillService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1")
public class SkillController {
    private final SkillService skillService;
    
    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("/skills")
    @ApiMessage("Tạo mới 1 kỹ năng")
    public ResponseEntity<Skill> createNewSkill(@Valid @RequestBody Skill s) throws IdInvalidException{
        if (s.getName() != null && this.skillService.isNameExist(s.getName())) {
            throw new IdInvalidException("Tên kỹ năng đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.skillService.handleCreateSkill(s));
    }

    @PutMapping("skills")
    @ApiMessage("Cập nhật thông tin kỹ năng")
    public ResponseEntity<Skill> updateSkill(@Valid @RequestBody Skill s) throws IdInvalidException {
        //check id
        Skill currentSkill = this.skillService.fecthSkillById(s.getId());
        if (currentSkill == null) {
            throw new IdInvalidException("Kỹ năng không tồn tại");
        }

        //check name
        if (s.getName() != null && this.skillService.isNameExist(s.getName())) {
            throw new IdInvalidException("Tên kỹ năng đã tồn tại");
        }

        currentSkill.setName(s.getName());
        return ResponseEntity.ok().body(this.skillService.handleUpdateSkill(currentSkill));
    }

    @GetMapping("/skills")
    @ApiMessage("Lấy danh sách kỹ năng")
    public ResponseEntity<ResultPaginationDTO> getAllSkills(@Filter Specification<Skill> spec, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.skillService.fetchAllSkills(spec, pageable));
    }
    
    @DeleteMapping("/skills/{id}")
    @ApiMessage("Xoá kỹ năng")
    public ResponseEntity<Void> deleteSkill(@PathVariable("id") long id) throws IdInvalidException {
        Skill currentSkill = this.skillService.fecthSkillById(id);
        if (currentSkill == null) {
            throw new IdInvalidException("Kỹ năng không tồn tại");
        }
        this.skillService.handleDeleteSkill(id);
        return ResponseEntity.ok().body(null);
    }
}
