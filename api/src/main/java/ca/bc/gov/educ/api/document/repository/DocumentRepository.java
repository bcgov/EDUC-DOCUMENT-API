package ca.bc.gov.educ.api.document.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import ca.bc.gov.educ.api.document.model.DocumentEntity;

public interface DocumentRepository extends CrudRepository<DocumentEntity, UUID> {
}