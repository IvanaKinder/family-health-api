package com.familyhealth.api.mapper;

import com.familyhealth.api.generated.model.ChildPage;
import com.familyhealth.api.generated.model.ChildRequest;
import com.familyhealth.api.generated.model.ChildResponse;
import com.familyhealth.api.model.Child;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface ChildMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Child toEntity(ChildRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(ChildRequest request, @MappingTarget Child child);

    ChildResponse toResponse(Child child);

    default ChildPage toPage(Page<Child> page) {
        return new ChildPage()
                .content(page.getContent().stream().map(this::toResponse).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages());
    }
}
