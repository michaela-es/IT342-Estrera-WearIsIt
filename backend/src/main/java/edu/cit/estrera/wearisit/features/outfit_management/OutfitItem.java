package edu.cit.estrera.wearisit.features.outfit_management;

import edu.cit.estrera.wearisit.features.clothing_item_management.ClothingItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "outfit_items")
@Getter @Setter @NoArgsConstructor
public class OutfitItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "outfit_id", nullable = false)
    private Outfit outfit;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private ClothingItem clothingItem;

    @Column(name = "position", nullable = false)
    private Integer position;

    @Column(name = "notes", length = 500)
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}