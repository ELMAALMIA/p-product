package com.eTrust.product.dao;

import com.eTrust.product.enums.InventoryStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "products")
public class ProductDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    private String code;

    private String name;
    private String description;
    private String image;
    private String category;
    private Double price;
    private Integer quantity;

    @Indexed(unique = true)
    @Field("internal_reference")
    private String internalReference;

    @Field("shell_id")
    private Long shellId;

    @Field("inventory_status")
    private InventoryStatus inventoryStatus = InventoryStatus.INSTOCK;

    private Double rating = 0.0;

    @Field("created_at")
    private Long createdAt;

    @Field("updated_at")
    private Long updatedAt;

    public ProductDocument() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getInternalReference() { return internalReference; }
    public void setInternalReference(String ref) { this.internalReference = ref; }

    public Long getShellId() { return shellId; }
    public void setShellId(Long shellId) { this.shellId = shellId; }

    public InventoryStatus getInventoryStatus() { return inventoryStatus; }
    public void setInventoryStatus(InventoryStatus s) { this.inventoryStatus = s; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }

    public Long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }
}
