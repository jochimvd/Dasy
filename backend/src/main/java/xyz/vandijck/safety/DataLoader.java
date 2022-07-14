package xyz.vandijck.safety;

import com.github.javafaker.Faker;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import xyz.vandijck.safety.entity.category.Category;
import xyz.vandijck.safety.entity.consequence.Consequence;
import xyz.vandijck.safety.entity.location.Location;
import xyz.vandijck.safety.entity.observation.Observation;
import xyz.vandijck.safety.entity.observation.Status;
import xyz.vandijck.safety.entity.severity.Severity;
import xyz.vandijck.safety.repository.*;

// @Profile("dev") // TODO
@Component
public class DataLoader implements ApplicationRunner {

    private final ConsequenceRepository consequenceRepository;

    private final SeverityRepository severityRepository;

    private final CategoryRepository categoryRepository;

    private final LocationRepository locationRepository;

    private final ObservationRepository observationRepository;

    @Autowired
    public DataLoader(ConsequenceRepository consequenceRepository, SeverityRepository severityRepository, CategoryRepository categoryRepository,
                      LocationRepository locationRepository, ObservationRepository observationRepository) {
        this.consequenceRepository = consequenceRepository;
        this.severityRepository = severityRepository;
        this.categoryRepository = categoryRepository;
        this.locationRepository = locationRepository;
        this.observationRepository = observationRepository;
    }


    public void run(ApplicationArguments args) {

        // Consequences

        Consequence consequence1 = new Consequence();
        consequence1.setName("To be expected");
        consequence1.setProbability(10.);
        consequenceRepository.save(consequence1);

        Consequence consequence2 = new Consequence();
        consequence2.setName("Very possible");
        consequence2.setProbability(6.);
        consequenceRepository.save(consequence2);

        Consequence consequence3 = new Consequence();
        consequence3.setName("Unusual but possible");
        consequence3.setProbability(3.);
        consequenceRepository.save(consequence3);

        Consequence consequence4 = new Consequence();
        consequence4.setName("Distantly possible");
        consequence4.setProbability(1.);
        consequenceRepository.save(consequence4);

        Consequence consequence5 = new Consequence();
        consequence5.setName("Plausible but unlikely");
        consequence5.setProbability(.5);
        consequenceRepository.save(consequence5);

        Consequence consequence6 = new Consequence();
        consequence6.setName("Practically impossible");
        consequence6.setProbability(.2);
        consequenceRepository.save(consequence6);

        Consequence consequence7 = new Consequence();
        consequence7.setName("virtually impossible");
        consequence7.setProbability(.1);
        consequenceRepository.save(consequence7);

        List<Consequence> consequences = consequenceRepository.findAll();

        // Severities

        Severity severity1 = new Severity();
        severity1.setName("Catastrofe, ramp (groot aantal doden)");
        severity1.setLevel(100.);
        severityRepository.save(severity1);

        Severity severity2 = new Severity();
        severity2.setName("Ongeval met klein aantal doden");
        severity2.setLevel(40.);
        severityRepository.save(severity2);

        Severity severity3 = new Severity();
        severity3.setName("Dodelijk ongeval (één dode)");
        severity3.setLevel(15.);
        severityRepository.save(severity3);

        Severity severity4 = new Severity();
        severity4.setName("Zeer ernstig (zware letsels en/of blijvende ongeschiktheid)");
        severity4.setLevel(7.);
        severityRepository.save(severity4);

        Severity severity5 = new Severity();
        severity5.setName("Ernstig (tijdelijke ongeschiktheid)");
        severity5.setLevel(3.);
        severityRepository.save(severity5);

        Severity severity6 = new Severity();
        severity6.setName("Licht (met EHBO of medische verzorging)");
        severity6.setLevel(1.);
        severityRepository.save(severity6);

        List<Severity> severities = severityRepository.findAll();

        // Categories

        Category category1 = new Category();
        category1.setName("Standing in the line of fire");
        category1.setSeverity(severity4);
        category1.setConsequence(consequence1);
        categoryRepository.save(category1);

        Category category2 = new Category();
        category2.setName("Not securing at height");
        category2.setSeverity(severity5);
        category2.setConsequence(consequence2);
        categoryRepository.save(category2);

        Category category3 = new Category();
        category3.setName("Infraction against lifting procedure");
        category3.setSeverity(severity6);
        category3.setConsequence(consequence3);
        categoryRepository.save(category3);

        Category category4 = new Category();
        category4.setName("not executing the LMRA (correctly)");
        category4.setSeverity(severity6);
        category4.setConsequence(consequence4);
        categoryRepository.save(category4);

        Category category5 = new Category();
        category5.setName("grinding without handle");
        category5.setSeverity(severity4);
        category5.setConsequence(consequence5);
        categoryRepository.save(category5);

        Category category6 = new Category();
        category6.setName("not following internal traffic rules");
        category6.setSeverity(severity4);
        category6.setConsequence(consequence6);
        categoryRepository.save(category6);

        Category category7 = new Category();
        category7.setName("House keeping NOK");
        category7.setSeverity(severity3);
        category7.setConsequence(consequence2);
        categoryRepository.save(category7);

        Category category8 = new Category();
        category8.setName("insufficient eye protection");
        category8.setSeverity(severity5);
        category8.setConsequence(consequence1);
        categoryRepository.save(category8);

        Category category9 = new Category();
        category9.setName("zone is badly isolated to 3rd parties");
        category9.setSeverity(severity3);
        category9.setConsequence(consequence5);
        categoryRepository.save(category9);

        Category category10 = new Category();
        category10.setName("insufficient breathing protection");
        category10.setSeverity(severity4);
        category10.setConsequence(consequence6);
        categoryRepository.save(category10);

        Category category11 = new Category();
        category11.setName("insuficient hearing protection");
        category11.setSeverity(severity6);
        category11.setConsequence(consequence4);
        categoryRepository.save(category11);

        Category category12 = new Category();
        category12.setName("working without permit");
        category12.setSeverity(severity5);
        category12.setConsequence(consequence3);
        categoryRepository.save(category12);

        Category category13 = new Category();
        category13.setName("not putting on a helmet");
        category13.setSeverity(severity6);
        category13.setConsequence(consequence6);
        categoryRepository.save(category13);

        Category category14 = new Category();
        category14.setName("infraction against Hot work procedure");
        category14.setSeverity(severity4);
        category14.setConsequence(consequence7);
        categoryRepository.save(category14);

        Category category15 = new Category();
        category15.setName("infraction against confined space procedure");
        category15.setSeverity(severity2);
        category15.setConsequence(consequence7);
        categoryRepository.save(category15);

        Category category16 = new Category();
        category16.setName("short sleeves or non comformant clothing");
        category16.setSeverity(severity3);
        category16.setConsequence(consequence4);
        categoryRepository.save(category16);

        Category category17 = new Category();
        category17.setName("not following the method statement");
        category17.setSeverity(severity6);
        category17.setConsequence(consequence1);
        categoryRepository.save(category17);

        List<Category> categories = categoryRepository.findAll();

        // Locations

        Location location1 = new Location();
        location1.setName("Construction site");
        locationRepository.save(location1);

        Location location2 = new Location();
        location2.setName("Z04");
        locationRepository.save(location2);

        Location location3 = new Location();
        location3.setName("Z06");
        locationRepository.save(location3);

        Location location4 = new Location();
        location4.setName("Z22");
        locationRepository.save(location4);

        List<Location> locations = locationRepository.findAll();

        // Observations

        Faker faker = new Faker(new Locale("en-US"), new Random(12345L));

        for (int i = 0; i < 300; i++) {
            Observation observation = new Observation();
            observation.setKey("SOR-HYL-" + i);

            observation.setObserver(
                    faker.name().fullName());

            observation.setObserverCompany(
                    faker.company().name());

            observation.setObservedAt(faker.date().between(
                            Date.from(LocalDate.of(2022, 1, 1)
                                    .atStartOfDay(ZoneId.systemDefault()).toInstant()),
                            Date.from(LocalDate.now()
                                    .atStartOfDay(ZoneId.systemDefault()).toInstant()))
                    .toInstant().atZone(ZoneId.systemDefault()));

            observation.setLocation(
                    locations.get(faker.number().numberBetween(0, locations.size())));

            observation.setObservedCompany(
                    faker.company().name());

            observation.setImmediateDanger(
                    faker.random().nextBoolean());

            observation.setType("Unsafe");

            observation.setCategory(
                    categories.get(faker.number().numberBetween(0, categories.size())));

            observation.setDescription(
                    faker.lorem().sentence());

            observation.setActionsTaken(
                    faker.lorem().sentence());

            observation.setFurtherActions(
                    faker.lorem().sentence());

            observation.setStatus(
                    Status.NEW
            );

            observationRepository.save(observation);
        }

    }
}