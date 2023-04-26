package com.example.userapi.controller;

import com.example.userapi.model.User;
import com.example.userapi.model.UserDto;
import com.example.userapi.repository.UserRepository;
import org.modelmapper.ModelMapper;
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
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<User>>> getUsers() {
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
        Optional<User> user = userRepository.findById(id);
        return user.map(value -> ResponseEntity.ok(EntityModel.of(value,
                linkTo(methodOn(UserController.class).getUser(id)).withSelfRel(),
                linkTo(methodOn(UserController.class).getUsers()).withRel("users")))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EntityModel<User>> createUser(@RequestBody UserDto userDto) {
        User user = modelMapper.map(userDto, User.class);
        User savedUser = userRepository.save(user);
        return ResponseEntity.created(linkTo(methodOn(UserController.class).getUser(savedUser.getId())).toUri())
                .body(EntityModel.of(savedUser,
                        linkTo(methodOn(UserController.class).getUser(savedUser.getId())).withSelfRel(),
                        linkTo(methodOn(UserController.class).getUsers()).withRel("users")));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<User>> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
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
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}

