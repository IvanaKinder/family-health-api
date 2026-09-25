package com.familyhealth.api.mapper;

import com.familyhealth.api.generated.model.MilestonePage;
import com.familyhealth.api.generated.model.MilestoneRequest;
import com.familyhealth.api.generated.model.MilestoneResponse;
import com.familyhealth.api.model.Milestone;
import com.familyhealth.api.service.model.MilestoneCommand;
import com.familyhealth.api.service.model.MilestoneView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface MilestoneMapper {

    MilestoneCommand toCommand(MilestoneRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "child", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Milestone toEntity(MilestoneCommand command);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "child", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(MilestoneCommand command, @MappingTarget Milestone milestone);

    MilestoneView toView(Milestone milestone);

    MilestoneResponse toResponse(MilestoneView view);

    default MilestonePage toPage(Page<MilestoneView> page) {
        return new MilestonePage()
                .content(page.getContent().stream().map(this::toResponse).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages());
    }
}
