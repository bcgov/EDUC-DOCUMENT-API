package ca.bc.gov.educ.api.document.repository;

import org.springframework.data.repository.CrudRepository;

import ca.bc.gov.educ.api.document.model.DocumentOwnerEntity;
import ca.bc.gov.educ.api.document.model.DocumentOwnerId;

public interface DocumentOwnerRepository extends CrudRepository<DocumentOwnerEntity, DocumentOwnerId> {
}