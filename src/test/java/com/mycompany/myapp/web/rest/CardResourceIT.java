package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Card;
import com.mycompany.myapp.domain.Line;
import com.mycompany.myapp.repository.CardRepository;
import com.mycompany.myapp.service.criteria.CardCriteria;
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
 * Integration tests for the {@link CardResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CardResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final Integer DEFAULT_LEVEL = 1;
    private static final Integer UPDATED_LEVEL = 2;
    private static final Integer SMALLER_LEVEL = 1 - 1;

    private static final String DEFAULT_DESC = "AAAAAAAAAA";
    private static final String UPDATED_DESC = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/cards";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCardMockMvc;

    private Card card;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Card createEntity(EntityManager em) {
        Card card = new Card().title(DEFAULT_TITLE).level(DEFAULT_LEVEL).desc(DEFAULT_DESC);
        return card;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Card createUpdatedEntity(EntityManager em) {
        Card card = new Card().title(UPDATED_TITLE).level(UPDATED_LEVEL).desc(UPDATED_DESC);
        return card;
    }

    @BeforeEach
    public void initTest() {
        card = createEntity(em);
    }

    @Test
    @Transactional
    void createCard() throws Exception {
        int databaseSizeBeforeCreate = cardRepository.findAll().size();
        // Create the Card
        restCardMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(card)))
            .andExpect(status().isCreated());

        // Validate the Card in the database
        List<Card> cardList = cardRepository.findAll();
        assertThat(cardList).hasSize(databaseSizeBeforeCreate + 1);
        Card testCard = cardList.get(cardList.size() - 1);
        assertThat(testCard.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testCard.getLevel()).isEqualTo(DEFAULT_LEVEL);
        assertThat(testCard.getDesc()).isEqualTo(DEFAULT_DESC);
    }

    @Test
    @Transactional
    void createCardWithExistingId() throws Exception {
        // Create the Card with an existing ID
        card.setId(1L);

        int databaseSizeBeforeCreate = cardRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCardMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(card)))
            .andExpect(status().isBadRequest());

        // Validate the Card in the database
        List<Card> cardList = cardRepository.findAll();
        assertThat(cardList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCards() throws Exception {
        // Initialize the database
        cardRepository.saveAndFlush(card);

        // Get all the cardList
        restCardMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(card.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].level").value(hasItem(DEFAULT_LEVEL)))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC)));
    }

    @Test
    @Transactional
    void getCard() throws Exception {
        // Initialize the database
        cardRepository.saveAndFlush(card);

        // Get the card
        restCardMockMvc
            .perform(get(ENTITY_API_URL_ID, card.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(card.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.level").value(DEFAULT_LEVEL))
            .andExpect(jsonPath("$.desc").value(DEFAULT_DESC));
    }

    @Test
    @Transactional
    void getCardsByIdFiltering() throws Exception {
        // Initialize the database
        cardRepository.saveAndFlush(card);

        Long id = card.getId();

        defaultCardShouldBeFound("id.equals=" + id);
        defaultCardShouldNotBeFound("id.notEquals=" + id);

        defaultCardShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultCardShouldNotBeFound("id.greaterThan=" + id);

        defaultCardShouldBeFound("id.lessThanOrEqual=" + id);
        defaultCardShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCardsByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        cardRepository.saveAndFlush(card);

        // Get all the cardList where title equals to DEFAULT_TITLE
        defaultCardShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the cardList where title equals to UPDATED_TITLE
        defaultCardShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllCardsByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        cardRepository.saveAndFlush(card);

        // Get all the cardList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultCardShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the cardList where title equals to UPDATED_TITLE
        defaultCardShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllCardsByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        cardRepository.saveAndFlush(card);

        // Get all the cardList where title is not null
        defaultCardShouldBeFound("title.specified=true");

        // Get all the cardList where title is null
        defaultCardShouldNotBeFound("title.specified=false");
    }

    @Test
    @Transactional
    void getAllCardsByTitleContainsSomething() throws Exception {
        // Initialize the database
        cardRepository.saveAndFlush(card);

        // Get all the cardList where title contains DEFAULT_TITLE
        defaultCardShouldBeFound("title.contains=" + DEFAULT_TITLE);

        // Get all the cardList where title contains UPDATED_TITLE
        defaultCardShouldNotBeFound("title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllCardsByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        cardRepository.saveAndFlush(card);

        // Get all the cardList where title does not contain DEFAULT_TITLE
        defaultCardShouldNotBeFound("title.doesNotContain=" + DEFAULT_TITLE);

        // Get all the cardList where title does not contain UPDATED_TITLE
        defaultCardShouldBeFound("title.doesNotContain=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllCardsByLevelIsEqualToSomething() throws Exception {
        // Initialize the database
        cardRepository.saveAndFlush(card);

        // Get all the cardList where level equals to DEFAULT_LEVEL
        defaultCardShouldBeFound("level.equals=" + DEFAULT_LEVEL);

        // Get all the cardList where level equals to UPDATED_LEVEL
        defaultCardShouldNotBeFound("level.equals=" + UPDATED_LEVEL);
    }

    @Test
    @Transactional
    void getAllCardsByLevelIsInShouldWork() throws Exception {
        // Initialize the database
        cardRepository.saveAndFlush(card);

        // Get all the cardList where level in DEFAULT_LEVEL or UPDATED_LEVEL
        defaultCardShouldBeFound("level.in=" + DEFAULT_LEVEL + "," + UPDATED_LEVEL);

        // Get all the cardList where level equals to UPDATED_LEVEL
        defaultCardShouldNotBeFound("level.in=" + UPDATED_LEVEL);
    }

    @Test
    @Transactional
    void getAllCardsByLevelIsNullOrNotNull() throws Exception {
        // Initialize the database
        cardRepository.saveAndFlush(card);

        // Get all the cardList where level is not null
        defaultCardShouldBeFound("level.specified=true");

        // Get all the cardList where level is null
        defaultCardShouldNotBeFound("level.specified=false");
    }

    @Test
    @Transactional
    void getAllCardsByLevelIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        cardRepository.saveAndFlush(card);

        // Get all the cardList where level is greater than or equal to DEFAULT_LEVEL
        defaultCardShouldBeFound("level.greaterThanOrEqual=" + DEFAULT_LEVEL);

        // Get all the cardList where level is greater than or equal to UPDATED_LEVEL
        defaultCardShouldNotBeFound("level.greaterThanOrEqual=" + UPDATED_LEVEL);
    }

    @Test
    @Transactional
    void getAllCardsByLevelIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        cardRepository.saveAndFlush(card);

        // Get all the cardList where level is less than or equal to DEFAULT_LEVEL
        defaultCardShouldBeFound("level.lessThanOrEqual=" + DEFAULT_LEVEL);

        // Get all the cardList where level is less than or equal to SMALLER_LEVEL
        defaultCardShouldNotBeFound("level.lessThanOrEqual=" + SMALLER_LEVEL);
    }

    @Test
    @Transactional
    void getAllCardsByLevelIsLessThanSomething() throws Exception {
        // Initialize the database
        cardRepository.saveAndFlush(card);

        // Get all the cardList where level is less than DEFAULT_LEVEL
        defaultCardShouldNotBeFound("level.lessThan=" + DEFAULT_LEVEL);

        // Get all the cardList where level is less than UPDATED_LEVEL
        defaultCardShouldBeFound("level.lessThan=" + UPDATED_LEVEL);
    }

    @Test
    @Transactional
    void getAllCardsByLevelIsGreaterThanSomething() throws Exception {
        // Initialize the database
        cardRepository.saveAndFlush(card);

        // Get all the cardList where level is greater than DEFAULT_LEVEL
        defaultCardShouldNotBeFound("level.greaterThan=" + DEFAULT_LEVEL);

        // Get all the cardList where level is greater than SMALLER_LEVEL
        defaultCardShouldBeFound("level.greaterThan=" + SMALLER_LEVEL);
    }

    @Test
    @Transactional
    void getAllCardsByDescIsEqualToSomething() throws Exception {
        // Initialize the database
        cardRepository.saveAndFlush(card);

        // Get all the cardList where desc equals to DEFAULT_DESC
        defaultCardShouldBeFound("desc.equals=" + DEFAULT_DESC);

        // Get all the cardList where desc equals to UPDATED_DESC
        defaultCardShouldNotBeFound("desc.equals=" + UPDATED_DESC);
    }

    @Test
    @Transactional
    void getAllCardsByDescIsInShouldWork() throws Exception {
        // Initialize the database
        cardRepository.saveAndFlush(card);

        // Get all the cardList where desc in DEFAULT_DESC or UPDATED_DESC
        defaultCardShouldBeFound("desc.in=" + DEFAULT_DESC + "," + UPDATED_DESC);

        // Get all the cardList where desc equals to UPDATED_DESC
        defaultCardShouldNotBeFound("desc.in=" + UPDATED_DESC);
    }

    @Test
    @Transactional
    void getAllCardsByDescIsNullOrNotNull() throws Exception {
        // Initialize the database
        cardRepository.saveAndFlush(card);

        // Get all the cardList where desc is not null
        defaultCardShouldBeFound("desc.specified=true");

        // Get all the cardList where desc is null
        defaultCardShouldNotBeFound("desc.specified=false");
    }

    @Test
    @Transactional
    void getAllCardsByDescContainsSomething() throws Exception {
        // Initialize the database
        cardRepository.saveAndFlush(card);

        // Get all the cardList where desc contains DEFAULT_DESC
        defaultCardShouldBeFound("desc.contains=" + DEFAULT_DESC);

        // Get all the cardList where desc contains UPDATED_DESC
        defaultCardShouldNotBeFound("desc.contains=" + UPDATED_DESC);
    }

    @Test
    @Transactional
    void getAllCardsByDescNotContainsSomething() throws Exception {
        // Initialize the database
        cardRepository.saveAndFlush(card);

        // Get all the cardList where desc does not contain DEFAULT_DESC
        defaultCardShouldNotBeFound("desc.doesNotContain=" + DEFAULT_DESC);

        // Get all the cardList where desc does not contain UPDATED_DESC
        defaultCardShouldBeFound("desc.doesNotContain=" + UPDATED_DESC);
    }

    @Test
    @Transactional
    void getAllCardsByLineIsEqualToSomething() throws Exception {
        Line line;
        if (TestUtil.findAll(em, Line.class).isEmpty()) {
            cardRepository.saveAndFlush(card);
            line = LineResourceIT.createEntity(em);
        } else {
            line = TestUtil.findAll(em, Line.class).get(0);
        }
        em.persist(line);
        em.flush();
        card.setLine(line);
        cardRepository.saveAndFlush(card);
        Long lineId = line.getId();

        // Get all the cardList where line equals to lineId
        defaultCardShouldBeFound("lineId.equals=" + lineId);

        // Get all the cardList where line equals to (lineId + 1)
        defaultCardShouldNotBeFound("lineId.equals=" + (lineId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCardShouldBeFound(String filter) throws Exception {
        restCardMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(card.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].level").value(hasItem(DEFAULT_LEVEL)))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC)));

        // Check, that the count call also returns 1
        restCardMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCardShouldNotBeFound(String filter) throws Exception {
        restCardMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCardMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCard() throws Exception {
        // Get the card
        restCardMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCard() throws Exception {
        // Initialize the database
        cardRepository.saveAndFlush(card);

        int databaseSizeBeforeUpdate = cardRepository.findAll().size();

        // Update the card
        Card updatedCard = cardRepository.findById(card.getId()).get();
        // Disconnect from session so that the updates on updatedCard are not directly saved in db
        em.detach(updatedCard);
        updatedCard.title(UPDATED_TITLE).level(UPDATED_LEVEL).desc(UPDATED_DESC);

        restCardMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCard.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCard))
            )
            .andExpect(status().isOk());

        // Validate the Card in the database
        List<Card> cardList = cardRepository.findAll();
        assertThat(cardList).hasSize(databaseSizeBeforeUpdate);
        Card testCard = cardList.get(cardList.size() - 1);
        assertThat(testCard.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testCard.getLevel()).isEqualTo(UPDATED_LEVEL);
        assertThat(testCard.getDesc()).isEqualTo(UPDATED_DESC);
    }

    @Test
    @Transactional
    void putNonExistingCard() throws Exception {
        int databaseSizeBeforeUpdate = cardRepository.findAll().size();
        card.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCardMockMvc
            .perform(
                put(ENTITY_API_URL_ID, card.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(card))
            )
            .andExpect(status().isBadRequest());

        // Validate the Card in the database
        List<Card> cardList = cardRepository.findAll();
        assertThat(cardList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCard() throws Exception {
        int databaseSizeBeforeUpdate = cardRepository.findAll().size();
        card.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCardMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(card))
            )
            .andExpect(status().isBadRequest());

        // Validate the Card in the database
        List<Card> cardList = cardRepository.findAll();
        assertThat(cardList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCard() throws Exception {
        int databaseSizeBeforeUpdate = cardRepository.findAll().size();
        card.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCardMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(card)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Card in the database
        List<Card> cardList = cardRepository.findAll();
        assertThat(cardList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCardWithPatch() throws Exception {
        // Initialize the database
        cardRepository.saveAndFlush(card);

        int databaseSizeBeforeUpdate = cardRepository.findAll().size();

        // Update the card using partial update
        Card partialUpdatedCard = new Card();
        partialUpdatedCard.setId(card.getId());

        partialUpdatedCard.title(UPDATED_TITLE);

        restCardMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCard.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCard))
            )
            .andExpect(status().isOk());

        // Validate the Card in the database
        List<Card> cardList = cardRepository.findAll();
        assertThat(cardList).hasSize(databaseSizeBeforeUpdate);
        Card testCard = cardList.get(cardList.size() - 1);
        assertThat(testCard.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testCard.getLevel()).isEqualTo(DEFAULT_LEVEL);
        assertThat(testCard.getDesc()).isEqualTo(DEFAULT_DESC);
    }

    @Test
    @Transactional
    void fullUpdateCardWithPatch() throws Exception {
        // Initialize the database
        cardRepository.saveAndFlush(card);

        int databaseSizeBeforeUpdate = cardRepository.findAll().size();

        // Update the card using partial update
        Card partialUpdatedCard = new Card();
        partialUpdatedCard.setId(card.getId());

        partialUpdatedCard.title(UPDATED_TITLE).level(UPDATED_LEVEL).desc(UPDATED_DESC);

        restCardMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCard.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCard))
            )
            .andExpect(status().isOk());

        // Validate the Card in the database
        List<Card> cardList = cardRepository.findAll();
        assertThat(cardList).hasSize(databaseSizeBeforeUpdate);
        Card testCard = cardList.get(cardList.size() - 1);
        assertThat(testCard.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testCard.getLevel()).isEqualTo(UPDATED_LEVEL);
        assertThat(testCard.getDesc()).isEqualTo(UPDATED_DESC);
    }

    @Test
    @Transactional
    void patchNonExistingCard() throws Exception {
        int databaseSizeBeforeUpdate = cardRepository.findAll().size();
        card.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCardMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, card.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(card))
            )
            .andExpect(status().isBadRequest());

        // Validate the Card in the database
        List<Card> cardList = cardRepository.findAll();
        assertThat(cardList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCard() throws Exception {
        int databaseSizeBeforeUpdate = cardRepository.findAll().size();
        card.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCardMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(card))
            )
            .andExpect(status().isBadRequest());

        // Validate the Card in the database
        List<Card> cardList = cardRepository.findAll();
        assertThat(cardList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCard() throws Exception {
        int databaseSizeBeforeUpdate = cardRepository.findAll().size();
        card.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCardMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(card)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Card in the database
        List<Card> cardList = cardRepository.findAll();
        assertThat(cardList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCard() throws Exception {
        // Initialize the database
        cardRepository.saveAndFlush(card);

        int databaseSizeBeforeDelete = cardRepository.findAll().size();

        // Delete the card
        restCardMockMvc
            .perform(delete(ENTITY_API_URL_ID, card.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Card> cardList = cardRepository.findAll();
        assertThat(cardList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
