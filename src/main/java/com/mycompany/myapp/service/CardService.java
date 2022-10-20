package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Card;
import com.mycompany.myapp.repository.CardRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Card}.
 */
@Service
@Transactional
public class CardService {

    private final Logger log = LoggerFactory.getLogger(CardService.class);

    private final CardRepository cardRepository;

    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    /**
     * Save a card.
     *
     * @param card the entity to save.
     * @return the persisted entity.
     */
    public Card save(Card card) {
        log.debug("Request to save Card : {}", card);
        return cardRepository.save(card);
    }

    /**
     * Update a card.
     *
     * @param card the entity to save.
     * @return the persisted entity.
     */
    public Card update(Card card) {
        log.debug("Request to update Card : {}", card);
        return cardRepository.save(card);
    }

    /**
     * Partially update a card.
     *
     * @param card the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Card> partialUpdate(Card card) {
        log.debug("Request to partially update Card : {}", card);

        return cardRepository
            .findById(card.getId())
            .map(existingCard -> {
                if (card.getTitle() != null) {
                    existingCard.setTitle(card.getTitle());
                }
                if (card.getLevel() != null) {
                    existingCard.setLevel(card.getLevel());
                }
                if (card.getDesc() != null) {
                    existingCard.setDesc(card.getDesc());
                }

                return existingCard;
            })
            .map(cardRepository::save);
    }

    /**
     * Get all the cards.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Card> findAll(Pageable pageable) {
        log.debug("Request to get all Cards");
        return cardRepository.findAll(pageable);
    }

    /**
     * Get one card by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Card> findOne(Long id) {
        log.debug("Request to get Card : {}", id);
        return cardRepository.findById(id);
    }

    /**
     * Delete the card by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Card : {}", id);
        cardRepository.deleteById(id);
    }
}
