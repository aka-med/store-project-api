package com.akamed.storeproject.mappers;


import com.akamed.storeproject.dtos.RegisterUserRequest;
import com.akamed.storeproject.dtos.UserDto;
import com.akamed.storeproject.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

//    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    UserDto toDto(User user);


    User toEntity(RegisterUserRequest request);
}
