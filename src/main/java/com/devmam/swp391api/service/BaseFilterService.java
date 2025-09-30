package com.devmam.swp391api.service;

import com.devmam.taraacademyapi.constant.enums.SortDirection;
import com.devmam.taraacademyapi.exception.customize.InvalidFieldException;
import com.devmam.taraacademyapi.models.dto.request.BaseFilterRequest;
import com.devmam.taraacademyapi.models.dto.request.FilterCriteria;
import com.devmam.taraacademyapi.models.dto.request.SortCriteria;
import jakarta.persistence.Column;
import jakarta.persistence.EntityManager;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;
import java.util.*;

import jakarta.persistence.criteria.Predicate;

import java.util.stream.Collectors;

public abstract class BaseFilterService<T> {
    protected abstract EntityManager getEntityManager();

    protected abstract Class<T> getEntityClass();

    /**
     * Lấy tất cả field names có @Column annotation
     */
    protected Set<String> getValidColumnFields() {
        Set<String> validFields = new HashSet<>();
        Field[] fields = getEntityClass().getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(Column.class)) {
                validFields.add(field.getName());
            }
            // Cũng check cho @JoinColumn nếu cần
            if (field.isAnnotationPresent(JoinColumn.class)) {
                validFields.add(field.getName());
            }
        }

        return validFields;
    }

    /**
     * Validate field name có tồn tại và có @Column annotation không
     */
    protected void validateFieldName(String fieldName) {
        Set<String> validFields = getValidColumnFields();
        if (!validFields.contains(fieldName)) {
            throw new InvalidFieldException(
                    String.format("Field '%s' không tồn tại hoặc không có @Column annotation trong entity %s. " +
                                    "Các field hợp lệ: %s",
                            fieldName, getEntityClass().getSimpleName(), validFields)
            );
        }
    }

    /**
     * Tạo Specification từ danh sách FilterCriteria
     */
    protected Specification<T> createSpecification(List<FilterCriteria> filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            for (FilterCriteria filter : filters) {
                if (filter.getFieldName() == null || filter.getOperation() == null) {
                    continue;
                }

                // Validate field name
                validateFieldName(filter.getFieldName());

                Path<Object> fieldPath = getFieldPath(root, filter.getFieldName());
                Predicate predicate = createPredicate(criteriaBuilder, fieldPath, filter);

                if (predicate != null) {
                    predicates.add(predicate);
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Tạo field path, hỗ trợ nested fields (ví dụ: "user.name")
     */
    protected Path<Object> getFieldPath(Root<T> root, String fieldName) {
        String[] parts = fieldName.split("\\.");
        Path<Object> path = root.get(parts[0]);

        for (int i = 1; i < parts.length; i++) {
            path = path.get(parts[i]);
        }

        return path;
    }

    /**
     * Tạo Predicate dựa trên operation
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected Predicate createPredicate(CriteriaBuilder cb, Path<?> path, FilterCriteria filter) {
        Object value = filter.getValue();
        if (value == null) {
            return null;
        }

        switch (filter.getOperation()) {
            case EQUALS:
                return cb.equal(path, value);

            case LESS_THAN:
                if (value instanceof Comparable comparable) {
                    return cb.lessThan(path.as(Comparable.class), comparable);
                }
                throw new IllegalArgumentException("Giá trị không phải Comparable cho toán tử LESS_THAN");

            case LESS_THAN_OR_EQUAL:
                if (value instanceof Comparable comparable) {
                    return cb.lessThanOrEqualTo(path.as(Comparable.class), comparable);
                }
                throw new IllegalArgumentException("Giá trị không phải Comparable cho toán tử LESS_THAN_OR_EQUAL");

            case GREATER_THAN:
                if (value instanceof Comparable comparable) {
                    return cb.greaterThan(path.as(Comparable.class), comparable);
                }
                throw new IllegalArgumentException("Giá trị không phải Comparable cho toán tử GREATER_THAN");

            case GREATER_THAN_OR_EQUAL:
                if (value instanceof Comparable comparable) {
                    return cb.greaterThanOrEqualTo(path.as(Comparable.class), comparable);
                }
                throw new IllegalArgumentException("Giá trị không phải Comparable cho toán tử GREATER_THAN_OR_EQUAL");

            case LIKE:
                if (value instanceof String str) {
                    return cb.like(path.as(String.class), "%" + str + "%");
                }
                throw new IllegalArgumentException("LIKE chỉ áp dụng cho String");

            case ILIKE:
                if (value instanceof String str) {
                    return cb.like(cb.lower(path.as(String.class)), "%" + str.toLowerCase() + "%");
                }
                throw new IllegalArgumentException("ILIKE chỉ áp dụng cho String");

            case IN:
                if (value instanceof Collection<?> col) {
                    return path.in(col);
                }
                return path.in(value);

            case NOT_IN:
                if (value instanceof Collection<?> col) {
                    return cb.not(path.in(col));
                }
                return cb.not(path.in(value));

            default:
                throw new IllegalArgumentException("Không hỗ trợ operation: " + filter.getOperation());
        }
    }


    /**
     * Tạo Pageable từ sort criteria và pagination info
     */
    protected Pageable createPageable(List<SortCriteria> sorts, Integer page, Integer size) {
        // Validate sort fields
        for (SortCriteria sort : sorts) {
            validateFieldName(sort.getFieldName());
        }

        if (sorts.isEmpty()) {
            return PageRequest.of(page != null ? page : 0, size != null ? size : 20);
        }

        List<Sort.Order> orders = sorts.stream()
                .map(sort -> {
                    Sort.Direction direction = sort.getDirection() == SortDirection.DESC ?
                            Sort.Direction.DESC : Sort.Direction.ASC;
                    return new Sort.Order(direction, sort.getFieldName());
                })
                .collect(Collectors.toList());

        Sort sortObj = Sort.by(orders);
        return PageRequest.of(page != null ? page : 0, size != null ? size : 20, sortObj);
    }

    /**
     * Main method để filter và sort
     */
    public Page<T> filter(BaseFilterRequest request) {
        try {
            Specification<T> spec = createSpecification(request.getFilters());
            Pageable pageable = createPageable(request.getSorts(), request.getPage(), request.getSize());

            // Sử dụng JPA Repository hoặc EntityManager
            return findAll(spec, pageable);

        } catch (Exception e) {
            if (e instanceof InvalidFieldException) {
                throw e;
            }
            throw new RuntimeException("Lỗi khi thực hiện filter: " + e.getMessage(), e);
        }
    }

    /**
     * Abstract method để implement trong concrete service
     */
    protected abstract Page<T> findAll(Specification<T> spec, Pageable pageable);
}
