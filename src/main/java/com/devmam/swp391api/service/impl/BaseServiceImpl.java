package com.devmam.swp391api.service.impl;

import com.devmam.taraacademyapi.constant.enums.SortDirection;
import com.devmam.taraacademyapi.exception.customize.InvalidFieldException;
import com.devmam.taraacademyapi.models.dto.request.BaseFilterRequest;
import com.devmam.taraacademyapi.models.dto.request.FilterCriteria;
import com.devmam.taraacademyapi.models.dto.request.SortCriteria;
import com.devmam.taraacademyapi.service.BaseService;
import jakarta.persistence.Column;
import jakarta.persistence.EntityManager;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.*;

import jakarta.persistence.criteria.Predicate;

import java.util.stream.Collectors;

@Service
@Transactional
public abstract class BaseServiceImpl<T, ID> implements BaseService<T, ID> {

    protected final JpaRepository<T, ID> repository;
    protected final JpaSpecificationExecutor<T> specificationExecutor;
    private final String statusFieldName;
    private Class<T> entityClass;

    public BaseServiceImpl(JpaRepository<T, ID> repository, String statusFieldName) {
        this.repository = repository;
        this.statusFieldName = statusFieldName;

        // Kiểm tra repository có implement JpaSpecificationExecutor không
        if (!(repository instanceof JpaSpecificationExecutor)) {
            throw new IllegalArgumentException("Repository phải implement JpaSpecificationExecutor để sử dụng filter");
        }
        this.specificationExecutor = (JpaSpecificationExecutor<T>) repository;

        // Tự động detect entity class từ generic type
        this.entityClass = getEntityClassFromGeneric();
    }

    public BaseServiceImpl(JpaRepository<T, ID> repository) {
        this(repository, "status");
    }

    protected abstract EntityManager getEntityManager();

    /**
     * Tự động detect entity class từ generic type
     */
    @SuppressWarnings("unchecked")
    private Class<T> getEntityClassFromGeneric() {
        try {
            ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
            return (Class<T>) parameterizedType.getActualTypeArguments()[0];
        } catch (Exception e) {
            throw new IllegalStateException("Không thể tự động detect entity class. Hãy override method getEntityClass()", e);
        }
    }

    /**
     * Lấy entity class (có thể override nếu cần)
     */
    protected Class<T> getEntityClass() {
        return this.entityClass;
    }

    // ================= CRUD METHODS =================

    /**
     * Tạo mới entity
     */
    @Override
    @Transactional
    public T create(T entity) {
        return repository.save(entity);
    }

    /**
     * Cập nhật entity
     */
    @Override
    @Transactional
    public T update(ID id, T entity) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Entity not found for id: " + id);
        }
        return repository.save(entity);
    }

    /**
     * Xóa entity
     */
    @Override
    @Transactional
    public void delete(ID id) {
        repository.deleteById(id);
    }

    /**
     * Thay đổi status
     */
    @Override
    @Transactional
    public T changeStatus(ID id, Integer status) {
        Optional<T> optional = repository.findById(id);
        T entity = optional.orElseThrow(() -> new IllegalArgumentException("Entity not found for id: " + id));
        setStatus(entity, status);
        return repository.save(entity);
    }

    /**
     * Lấy tất cả
     */
    @Override
    @Transactional(readOnly = true)
    public List<T> getAll() {
        return repository.findAll();
    }

    /**
     * Lấy một entity theo ID
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<T> getOne(ID id) {
        return repository.findById(id);
    }

    /**
     * Kiểm tra entity có tồn tại không
     */
    @Override
    @Transactional(readOnly = true)
    public boolean exists(ID id) {
        return repository.existsById(id);
    }

    /**
     * Đếm tổng số entity
     */
    @Override
    @Transactional(readOnly = true)
    public long count() {
        return repository.count();
    }

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

                Path<?> fieldPath = getFieldPath(root, filter.getFieldName());
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
    protected Path<?> getFieldPath(Root<T> root, String fieldName) {
        String[] parts = fieldName.split("\\.");
        Path<?> path = root.get(parts[0]);

        for (int i = 1; i < parts.length; i++) {
            path = path.get(parts[i]);
        }

        return path;
    }

    /**
     * Tạo Predicate dựa trên operation
     */
    @SuppressWarnings("unchecked")
    protected Predicate createPredicate(CriteriaBuilder cb, Path<?> path, FilterCriteria filter) {
        Object value = filter.getValue();
        if (value == null) {
            return null;
        }

        switch (filter.getOperation()) {
            case EQUALS:
                if (path.getJavaType().equals(UUID.class) && value instanceof String str) {
                    return cb.equal(path, UUID.fromString(str));
                }
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
                if (value instanceof Collection) {
                    return path.in((Collection<?>) value);
                }
                return path.in(value);

            case NOT_IN:
                if (value instanceof Collection) {
                    return cb.not(path.in((Collection<?>) value));
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
    @Override
    @Transactional(readOnly = true)
    public Page<T> filter(BaseFilterRequest request) {
        try {
            Specification<T> spec = createSpecification(request.getFilters());
            Pageable pageable = createPageable(request.getSorts(), request.getPage(), request.getSize());

            return specificationExecutor.findAll(spec, pageable);

        } catch (Exception e) {
            if (e instanceof InvalidFieldException) {
                throw e;
            }
            throw new RuntimeException("Lỗi khi thực hiện filter: " + e.getMessage(), e);
        }
    }

    // ================= PRIVATE HELPER METHODS =================

    /**
     * Set status field via reflection
     */
    private void setStatus(T entity, Integer status) {
        try {
            Field field = getField(entity.getClass(), statusFieldName);
            field.setAccessible(true);
            field.set(entity, status);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to set status field '" + statusFieldName + "' via reflection", e);
        }
    }

    /**
     * Get field from class hierarchy
     */
    private Field getField(Class<?> clazz, String name) throws NoSuchFieldException {
        Class<?> current = clazz;
        while (current != null) {
            try {
                return current.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException(name);
    }

    /**
     * Convert value to target type
     * @param value
     * @param targetType
     * @return
     */

    private Object convertValue(Object value, Class<?> targetType) {
        if (value == null) return null;

        if (targetType.equals(UUID.class) && value instanceof String str) {
            return UUID.fromString(str);
        }
        if (targetType.equals(Long.class) && value instanceof String str) {
            return Long.parseLong(str);
        }
        if (targetType.equals(Integer.class) && value instanceof String str) {
            return Integer.parseInt(str);
        }
        if (targetType.equals(Boolean.class) && value instanceof String str) {
            return Boolean.parseBoolean(str);
        }
        // fallback
        return value;
    }

}
