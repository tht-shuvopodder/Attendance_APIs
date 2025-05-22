package com.API_Testing.APIx.controller;

import com.API_Testing.APIx.model.TaskInfo;
import com.API_Testing.APIx.model.request.TaskAssignRequestDTO;
import com.API_Testing.APIx.model.request.TaskRequestDTO;
import com.API_Testing.APIx.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    @Value("${app.upload-dir}")
    private String uploadDir;
    @Value("${app.max-size}")
    private int maxSize;
    @Value("${app.media-download-url}")
    private String mediaDownloadUrl;

    // Create directory if not exists
    @PostConstruct
    public void init() {
        if (uploadDir != null) {
            System.out.println("Upload Dir: " + uploadDir);
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        } else {
            throw new IllegalStateException("Upload directory is not configured in application properties.");
        }
    }

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createTask(@RequestBody TaskRequestDTO request) {
        taskService.createTask(request);
        return ResponseEntity.ok("✅ New Task added to the list.");
    }


    @PostMapping(value = "/assign", consumes = "multipart/form-data")
    public ResponseEntity<String> assignTask(@RequestParam("file") MultipartFile file,
                                             @RequestParam("request") String taskAssignRequestDTO) {
        if (file.isEmpty()) {
            return new ResponseEntity<>("No file selected for upload", HttpStatus.BAD_REQUEST);
        } else if (file.getSize()/(1000.0*1000.0) > maxSize) {//convert in mb and compare
            return new ResponseEntity<>("File too large", HttpStatus.BAD_REQUEST);
        }

        try {
            TaskAssignRequestDTO requestDTO = new ObjectMapper().readValue(taskAssignRequestDTO, TaskAssignRequestDTO.class);

            //check if needed more
            if(requestDTO.getTaskId()==null||requestDTO.getAdminId()==null||requestDTO.getDeviceMac()==null){
                return new ResponseEntity<>("Invalid message request!", HttpStatus.BAD_REQUEST);
            }

            // Save the file
            String fileNameExt = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String newFileName = UUID.randomUUID() + "." + fileNameExt;
            String filePath = uploadDir + "/" + newFileName;

            file.transferTo(new File(filePath));
            System.out.println(filePath);

            requestDTO.setDocumentUrl(mediaDownloadUrl+newFileName);
            taskService.assignTask(requestDTO);
            return ResponseEntity.ok("Task assigned successfully to selected employees.");

        } catch (IOException e) {
            return new ResponseEntity<>("Error while uploading the file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // Download file
    @GetMapping(value = "/media/documents/download/{filename}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadFile(@PathVariable String filename) {
        System.out.println(filename);
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename);  // Correct way to join paths
            // Check if file exists
            if (Files.exists(filePath)) {
                // Use Resource to stream the file instead of loading it into memory
                Resource fileResource = new UrlResource(filePath.toUri());
                return ResponseEntity.ok()
                        .contentLength(fileResource.contentLength())
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                        .body(fileResource);
            } else {
                return new ResponseEntity<>("Media files not found or deleted!",HttpStatus.NOT_FOUND);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Media files not found or deleted!", HttpStatus.NOT_FOUND);
        }
    }

}
