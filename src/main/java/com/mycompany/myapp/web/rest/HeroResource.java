package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Hero;
import com.mycompany.myapp.repository.HeroRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Hero}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class HeroResource {

    private final Logger log = LoggerFactory.getLogger(HeroResource.class);

    private static final String ENTITY_NAME = "hero";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final HeroRepository heroRepository;

    public HeroResource(HeroRepository heroRepository) {
        this.heroRepository = heroRepository;
    }

    /**
     * {@code POST  /heroes} : Create a new hero.
     *
     * @param hero the hero to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new hero, or with status {@code 400 (Bad Request)} if the hero has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/heroes")
    public ResponseEntity<Hero> createHero(@Valid @RequestBody Hero hero) throws URISyntaxException {
        log.debug("REST request to save Hero : {}", hero);
        if (hero.getId() != null) {
            throw new BadRequestAlertException("A new hero cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Hero result = heroRepository.save(hero);
        return ResponseEntity
            .created(new URI("/api/heroes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /heroes/:id} : Updates an existing hero.
     *
     * @param id the id of the hero to save.
     * @param hero the hero to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated hero,
     * or with status {@code 400 (Bad Request)} if the hero is not valid,
     * or with status {@code 500 (Internal Server Error)} if the hero couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/heroes/{id}")
    public ResponseEntity<Hero> updateHero(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Hero hero)
        throws URISyntaxException {
        log.debug("REST request to update Hero : {}, {}", id, hero);
        if (hero.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, hero.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!heroRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Hero result = heroRepository.save(hero);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, hero.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /heroes/:id} : Partial updates given fields of an existing hero, field will ignore if it is null
     *
     * @param id the id of the hero to save.
     * @param hero the hero to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated hero,
     * or with status {@code 400 (Bad Request)} if the hero is not valid,
     * or with status {@code 404 (Not Found)} if the hero is not found,
     * or with status {@code 500 (Internal Server Error)} if the hero couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/heroes/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Hero> partialUpdateHero(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Hero hero
    ) throws URISyntaxException {
        log.debug("REST request to partial update Hero partially : {}, {}", id, hero);
        if (hero.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, hero.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!heroRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Hero> result = heroRepository
            .findById(hero.getId())
            .map(existingHero -> {
                if (hero.getName() != null) {
                    existingHero.setName(hero.getName());
                }

                return existingHero;
            })
            .map(heroRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, hero.getId().toString())
        );
    }

    /**
     * {@code GET  /heroes} : get all the heroes.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of heroes in body.
     */
    @GetMapping("/heroes")
    public ResponseEntity<List<Hero>> getAllHeroes(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Heroes");
        Page<Hero> page = heroRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /heroes/:id} : get the "id" hero.
     *
     * @param id the id of the hero to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the hero, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/heroes/{id}")
    public ResponseEntity<Hero> getHero(@PathVariable Long id) {
        log.debug("REST request to get Hero : {}", id);
        Optional<Hero> hero = heroRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(hero);
    }

    /**
     * {@code DELETE  /heroes/:id} : delete the "id" hero.
     *
     * @param id the id of the hero to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/heroes/{id}")
    public ResponseEntity<Void> deleteHero(@PathVariable Long id) {
        log.debug("REST request to delete Hero : {}", id);
        heroRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
