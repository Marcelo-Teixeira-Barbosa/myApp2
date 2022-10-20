package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Board;
import com.mycompany.myapp.domain.Line;
import com.mycompany.myapp.repository.LineRepository;
import com.mycompany.myapp.service.criteria.LineCriteria;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link LineResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class LineResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/lines";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLineMockMvc;

    private Line line;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Line createEntity(EntityManager em) {
        Line line = new Line().title(DEFAULT_TITLE);
        return line;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Line createUpdatedEntity(EntityManager em) {
        Line line = new Line().title(UPDATED_TITLE);
        return line;
    }

    @BeforeEach
    public void initTest() {
        line = createEntity(em);
    }

    @Test
    @Transactional
    void createLine() throws Exception {
        int databaseSizeBeforeCreate = lineRepository.findAll().size();
        // Create the Line
        restLineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(line)))
            .andExpect(status().isCreated());

        // Validate the Line in the database
        List<Line> lineList = lineRepository.findAll();
        assertThat(lineList).hasSize(databaseSizeBeforeCreate + 1);
        Line testLine = lineList.get(lineList.size() - 1);
        assertThat(testLine.getTitle()).isEqualTo(DEFAULT_TITLE);
    }

    @Test
    @Transactional
    void createLineWithExistingId() throws Exception {
        // Create the Line with an existing ID
        line.setId(1L);

        int databaseSizeBeforeCreate = lineRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(line)))
            .andExpect(status().isBadRequest());

        // Validate the Line in the database
        List<Line> lineList = lineRepository.findAll();
        assertThat(lineList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllLines() throws Exception {
        // Initialize the database
        lineRepository.saveAndFlush(line);

        // Get all the lineList
        restLineMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(line.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)));
    }

    @Test
    @Transactional
    void getLine() throws Exception {
        // Initialize the database
        lineRepository.saveAndFlush(line);

        // Get the line
        restLineMockMvc
            .perform(get(ENTITY_API_URL_ID, line.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(line.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE));
    }

    @Test
    @Transactional
    void getLinesByIdFiltering() throws Exception {
        // Initialize the database
        lineRepository.saveAndFlush(line);

        Long id = line.getId();

        defaultLineShouldBeFound("id.equals=" + id);
        defaultLineShouldNotBeFound("id.notEquals=" + id);

        defaultLineShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultLineShouldNotBeFound("id.greaterThan=" + id);

        defaultLineShouldBeFound("id.lessThanOrEqual=" + id);
        defaultLineShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllLinesByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        lineRepository.saveAndFlush(line);

        // Get all the lineList where title equals to DEFAULT_TITLE
        defaultLineShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the lineList where title equals to UPDATED_TITLE
        defaultLineShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllLinesByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        lineRepository.saveAndFlush(line);

        // Get all the lineList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultLineShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the lineList where title equals to UPDATED_TITLE
        defaultLineShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllLinesByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        lineRepository.saveAndFlush(line);

        // Get all the lineList where title is not null
        defaultLineShouldBeFound("title.specified=true");

        // Get all the lineList where title is null
        defaultLineShouldNotBeFound("title.specified=false");
    }

    @Test
    @Transactional
    void getAllLinesByTitleContainsSomething() throws Exception {
        // Initialize the database
        lineRepository.saveAndFlush(line);

        // Get all the lineList where title contains DEFAULT_TITLE
        defaultLineShouldBeFound("title.contains=" + DEFAULT_TITLE);

        // Get all the lineList where title contains UPDATED_TITLE
        defaultLineShouldNotBeFound("title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllLinesByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        lineRepository.saveAndFlush(line);

        // Get all the lineList where title does not contain DEFAULT_TITLE
        defaultLineShouldNotBeFound("title.doesNotContain=" + DEFAULT_TITLE);

        // Get all the lineList where title does not contain UPDATED_TITLE
        defaultLineShouldBeFound("title.doesNotContain=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllLinesByBoardIsEqualToSomething() throws Exception {
        Board board;
        if (TestUtil.findAll(em, Board.class).isEmpty()) {
            lineRepository.saveAndFlush(line);
            board = BoardResourceIT.createEntity(em);
        } else {
            board = TestUtil.findAll(em, Board.class).get(0);
        }
        em.persist(board);
        em.flush();
        line.setBoard(board);
        lineRepository.saveAndFlush(line);
        Long boardId = board.getId();

        // Get all the lineList where board equals to boardId
        defaultLineShouldBeFound("boardId.equals=" + boardId);

        // Get all the lineList where board equals to (boardId + 1)
        defaultLineShouldNotBeFound("boardId.equals=" + (boardId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultLineShouldBeFound(String filter) throws Exception {
        restLineMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(line.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)));

        // Check, that the count call also returns 1
        restLineMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultLineShouldNotBeFound(String filter) throws Exception {
        restLineMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restLineMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingLine() throws Exception {
        // Get the line
        restLineMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLine() throws Exception {
        // Initialize the database
        lineRepository.saveAndFlush(line);

        int databaseSizeBeforeUpdate = lineRepository.findAll().size();

        // Update the line
        Line updatedLine = lineRepository.findById(line.getId()).get();
        // Disconnect from session so that the updates on updatedLine are not directly saved in db
        em.detach(updatedLine);
        updatedLine.title(UPDATED_TITLE);

        restLineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedLine.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedLine))
            )
            .andExpect(status().isOk());

        // Validate the Line in the database
        List<Line> lineList = lineRepository.findAll();
        assertThat(lineList).hasSize(databaseSizeBeforeUpdate);
        Line testLine = lineList.get(lineList.size() - 1);
        assertThat(testLine.getTitle()).isEqualTo(UPDATED_TITLE);
    }

    @Test
    @Transactional
    void putNonExistingLine() throws Exception {
        int databaseSizeBeforeUpdate = lineRepository.findAll().size();
        line.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, line.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(line))
            )
            .andExpect(status().isBadRequest());

        // Validate the Line in the database
        List<Line> lineList = lineRepository.findAll();
        assertThat(lineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLine() throws Exception {
        int databaseSizeBeforeUpdate = lineRepository.findAll().size();
        line.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(line))
            )
            .andExpect(status().isBadRequest());

        // Validate the Line in the database
        List<Line> lineList = lineRepository.findAll();
        assertThat(lineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLine() throws Exception {
        int databaseSizeBeforeUpdate = lineRepository.findAll().size();
        line.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLineMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(line)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Line in the database
        List<Line> lineList = lineRepository.findAll();
        assertThat(lineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLineWithPatch() throws Exception {
        // Initialize the database
        lineRepository.saveAndFlush(line);

        int databaseSizeBeforeUpdate = lineRepository.findAll().size();

        // Update the line using partial update
        Line partialUpdatedLine = new Line();
        partialUpdatedLine.setId(line.getId());

        restLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLine.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLine))
            )
            .andExpect(status().isOk());

        // Validate the Line in the database
        List<Line> lineList = lineRepository.findAll();
        assertThat(lineList).hasSize(databaseSizeBeforeUpdate);
        Line testLine = lineList.get(lineList.size() - 1);
        assertThat(testLine.getTitle()).isEqualTo(DEFAULT_TITLE);
    }

    @Test
    @Transactional
    void fullUpdateLineWithPatch() throws Exception {
        // Initialize the database
        lineRepository.saveAndFlush(line);

        int databaseSizeBeforeUpdate = lineRepository.findAll().size();

        // Update the line using partial update
        Line partialUpdatedLine = new Line();
        partialUpdatedLine.setId(line.getId());

        partialUpdatedLine.title(UPDATED_TITLE);

        restLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLine.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLine))
            )
            .andExpect(status().isOk());

        // Validate the Line in the database
        List<Line> lineList = lineRepository.findAll();
        assertThat(lineList).hasSize(databaseSizeBeforeUpdate);
        Line testLine = lineList.get(lineList.size() - 1);
        assertThat(testLine.getTitle()).isEqualTo(UPDATED_TITLE);
    }

    @Test
    @Transactional
    void patchNonExistingLine() throws Exception {
        int databaseSizeBeforeUpdate = lineRepository.findAll().size();
        line.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, line.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(line))
            )
            .andExpect(status().isBadRequest());

        // Validate the Line in the database
        List<Line> lineList = lineRepository.findAll();
        assertThat(lineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLine() throws Exception {
        int databaseSizeBeforeUpdate = lineRepository.findAll().size();
        line.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(line))
            )
            .andExpect(status().isBadRequest());

        // Validate the Line in the database
        List<Line> lineList = lineRepository.findAll();
        assertThat(lineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLine() throws Exception {
        int databaseSizeBeforeUpdate = lineRepository.findAll().size();
        line.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLineMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(line)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Line in the database
        List<Line> lineList = lineRepository.findAll();
        assertThat(lineList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLine() throws Exception {
        // Initialize the database
        lineRepository.saveAndFlush(line);

        int databaseSizeBeforeDelete = lineRepository.findAll().size();

        // Delete the line
        restLineMockMvc
            .perform(delete(ENTITY_API_URL_ID, line.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Line> lineList = lineRepository.findAll();
        assertThat(lineList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
