package com.familyhealth.api.specification;

import com.familyhealth.api.model.Child_;
import com.familyhealth.api.model.Reminder;
import com.familyhealth.api.model.Reminder_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ReminderSpecification implements Specification<Reminder> {

    private final Long childId;
    private final LocalDate fromDate;
    private final LocalDate toDate;

    @Override
    public Predicate toPredicate(Root<Reminder> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get(Reminder_.child).get(Child_.id), childId));
        if (fromDate != null) predicates.add(cb.greaterThanOrEqualTo(root.get(Reminder_.dueDate), fromDate));
        if (toDate != null)   predicates.add(cb.lessThanOrEqualTo(root.get(Reminder_.dueDate), toDate));
        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
