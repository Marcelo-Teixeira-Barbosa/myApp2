package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Board;
import com.mycompany.myapp.domain.Line;
import com.mycompany.myapp.repository.BoardRepository;
import com.mycompany.myapp.repository.LineRepository;
import java.util.Arrays;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Board}.
 */
@Service
@Transactional
public class BoardService {

    private final Logger log = LoggerFactory.getLogger(BoardService.class);

    private final BoardRepository boardRepository;

    private final LineRepository lineRepository;

    public BoardService(BoardRepository boardRepository, LineRepository lineRepository) {
        this.boardRepository = boardRepository;
        this.lineRepository = lineRepository;
    }

    /**
     * Save a board.
     *
     * @param board the entity to save.
     * @return the persisted entity.
     */
    public Board save(Board board) {
        log.debug("Request to save Board : {}", board);
        board = boardRepository.save(board);

        for (String title : Arrays.asList("todo", "doing", "done")) {
            Line line = new Line().title(title).board(board);
            lineRepository.save(line);
        }

        //lineRepository.save(line);

        return board;
        //return boardRepository.save(board);
    }

    /**
     * Update a board.
     *
     * @param board the entity to save.
     * @return the persisted entity.
     */
    public Board update(Board board) {
        log.debug("Request to update Board : {}", board);
        return boardRepository.save(board);
    }

    /**
     * Partially update a board.
     *
     * @param board the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Board> partialUpdate(Board board) {
        log.debug("Request to partially update Board : {}", board);

        return boardRepository
            .findById(board.getId())
            .map(existingBoard -> {
                if (board.getTitle() != null) {
                    existingBoard.setTitle(board.getTitle());
                }

                return existingBoard;
            })
            .map(boardRepository::save);
    }

    /**
     * Get all the boards.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Board> findAll(Pageable pageable) {
        log.debug("Request to get all Boards");
        return boardRepository.findAll(pageable);
    }

    /**
     * Get one board by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Board> findOne(Long id) {
        log.debug("Request to get Board : {}", id);
        return boardRepository.findById(id);
    }

    /**
     * Delete the board by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Board : {}", id);
        boardRepository.deleteById(id);
    }
}
