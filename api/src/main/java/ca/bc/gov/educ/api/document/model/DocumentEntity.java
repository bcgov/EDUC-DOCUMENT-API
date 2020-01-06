package ca.bc.gov.educ.api.document.model;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@JsonView(Views.DocumentMetadata.class)
@Entity
@Table(name = "student_document")
public class DocumentEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID", 
        strategy = "org.hibernate.id.UUIDGenerator", 
        parameters = {
            @Parameter(
                name = "uuid_gen_strategy_class", 
                value = "org.hibernate.id.uuid.CustomVersionOneStrategy"
            ) 
        }
    )
    @Column(name = "student_document_id", unique = true, updatable = false, columnDefinition = "BINARY(16)")
    UUID documentID;

    @NotNull(message = "documentTypeCode cannot be null")
    @Column(name = "document_type_code")
    String documentTypeCode;

    @NotNull(message = "fileName cannot be null")
    @Column(name = "file_name")
    String fileName;

    @Column(name = "file_extension")
    String fileExtension;

    @Min(value = 1, message = "fileSize cannot be less than 1 byte")
    @Max(value = 10_485_760, message = "fileSize cannot be greater than 10MB")
    @Column(name = "file_size")
    Integer fileSize;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    List<DocumentOwnerEntity> documentOwners = new ArrayList<>(); 

    @Column(name = "create_user", updatable = false)
    String createUser;

    @PastOrPresent
    @Column(name = "create_date", updatable = false)
    Date createDate;

    @Column(name = "update_user")
    String updateUser;

    @PastOrPresent
    @Column(name = "update_date")
    Date updateDate;

    @JsonIgnore
    @Basic(fetch = FetchType.LAZY)
    @Lob
    @NotNull(message = "documentData cannot be null")
    @Column(name = "document_data")
    byte[] documentData;

    @JsonView(Views.Document.class)
    @JsonProperty("documentData")
    public String getDocumentBase64Data() {
        return new String(Base64.getEncoder().encode(documentData));
    }

    @JsonView(Views.Document.class)
    @JsonProperty("documentData")
    public void setDocumentBase64Data(String data) {
        documentData = Base64.getDecoder().decode(data);
    }

    public void addOwner(DocumentOwnerEntity owner) {
        this.documentOwners.add(owner);
        owner.setDocument(this);
    }

    public void removeOwner(DocumentOwnerEntity owner) {
        this.documentOwners.remove(owner);
        owner.setDocument(null);
    }
}
