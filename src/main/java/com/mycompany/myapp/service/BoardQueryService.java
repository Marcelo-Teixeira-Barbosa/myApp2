package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.Board;
import com.mycompany.myapp.repository.BoardRepository;
import com.mycompany.myapp.service.criteria.BoardCriteria;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Board} entities in the database.
 * The main input is a {@link BoardCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Board} or a {@link Page} of {@link Board} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BoardQueryService extends QueryService<Board> {

    private final Logger log = LoggerFactory.getLogger(BoardQueryService.class);

    private final BoardRepository boardRepository;

    public BoardQueryService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    /**
     * Return a {@link List} of {@link Board} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Board> findByCriteria(BoardCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Board> specification = createSpecification(criteria);
        return boardRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Board} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Board> findByCriteria(BoardCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Board> specification = createSpecification(criteria);
        return boardRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(BoardCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Board> specification = createSpecification(criteria);
        return boardRepository.count(specification);
    }

    /**
     * Function to convert {@link BoardCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Board> createSpecification(BoardCriteria criteria) {
        Specification<Board> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Board_.id));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), Board_.title));
            }
        }
        return specification;
    }
}
