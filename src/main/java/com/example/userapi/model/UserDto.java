package com.example.userapi.model;

import com.example.userapi.controller.UserController;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDto extends RepresentationModel<UserDto> {

    private Long id;

    private String name;

    private String company;

    public UserDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.company = user.getCompany();
        add(linkTo(methodOn(UserController.class).getUser(user.getId())).withSelfRel());
    }
}
