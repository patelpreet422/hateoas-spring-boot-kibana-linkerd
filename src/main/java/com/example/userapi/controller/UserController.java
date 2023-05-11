package com.example.userapi.controller;

import com.example.userapi.model.User;
import com.example.userapi.model.UserDto;
import com.example.userapi.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<User>>> getUsers() {
        log.info("getUsers() called");

        List<EntityModel<User>> users = userRepository.findAll().stream()
                .map(user -> EntityModel.of(user,
                        linkTo(methodOn(UserController.class).getUser(user.getId())).withSelfRel(),
                        linkTo(methodOn(UserController.class).getUsers()).withRel("users")))
                .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(users,
                linkTo(methodOn(UserController.class).getUsers()).withSelfRel()));
    }


    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<User>> getUser(@PathVariable Long id) {
        MDC.put("user_id", id.toString());
        log.info("getUser() called");

        Optional<User> user = userRepository.findById(id);
        return user.map(value -> ResponseEntity.ok(EntityModel.of(value,
                linkTo(methodOn(UserController.class).getUser(id)).withSelfRel(),
                linkTo(methodOn(UserController.class).getUsers()).withRel("users")))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EntityModel<User>> createUser(@RequestBody UserDto userDto) {
        MDC.put("user_name", userDto.getName());
        MDC.put("user_company", userDto.getCompany());

        log.info("createUser() called");

        User user = modelMapper.map(userDto, User.class);
        User savedUser = userRepository.save(user);
        return ResponseEntity.created(linkTo(methodOn(UserController.class).getUser(savedUser.getId())).toUri())
                .body(EntityModel.of(savedUser,
                        linkTo(methodOn(UserController.class).getUser(savedUser.getId())).withSelfRel(),
                        linkTo(methodOn(UserController.class).getUsers()).withRel("users")));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<User>> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        MDC.put("user_id", id.toString());
        MDC.put("user_name", userDto.getName());
        MDC.put("user_company", userDto.getCompany());

        log.info("updateUser() called");

        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userDto.setId(id);
            modelMapper.map(userDto, user.get());

            User savedUser = userRepository.save(user.get());
            return ResponseEntity.ok(EntityModel.of(savedUser,
                    linkTo(methodOn(UserController.class).getUser(id)).withSelfRel(),
                    linkTo(methodOn(UserController.class).getUsers()).withRel("users")));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        MDC.put("user_id", id.toString());
        log.info("deleteUser() called");

        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}

