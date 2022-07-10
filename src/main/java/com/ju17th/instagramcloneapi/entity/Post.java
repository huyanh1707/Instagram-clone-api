package com.ju17th.instagramcloneapi.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "posts")
@Getter
@Setter
public class Post extends BaseEntity {
    @NotBlank
    @Size(max = 100)
    private String description;

    @NotBlank
    private String imagePath;
}
