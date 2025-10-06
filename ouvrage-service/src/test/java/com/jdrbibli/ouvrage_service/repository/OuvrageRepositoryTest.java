/* package com.jdrbibli.ouvrage_service.repository;

import com.jdrbibli.ouvrage_service.entity.Gamme;
import com.jdrbibli.ouvrage_service.entity.Ouvrage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class OuvrageRepositoryTest {

    @Autowired
    private OuvrageRepository ouvrageRepository;

    @Autowired
    private GammeRepository gammeRepository;

    private Gamme gamme1;
    private Gamme gamme2;

    @BeforeEach
    void setup() {
        // Création de deux gammes
        gamme1 = new Gamme();
        gamme1.setNom("Gamme 1");
        gamme1.setDescription("Description 1");
        gamme1.setOwnerId(1L);
        gamme1 = gammeRepository.save(gamme1);

        gamme2 = new Gamme();
        gamme2.setNom("Gamme 2");
        gamme2.setDescription("Description 2");
        gamme2.setOwnerId(1L);
        gamme2 = gammeRepository.save(gamme2);

        // Création de quelques ouvrages
        Ouvrage o1 = new Ouvrage();
        o1.setTitre("Ouvrage 1");
        o1.setOwnerId(1L);
        o1.setGamme(gamme1);
        ouvrageRepository.save(o1);

        Ouvrage o2 = new Ouvrage();
        o2.setTitre("Ouvrage 2");
        o2.setOwnerId(1L);
        o2.setGamme(gamme1);
        ouvrageRepository.save(o2);

        Ouvrage o3 = new Ouvrage();
        o3.setTitre("Ouvrage 3");
        o3.setOwnerId(2L);
        o3.setGamme(gamme2);
        ouvrageRepository.save(o3);
    }

    @Test
    void testCountByGammeId() {
        long count = ouvrageRepository.countByGammeId(gamme1.getId());
        assertThat(count).isEqualTo(2);
    }

    @Test
    void testFindByOwnerId() {
        List<Ouvrage> ouvrages = ouvrageRepository.findByOwnerId(1L);
        assertThat(ouvrages).hasSize(2);
    }

    @Test
    void testFindByGammeIdAndOwnerId() {
        List<Ouvrage> ouvrages = ouvrageRepository.findByGammeIdAndOwnerId(gamme1.getId(), 1L);
        assertThat(ouvrages).hasSize(2);
    }

    @Test
    void testFindByGammeIdAndIdNot() {
        Ouvrage exclude = ouvrageRepository.findByGammeIdAndOwnerId(gamme1.getId(), 1L).get(0);
        List<Ouvrage> result = ouvrageRepository.findByGammeIdAndIdNot(gamme1.getId(), exclude.getId());
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isNotEqualTo(exclude.getId());
    }

    @Test
    void testDeleteByGammeId() {
        ouvrageRepository.deleteByGammeId(gamme1.getId());
        List<Ouvrage> remaining = ouvrageRepository.findByGammeId(gamme1.getId());
        assertThat(remaining).isEmpty();
    }

    @Test
    void testFindByIdAndOwnerId() {
        Ouvrage o = ouvrageRepository.findByGammeIdAndOwnerId(gamme1.getId(), 1L).get(0);
        Optional<Ouvrage> found = ouvrageRepository.findByIdAndOwnerId(o.getId(), 1L);
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(o.getId());
    }
}
 */