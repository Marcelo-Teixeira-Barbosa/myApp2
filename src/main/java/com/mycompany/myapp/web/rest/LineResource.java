package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Line;
import com.mycompany.myapp.repository.LineRepository;
import com.mycompany.myapp.service.LineQueryService;
import com.mycompany.myapp.service.LineService;
import com.mycompany.myapp.service.criteria.LineCriteria;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Line}.
 */
@RestController
@RequestMapping("/api")
public class LineResource {

    private final Logger log = LoggerFactory.getLogger(LineResource.class);

    private static final String ENTITY_NAME = "line";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LineService lineService;

    private final LineRepository lineRepository;

    private final LineQueryService lineQueryService;

    public LineResource(LineService lineService, LineRepository lineRepository, LineQueryService lineQueryService) {
        this.lineService = lineService;
        this.lineRepository = lineRepository;
        this.lineQueryService = lineQueryService;
    }

    /**
     * {@code POST  /lines} : Create a new line.
     *
     * @param line the line to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new line, or with status {@code 400 (Bad Request)} if the line has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/lines")
    public ResponseEntity<Line> createLine(@RequestBody Line line) throws URISyntaxException {
        log.debug("REST request to save Line : {}", line);
        if (line.getId() != null) {
            throw new BadRequestAlertException("A new line cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Line result = lineService.save(line);
        return ResponseEntity
            .created(new URI("/api/lines/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /lines/:id} : Updates an existing line.
     *
     * @param id the id of the line to save.
     * @param line the line to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated line,
     * or with status {@code 400 (Bad Request)} if the line is not valid,
     * or with status {@code 500 (Internal Server Error)} if the line couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/lines/{id}")
    public ResponseEntity<Line> updateLine(@PathVariable(value = "id", required = false) final Long id, @RequestBody Line line)
        throws URISyntaxException {
        log.debug("REST request to update Line : {}, {}", id, line);
        if (line.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, line.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!lineRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Line result = lineService.update(line);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, line.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /lines/:id} : Partial updates given fields of an existing line, field will ignore if it is null
     *
     * @param id the id of the line to save.
     * @param line the line to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated line,
     * or with status {@code 400 (Bad Request)} if the line is not valid,
     * or with status {@code 404 (Not Found)} if the line is not found,
     * or with status {@code 500 (Internal Server Error)} if the line couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/lines/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Line> partialUpdateLine(@PathVariable(value = "id", required = false) final Long id, @RequestBody Line line)
        throws URISyntaxException {
        log.debug("REST request to partial update Line partially : {}, {}", id, line);
        if (line.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, line.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!lineRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Line> result = lineService.partialUpdate(line);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, line.getId().toString())
        );
    }

    /**
     * {@code GET  /lines} : get all the lines.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of lines in body.
     */
    @GetMapping("/lines")
    public ResponseEntity<List<Line>> getAllLines(LineCriteria criteria, @org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get Lines by criteria: {}", criteria);
        Page<Line> page = lineQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /lines/count} : count all the lines.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/lines/count")
    public ResponseEntity<Long> countLines(LineCriteria criteria) {
        log.debug("REST request to count Lines by criteria: {}", criteria);
        return ResponseEntity.ok().body(lineQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /lines/:id} : get the "id" line.
     *
     * @param id the id of the line to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the line, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/lines/{id}")
    public ResponseEntity<Line> getLine(@PathVariable Long id) {
        log.debug("REST request to get Line : {}", id);
        Optional<Line> line = lineService.findOne(id);
        return ResponseUtil.wrapOrNotFound(line);
    }

    /**
     * {@code DELETE  /lines/:id} : delete the "id" line.
     *
     * @param id the id of the line to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/lines/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        log.debug("REST request to delete Line : {}", id);
        lineService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
