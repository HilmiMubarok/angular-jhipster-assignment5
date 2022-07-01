package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Hero;
import com.mycompany.myapp.repository.HeroRepository;
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
 * Integration tests for the {@link HeroResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class HeroResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/heroes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private HeroRepository heroRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restHeroMockMvc;

    private Hero hero;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Hero createEntity(EntityManager em) {
        Hero hero = new Hero().name(DEFAULT_NAME);
        return hero;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Hero createUpdatedEntity(EntityManager em) {
        Hero hero = new Hero().name(UPDATED_NAME);
        return hero;
    }

    @BeforeEach
    public void initTest() {
        hero = createEntity(em);
    }

    @Test
    @Transactional
    void createHero() throws Exception {
        int databaseSizeBeforeCreate = heroRepository.findAll().size();
        // Create the Hero
        restHeroMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(hero)))
            .andExpect(status().isCreated());

        // Validate the Hero in the database
        List<Hero> heroList = heroRepository.findAll();
        assertThat(heroList).hasSize(databaseSizeBeforeCreate + 1);
        Hero testHero = heroList.get(heroList.size() - 1);
        assertThat(testHero.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void createHeroWithExistingId() throws Exception {
        // Create the Hero with an existing ID
        hero.setId(1L);

        int databaseSizeBeforeCreate = heroRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restHeroMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(hero)))
            .andExpect(status().isBadRequest());

        // Validate the Hero in the database
        List<Hero> heroList = heroRepository.findAll();
        assertThat(heroList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = heroRepository.findAll().size();
        // set the field null
        hero.setName(null);

        // Create the Hero, which fails.

        restHeroMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(hero)))
            .andExpect(status().isBadRequest());

        List<Hero> heroList = heroRepository.findAll();
        assertThat(heroList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllHeroes() throws Exception {
        // Initialize the database
        heroRepository.saveAndFlush(hero);

        // Get all the heroList
        restHeroMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(hero.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getHero() throws Exception {
        // Initialize the database
        heroRepository.saveAndFlush(hero);

        // Get the hero
        restHeroMockMvc
            .perform(get(ENTITY_API_URL_ID, hero.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(hero.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getNonExistingHero() throws Exception {
        // Get the hero
        restHeroMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewHero() throws Exception {
        // Initialize the database
        heroRepository.saveAndFlush(hero);

        int databaseSizeBeforeUpdate = heroRepository.findAll().size();

        // Update the hero
        Hero updatedHero = heroRepository.findById(hero.getId()).get();
        // Disconnect from session so that the updates on updatedHero are not directly saved in db
        em.detach(updatedHero);
        updatedHero.name(UPDATED_NAME);

        restHeroMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedHero.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedHero))
            )
            .andExpect(status().isOk());

        // Validate the Hero in the database
        List<Hero> heroList = heroRepository.findAll();
        assertThat(heroList).hasSize(databaseSizeBeforeUpdate);
        Hero testHero = heroList.get(heroList.size() - 1);
        assertThat(testHero.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void putNonExistingHero() throws Exception {
        int databaseSizeBeforeUpdate = heroRepository.findAll().size();
        hero.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHeroMockMvc
            .perform(
                put(ENTITY_API_URL_ID, hero.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(hero))
            )
            .andExpect(status().isBadRequest());

        // Validate the Hero in the database
        List<Hero> heroList = heroRepository.findAll();
        assertThat(heroList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchHero() throws Exception {
        int databaseSizeBeforeUpdate = heroRepository.findAll().size();
        hero.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHeroMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(hero))
            )
            .andExpect(status().isBadRequest());

        // Validate the Hero in the database
        List<Hero> heroList = heroRepository.findAll();
        assertThat(heroList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamHero() throws Exception {
        int databaseSizeBeforeUpdate = heroRepository.findAll().size();
        hero.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHeroMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(hero)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Hero in the database
        List<Hero> heroList = heroRepository.findAll();
        assertThat(heroList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateHeroWithPatch() throws Exception {
        // Initialize the database
        heroRepository.saveAndFlush(hero);

        int databaseSizeBeforeUpdate = heroRepository.findAll().size();

        // Update the hero using partial update
        Hero partialUpdatedHero = new Hero();
        partialUpdatedHero.setId(hero.getId());

        restHeroMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHero.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedHero))
            )
            .andExpect(status().isOk());

        // Validate the Hero in the database
        List<Hero> heroList = heroRepository.findAll();
        assertThat(heroList).hasSize(databaseSizeBeforeUpdate);
        Hero testHero = heroList.get(heroList.size() - 1);
        assertThat(testHero.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void fullUpdateHeroWithPatch() throws Exception {
        // Initialize the database
        heroRepository.saveAndFlush(hero);

        int databaseSizeBeforeUpdate = heroRepository.findAll().size();

        // Update the hero using partial update
        Hero partialUpdatedHero = new Hero();
        partialUpdatedHero.setId(hero.getId());

        partialUpdatedHero.name(UPDATED_NAME);

        restHeroMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHero.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedHero))
            )
            .andExpect(status().isOk());

        // Validate the Hero in the database
        List<Hero> heroList = heroRepository.findAll();
        assertThat(heroList).hasSize(databaseSizeBeforeUpdate);
        Hero testHero = heroList.get(heroList.size() - 1);
        assertThat(testHero.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingHero() throws Exception {
        int databaseSizeBeforeUpdate = heroRepository.findAll().size();
        hero.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHeroMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, hero.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(hero))
            )
            .andExpect(status().isBadRequest());

        // Validate the Hero in the database
        List<Hero> heroList = heroRepository.findAll();
        assertThat(heroList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchHero() throws Exception {
        int databaseSizeBeforeUpdate = heroRepository.findAll().size();
        hero.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHeroMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(hero))
            )
            .andExpect(status().isBadRequest());

        // Validate the Hero in the database
        List<Hero> heroList = heroRepository.findAll();
        assertThat(heroList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamHero() throws Exception {
        int databaseSizeBeforeUpdate = heroRepository.findAll().size();
        hero.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHeroMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(hero)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Hero in the database
        List<Hero> heroList = heroRepository.findAll();
        assertThat(heroList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteHero() throws Exception {
        // Initialize the database
        heroRepository.saveAndFlush(hero);

        int databaseSizeBeforeDelete = heroRepository.findAll().size();

        // Delete the hero
        restHeroMockMvc
            .perform(delete(ENTITY_API_URL_ID, hero.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Hero> heroList = heroRepository.findAll();
        assertThat(heroList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
