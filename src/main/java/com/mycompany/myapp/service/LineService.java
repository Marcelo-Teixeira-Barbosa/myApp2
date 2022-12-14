package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Line;
import com.mycompany.myapp.repository.LineRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Line}.
 */
@Service
@Transactional
public class LineService {

    private final Logger log = LoggerFactory.getLogger(LineService.class);

    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    /**
     * Save a line.
     *
     * @param line the entity to save.
     * @return the persisted entity.
     */
    public Line save(Line line) {
        log.debug("Request to save Line : {}", line);
        return lineRepository.save(line);
    }

    /**
     * Update a line.
     *
     * @param line the entity to save.
     * @return the persisted entity.
     */
    public Line update(Line line) {
        log.debug("Request to update Line : {}", line);
        return lineRepository.save(line);
    }

    /**
     * Partially update a line.
     *
     * @param line the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Line> partialUpdate(Line line) {
        log.debug("Request to partially update Line : {}", line);

        return lineRepository
            .findById(line.getId())
            .map(existingLine -> {
                if (line.getTitle() != null) {
                    existingLine.setTitle(line.getTitle());
                }

                return existingLine;
            })
            .map(lineRepository::save);
    }

    /**
     * Get all the lines.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Line> findAll(Pageable pageable) {
        log.debug("Request to get all Lines");
        return lineRepository.findAll(pageable);
    }

    /**
     * Get one line by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Line> findOne(Long id) {
        log.debug("Request to get Line : {}", id);
        return lineRepository.findById(id);
    }

    /**
     * Delete the line by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Line : {}", id);
        lineRepository.deleteById(id);
    }
}
