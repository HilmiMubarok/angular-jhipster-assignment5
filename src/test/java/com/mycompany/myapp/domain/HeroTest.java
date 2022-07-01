package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class HeroTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Hero.class);
        Hero hero1 = new Hero();
        hero1.setId(1L);
        Hero hero2 = new Hero();
        hero2.setId(hero1.getId());
        assertThat(hero1).isEqualTo(hero2);
        hero2.setId(2L);
        assertThat(hero1).isNotEqualTo(hero2);
        hero1.setId(null);
        assertThat(hero1).isNotEqualTo(hero2);
    }
}
