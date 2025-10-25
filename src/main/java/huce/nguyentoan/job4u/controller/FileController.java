package huce.nguyentoan.job4u.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import huce.nguyentoan.job4u.domain.Response.File.ResUploadFileDTO;
import huce.nguyentoan.job4u.service.FileService;
import huce.nguyentoan.job4u.util.annotation.ApiMessage;
import huce.nguyentoan.job4u.util.error.StorageException;

@RestController
@RequestMapping("/api/v1")
public class FileController {
    @Value("${nguyentoan.upload-file.base-uri}")
    private String baseURI;

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/files")
    @ApiMessage("Tải tệp lên máy chủ")
    public ResponseEntity<ResUploadFileDTO> upload(@RequestParam("file") MultipartFile file, @RequestParam("folder") String folder) throws URISyntaxException, IOException, StorageException {
        if (file.isEmpty()) {
            throw new StorageException("File tải lên không được để trống");
        }

        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
        boolean isValid = allowedExtensions.stream().anyMatch(item -> fileName.toLowerCase().endsWith(item));
        if (!isValid) {
            throw new StorageException("Định dạng file không hợp lệ. Vui lòng tải lên các định dạng: " + String.join(", ", allowedExtensions));
        }

        //create a directory if not exists
        this.fileService.createDirectory(baseURI + folder);
        //store file
        String uploadFile = this.fileService.store(file, folder);

        ResUploadFileDTO res = new ResUploadFileDTO(uploadFile, Instant.now());
        return ResponseEntity.ok().body(res);
    }
}
