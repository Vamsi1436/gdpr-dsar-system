package com.dsar.repository;

import com.dsar.domain.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

List<Attachment> findByRequest_Id(Long requestId);
}
