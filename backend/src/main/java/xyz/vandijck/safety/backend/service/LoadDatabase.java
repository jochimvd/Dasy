package xyz.vandijck.safety.backend.service;


import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import xyz.vandijck.safety.backend.SafetyFaker;
import xyz.vandijck.safety.backend.entity.*;
import xyz.vandijck.safety.backend.repository.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
@Slf4j
@Profile({"prod", "dev", "test"})
class LoadDatabase {

    @Autowired
    private Environment environment;

    @Autowired
    private ConsequenceRepository consequenceRepository;

    @Autowired
    private SeverityRepository severityRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ObservationRepository observationRepository;


    abstract static class CSVEntry<T> {
        abstract T toObject();
    }

    @Data
    static class CSVSeverity extends CSVEntry<Severity> {
        private String name;
        private double level;
        private String description;


        public Severity toObject() {
            return new Severity()
                    .setName(name)
                    .setLevel(level)
                    .setDescription(description);
        }
    }

    @Data
    static class CSVConsequence extends CSVEntry<Consequence> {
        private String name;
        private double probability;
        private String description;


        public Consequence toObject() {
            return new Consequence()
                    .setName(name)
                    .setProbability(probability)
                    .setDescription(description);
        }
    }

    @Data
    static class CSVCategory extends CSVEntry<Category> {
        private String name;
        private Long consequence;
        private Long severity;
        private String description;


        public Category toObject() {
            return new Category()
                    .setName(name)
                    .setConsequence(consequence != null ? new Consequence().setId(consequence) : null)
                    .setSeverity(severity != null ? new Severity().setId(severity) : null)
                    .setDescription(description);
        }
    }

    List<User> getMockUsers(SafetyFaker faker, PasswordEncoder encoder) {

        List<User> mockUsers = new ArrayList<>();
        User admin = faker.createUser()
                .setFirstName("Miss")
                .setLastName("Moneypenny")
                .setEmail("admin@example.com")
                .setPassword(encoder.encode("admin"))
                .setRole(Role.ADMIN);
        mockUsers.add(admin);

        User editor = faker.createUser()
                .setFirstName("Coach")
                .setLastName("K")
                .setEmail("editor@example.com")
                .setPassword(encoder.encode("editor"))
                .setRole(Role.EDITOR);
        mockUsers.add(editor);

        User reader = faker.createUser()
                .setFirstName("John")
                .setLastName("Doe")
                .setEmail("user@example.com")
                .setPassword(encoder.encode("reader"))
                .setRole(Role.READER);
        mockUsers.add(reader);

        return mockUsers;
    }

    @Bean
    CommandLineRunner initDatabase(PasswordEncoder passwordEncoder) {
        if (userRepository.count() > 0) return args -> {};

        SafetyFaker faker = new SafetyFaker(locationRepository, false);

        // uncomment this to have a fresh database with only the existing dojos
        // be wary of the mock users that should be removed when running the application with real users
/*        if (Arrays.asList(environment.getActiveProfiles()).contains("prod")) {
            return args -> {
                log.info("Starting database seeding for production");

                List<User> users = getMockUsers(faker, passwordEncoder);
                User dad = users.get(PARENT_INDEX);
                User leader = users.get(LEAD_INDEX);
                User coach = users.get(COACH_INDEX);

                users.add(faker.createChild(dad));

                Set<Tag> allTags = new HashSet<>();
                List<UserDojoRole> userDojoRoles = new ArrayList<>();
                List<Dojo> dojos = loadDojos("dojos.csv");

                dojos.forEach(dojo -> {
                    allTags.addAll(dojo.getTags());
                    dojo.setRegistrationDate(faker.createRegistrationDate());
                    userDojoRoles.add(new UserDojoRole(coach, dojo, Role.COACH));
                    userDojoRoles.add(new UserDojoRole(leader, dojo, Role.LEAD));
                });

                tagRepository.saveAll(allTags);
                tagRepository.flush();

                dojos.forEach(dojo -> dojo.setTags(dojo.getTags().stream().map(t -> tagRepository.findByName(t.getName())).collect(Collectors.toSet())));

                userRepository.saveAll(users);
                dojoRepository.saveAll(dojos);
                userDojoRoleRepository.saveAll(userDojoRoles);
                log.info(String.valueOf(userDojoRoleRepository.findAll().size()));
                log.info("Saved dummy data");
            };
        }*/

        return args -> {
            log.info("Starting database seeding for development");

            List<Location> locations = faker.createLocations();

            List<Severity> severities = loadCSV("data/severities.csv", CSVSeverity.class);

            List<Consequence> consequences = loadCSV("data/consequences.csv", CSVConsequence.class);

            List<Category> categories = loadCSV("data/categories.csv", CSVCategory.class);
            categories.forEach(category -> {
                if (category.getSeverity() == null)
                    category.setSeverity(severities.get(faker.number().numberBetween(0, severities.size())));
                if (category.getConsequence() == null)
                    category.setConsequence(consequences.get(faker.number().numberBetween(0, consequences.size())));
            });

            List<User> users = getMockUsers(faker, passwordEncoder);

            List<Observation> observations = faker.createObservations(users, locations, categories, 100);

            log.info("saving users " + users.size());
            userRepository.saveAll(users);
            userRepository.flush();
            log.info("saving locations " + locations.size());
            locationRepository.saveAll(locations);
            locationRepository.flush();
            log.info("saving severities " + severities.size());
            severityRepository.saveAll(severities);
            severityRepository.flush();
            log.info("saving consequences " + consequences.size());
            consequenceRepository.saveAll(consequences);
            consequenceRepository.flush();
            log.info("saving categories " + categories.size());
            categoryRepository.saveAll(categories);
            categoryRepository.flush();
            log.info("saving observations " + observations.size());
            observationRepository.saveAll(observations);
            observationRepository.flush();

            log.info("Saved dummy data");
        };
    }

    private <T, G extends CSVEntry<T>> List<T> loadCSV(String fileName, Class<G> csvClass) {
        try {
            List<T> ret = new ArrayList<>();

            // Setup CSV reader
            CsvMapper csvMapper = new CsvMapper();
            CsvSchema schema = CsvSchema.emptySchema().withHeader();
            ObjectReader oReader = csvMapper.readerWithSchemaFor(csvClass).with(schema);

            // Read the files
            InputStream resource = new ClassPathResource(fileName).getInputStream();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource))) {
                MappingIterator<G> mi = oReader.readValues(reader);
                while (mi.hasNext()) {
                    ret.add(mi.next().toObject());
                }
            }

            return ret;
        } catch (Exception e) {
            log.error("Error occurred while loading object list from file " + fileName, e);
            return Collections.emptyList();
        }
    }
}