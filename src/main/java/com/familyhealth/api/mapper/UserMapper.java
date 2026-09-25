package com.familyhealth.api.mapper;

import com.familyhealth.api.generated.model.LoginRequest;
import com.familyhealth.api.generated.model.RegisterRequest;
import com.familyhealth.api.model.User;
import com.familyhealth.api.service.model.LoginCommand;
import com.familyhealth.api.service.model.RegisterCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    RegisterCommand toCommand(RegisterRequest request);

    LoginCommand toCommand(LoginRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User toEntity(RegisterCommand command);
}
