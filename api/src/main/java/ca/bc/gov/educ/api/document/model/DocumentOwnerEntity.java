package ca.bc.gov.educ.api.document.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.Date;
import java.util.UUID;

@Data
@JsonView(Views.DocumentMetadata.class)
@Entity
@IdClass(DocumentOwnerId.class)
@Table(name = "student_document_owner_xref")
public class DocumentOwnerEntity {

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Id
    @ManyToOne
    @JoinColumn(name = "student_document_id", updatable = false, columnDefinition = "BINARY(16)")
    DocumentEntity document;

    @Id
    @NotNull(message = "documentOwnerTypeCode cannot be null")
    @Column(name = "document_owner_type_code")
    String documentOwnerTypeCode;

    @Id
    @NotNull(message = "documentOwnerID cannot be null")
    @Column(name = "document_owner_id", columnDefinition = "BINARY(16)")
    UUID documentOwnerID;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @Column(name = "create_user", updatable = false)
    String createUser;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @PastOrPresent
    @Column(name = "create_date", updatable = false)
    Date createDate;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @Column(name = "update_user")
    String updateUser;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @PastOrPresent
    @Column(name = "update_date")
    Date updateDate;
}
