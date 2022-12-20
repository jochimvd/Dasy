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
import xyz.vandijck.safety.backend.entity.common.Status;
import xyz.vandijck.safety.backend.repository.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@Slf4j
@Profile({"prod", "dev", "test"})
class LoadDatabase {

    @Autowired
    private Environment environment;

    @Autowired
    private ReoccurrenceRepository reoccurrenceRepository;

    @Autowired
    private SeverityRepository severityRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private TypeRepository typeRepository;

    @Autowired
    private TypeService typeService;

    @Autowired
    private SiteRepository siteRepository;

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
    static class CSVReoccurrence extends CSVEntry<Reoccurrence> {
        private String name;
        private double rate;
        private String description;


        public Reoccurrence toObject() {
            return new Reoccurrence()
                    .setName(name)
                    .setRate(rate)
                    .setDescription(description);
        }
    }

    @Data
    static class CSVType extends CSVEntry<Type> {
        private String name;
        private boolean notify;


        public Type toObject() {
            return new Type()
                    .setName(name)
                    .setNotify(notify);
        }
    }

    @Data
    static class CSVCategory extends CSVEntry<Category> {
        private String name;
        private Long reoccurrence;
        private Double severity;
        private String description;


        public Category toObject() {
            return new Category()
                    .setName(name)
                    .setReoccurrence(reoccurrence != null ? new Reoccurrence().setId(reoccurrence) : null)
                    .setSeverityLevel(severity)
                    .setDescription(description);
        }
    }

    @Data
    static class CSVObservation extends CSVEntry<Observation> {
        private String observer;
        private String observedAt;
        private String observedCompany;
        private String category;

        private String type;

        private String description;
        private String actionsTaken;
        private String furtherActions;
        private String status;

        private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        private static final Map<String, Status> STATUS = new HashMap<>();
        static {
            STATUS.put("Open", Status.NEW);
            STATUS.put("Closed", Status.DONE);
        }


        public Observation toObject() {
            LocalDate date = LocalDate.parse(observedAt, DateTimeFormatter.ofPattern("d/MM/yyyy"));
            return new Observation()
                    .setObservedAt(ZonedDateTime.of(date.atStartOfDay(), ZoneId.systemDefault()))
                    .setObservedCompany(new Company().setName(observedCompany.isBlank() ? "Unknown" : observedCompany))
                    .setType(new Type().setName(type))
                    .setCategory(new Category().setId(ALPHABET.indexOf(category) + 1))
                    .setDescription(description)
                    .setActionsTaken(actionsTaken)
                    .setFurtherActions(furtherActions)
                    .setStatus(STATUS.getOrDefault(status, Status.IN_PROGRESS));
        }
    }

    List<User> getMockUsers(SafetyFaker faker, PasswordEncoder encoder) {

        List<User> mockUsers = new ArrayList<>();
        User admin = faker.createUser()
                .setFirstName(faker.name().firstName())
                .setLastName(faker.name().lastName())
                .setEmail("admin@dasy.app")
                .setPassword(encoder.encode("dasyadmin"))
                .setRole(Role.ADMIN);
        mockUsers.add(admin);

        User editor = faker.createUser()
                .setFirstName(faker.name().firstName())
                .setLastName(faker.name().lastName())
                .setEmail("editor@dasy.app")
                .setPassword(encoder.encode("dasyeditor"))
                .setRole(Role.EDITOR);
        mockUsers.add(editor);

        User reader = faker.createUser()
                .setFirstName(faker.name().firstName())
                .setLastName(faker.name().lastName())
                .setEmail("reader@dasy.app")
                .setPassword(encoder.encode("dasyreader"))
                .setRole(Role.READER);
        mockUsers.add(reader);

        User demo = faker.createUser()
                .setFirstName(faker.name().firstName())
                .setLastName(faker.name().lastName())
                .setEmail("demo@dasy.app")
                .setPassword(encoder.encode("demo"))
                .setRole(Role.ADMIN);
        mockUsers.add(demo);


        return mockUsers;
    }

    @Bean
    CommandLineRunner initDatabase(PasswordEncoder passwordEncoder) {
        if (userRepository.count() > 0) return args -> {};

        SafetyFaker faker = new SafetyFaker(siteRepository, false);

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

            List<Site> sites = Stream.of("Molecule switch", "Pontus", "HDS", "P2C", "Unity", "Pfizer", "GSK")
                    .map(s -> siteRepository.save(new Site().setName(s))).collect(Collectors.toList());

            List<Type> types = loadCSV("data/types.csv", CSVType.class);

            List<Severity> severities = loadCSV("data/severities.csv", CSVSeverity.class);

            List<Reoccurrence> reoccurrences = loadCSV("data/reoccurrences.csv", CSVReoccurrence.class);

            List<Category> categories = loadCSV("data/categories.csv", CSVCategory.class);
            categories.forEach(category -> {
                if (category.getSeverity() == null)
                    category.setSeverity(faker.choose(severities));
                if (category.getReoccurrence() == null)
                    category.setReoccurrence(faker.choose(reoccurrences));
            });

            List<User> users = getMockUsers(faker, passwordEncoder);

//            List<Observation> observations = faker.createObservations(users, companies, sites, categories, 100);

            List<Observation> observations = loadCSV("data/observations.csv", CSVObservation.class, ";");
            observations.forEach(observation -> {
                observation
                        .setObserver(faker.choose(users))
                        .setSite(faker.choose(sites))
                        .setImmediateDanger(faker.bool().bool());
            });

            List<Company> companies = observations.stream().map(Observation::getObservedCompany).toList();

            log.info("saving companies " + companies.size());
            companies.forEach(company -> companyService.findElseCreate(company.getName()));
            companyRepository.flush();
            log.info("saving types " + types.size());
            typeRepository.saveAll(types);
            typeRepository.flush();

            observations.forEach(observation ->
                    observation.setObservedCompany(companyRepository.findByName(observation.getObservedCompany().getName())));

            observations.forEach(observation ->
                    observation.setType(typeRepository.findByName(observation.getType().getName())));

            users.forEach(user ->
                    user.setCompany(faker.choose(companyRepository.findAll())));

            log.info("saving users " + users.size());
            userRepository.saveAll(users);
            userRepository.flush();
            log.info("saving sites " + sites.size());
            siteRepository.saveAll(sites);
            siteRepository.flush();
            log.info("saving severities " + severities.size());
            severityRepository.saveAll(severities);
            severityRepository.flush();
            log.info("saving reoccurrences " + reoccurrences.size());
            reoccurrenceRepository.saveAll(reoccurrences);
            reoccurrenceRepository.flush();
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
        return loadCSV(fileName, csvClass, ",");
    }

    private <T, G extends CSVEntry<T>> List<T> loadCSV(String fileName, Class<G> csvClass, String separator) {
        try {
            List<T> ret = new ArrayList<>();

            // Setup CSV reader
            CsvMapper csvMapper = new CsvMapper();
            CsvSchema schema = CsvSchema.emptySchema().withHeader().withColumnSeparator(separator.charAt(0));
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