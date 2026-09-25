package com.familyhealth.api.mapper;

import com.familyhealth.api.generated.model.ReminderPage;
import com.familyhealth.api.generated.model.ReminderRequest;
import com.familyhealth.api.generated.model.ReminderResponse;
import com.familyhealth.api.model.Reminder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface ReminderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "child", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Reminder toEntity(ReminderRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "child", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(ReminderRequest request, @MappingTarget Reminder reminder);

    ReminderResponse toResponse(Reminder reminder);

    default ReminderPage toPage(Page<Reminder> page) {
        return new ReminderPage()
                .content(page.getContent().stream().map(this::toResponse).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages());
    }
}
