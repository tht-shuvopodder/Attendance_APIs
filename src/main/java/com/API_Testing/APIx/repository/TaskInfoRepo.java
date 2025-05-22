package com.API_Testing.APIx.repository;

import com.API_Testing.APIx.model.TaskInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskInfoRepo extends JpaRepository<TaskInfo, Integer> {

    boolean existsByTaskId(Integer taskId);
}
