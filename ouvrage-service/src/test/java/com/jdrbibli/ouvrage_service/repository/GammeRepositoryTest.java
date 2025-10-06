/* package com.jdrbibli.ouvrage_service.repository;

import com.jdrbibli.ouvrage_service.entity.Gamme;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test") // utilise application-test.yml
public class GammeRepositoryTest {

    @Autowired
    private GammeRepository gammeRepository;

    @Test
    public void testSaveAndFindById() {
        Gamme g = new Gamme();
        g.setNom("Gamme A");
        g.setDescription("Description A");
        g.setOwnerId(1L);

        Gamme saved = gammeRepository.save(g);

        assertThat(saved.getId()).isNotNull();

        Gamme found = gammeRepository.findById(saved.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getNom()).isEqualTo("Gamme A");
    }

    @Test
    public void testFindByOwnerId() {
        Gamme g1 = new Gamme();
        g1.setNom("Gamme B");
        g1.setDescription("Description B");
        g1.setOwnerId(2L);

        Gamme g2 = new Gamme();
        g2.setNom("Gamme C");
        g2.setDescription("Description C");
        g2.setOwnerId(2L);

        gammeRepository.save(g1);
        gammeRepository.save(g2);

        List<Gamme> list = gammeRepository.findByOwnerId(2L);
        assertThat(list).hasSize(2);
    }

    @Test
    public void testDelete() {
        Gamme g = new Gamme();
        g.setNom("Gamme D");
        g.setDescription("Description D");
        g.setOwnerId(3L);

        Gamme saved = gammeRepository.save(g);

        gammeRepository.delete(saved);

        assertThat(gammeRepository.findById(saved.getId())).isEmpty();
    }
}
 */