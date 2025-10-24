package com.devmam.taraacademyapi.repository;

import com.devmam.taraacademyapi.models.entities.FileUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileUploadRepository extends JpaRepository<FileUpload, Integer>, JpaSpecificationExecutor<FileUpload> {

    Optional<FileUpload> findByFileTypeAndReferenceId(String fileType, Integer referenceId);
    
    List<FileUpload> findByFileType(String fileType);
    
    List<FileUpload> findByReferenceId(Integer referenceId);
    
    List<FileUpload> findByIsDeleted(Integer isDeleted);
    
    @Query("SELECT f FROM FileUpload f WHERE f.fileType = :fileType AND f.referenceId = :referenceId AND f.isDeleted = 0")
    List<FileUpload> findActiveFilesByTypeAndReference(String fileType, Integer referenceId);
}