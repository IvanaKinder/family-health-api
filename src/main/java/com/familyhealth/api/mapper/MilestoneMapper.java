package com.familyhealth.api.mapper;

import com.familyhealth.api.generated.model.MilestonePage;
import com.familyhealth.api.generated.model.MilestoneRequest;
import com.familyhealth.api.generated.model.MilestoneResponse;
import com.familyhealth.api.model.Milestone;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface MilestoneMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "child", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Milestone toEntity(MilestoneRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "child", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(MilestoneRequest request, @MappingTarget Milestone milestone);

    MilestoneResponse toResponse(Milestone milestone);

    default MilestonePage toPage(Page<Milestone> page) {
        return new MilestonePage()
                .content(page.getContent().stream().map(this::toResponse).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages());
    }
}
