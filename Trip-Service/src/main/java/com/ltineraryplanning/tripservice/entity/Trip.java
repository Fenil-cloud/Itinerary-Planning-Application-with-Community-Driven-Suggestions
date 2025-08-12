package com.ltineraryplanning.tripservice.entity;

import com.ltineraryplanning.tripservice.enums.TripType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tripId;
    private String userId;
    private Long numberOfMembers;
    private String tripName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean allowComment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isPublic;
    private Boolean isPrivate;
    private TripType tripType;
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Destination> destinations = new ArrayList<>();
    @ElementCollection
    @CollectionTable(name = "trip_share_with", joinColumns = @JoinColumn(name = "trip_id"))
    @Column(name = "shared_username")
    private List<String> shareWithUsernames = new ArrayList<>();
}
