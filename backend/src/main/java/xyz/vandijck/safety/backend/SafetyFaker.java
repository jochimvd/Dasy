package xyz.vandijck.safety.backend;

import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.vandijck.safety.backend.entity.*;
import xyz.vandijck.safety.backend.entity.common.Status;
import xyz.vandijck.safety.backend.repository.CompanyRepository;
import xyz.vandijck.safety.backend.repository.SiteRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Extension of the Faker class that provides instances for the safety application
 */
@Component
public class SafetyFaker extends Faker {
    private static final Long SEED = 102309378413294L;
    private static final Random RANDOM = new Random(SEED);

    private static final Status[] OBSERVATION_STATUSES = Status.values();

    private boolean testEncoding;

    private CompanyRepository companyRepository;

    private SiteRepository siteRepository;

    @Autowired
    public SafetyFaker(SiteRepository siteRepository) {
        this(siteRepository, false);
    }

    /**
     * @param testEncoding If true it will ensure that there is at least one special character in the generated strings
     *                     produced by this class. This will mainly be used during testing to see if the character
     *                     encoding of all the request and responses stay consistent
     */
    public SafetyFaker(SiteRepository siteRepository, boolean testEncoding) {
        super(RANDOM);
        this.siteRepository = siteRepository;
        this.testEncoding = testEncoding;
    }

    public void setTestEncoding(boolean testEncoding) {
        this.testEncoding = testEncoding;
    }

    /**
     * Return the same string but depending on {@link SafetyFaker#testEncoding} special characters will be appended
     *
     * @param string the string to be changed
     * @return a changed string
     */
    private String changeString(String string) {
        if (this.testEncoding) {
            return string + " à";
        } else {
            return string;
        }
    }

    /**
     * Determines a random time-Instant between the given values
     *
     * @param startInclusive The minimum value, inclusive
     * @param endExclusive   The maximum value, exclusive
     * @return A random Instant between both values
     */
    public Instant between(Instant startInclusive, Instant endExclusive) {
        long min = startInclusive.getEpochSecond();
        long max = endExclusive.getEpochSecond();
        return Instant.ofEpochSecond(min + random().nextInt((int) (max - min)));
    }

    public <T> T choose(T[] choices) {
        return choices[RANDOM.nextInt(choices.length)];
    }

    public <T> T choose(Collection<T> choices) {
        return choices.stream()
                .skip(RANDOM.nextInt(choices.size()))
                .findFirst().get();
    }

    /**
     * Converts a given Instant to a LocalDate instance
     *
     * @param instant The instant to convert
     * @return The LocalDate version of the instant
     */
    private LocalDate getLocalDate(Instant instant) {
        return LocalDate.ofInstant(instant, ZoneId.systemDefault());
    }

    /**
     * Converts a given Instant to a LocalDateTime instance
     *
     * @param instant The instant to convert
     * @return The LocalDateTime version of the instant
     */
    private LocalDateTime getLocalDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    /**
     * Create a List of length 2 to 5 from the given Supplier
     *
     * @param supplier A function producing values of class E
     * @param <E>      The class of the produced entities
     * @return The constructed List
     */
    public <E> List<E> createMany(Supplier<E> supplier) {
        return createMany(supplier, 2, 5);
    }

    /**
     * Creates a list of length min to max from the given Supplier
     *
     * @param supplier A function producing values of class E
     * @param min      The minimum length of the list
     * @param max      The maximum length
     * @param <E>      The class of the produced entities
     * @return The constructed list
     */
    public <E> List<E> createMany(Supplier<E> supplier, int min, int max) {
        return RANDOM.ints(random().nextInt(min, max))
                .mapToObj(i -> supplier.get())
                .collect(Collectors.toList());
    }


    /**
     * Creates a User, initialized with random possible data
     *
     * @return a User for testing
     */
    public User createUser() {
        String name = changeString(harryPotter().character());
        String[] parts = name.split(" ");

        return new User()
                .setFirstName(parts[0])
                .setLastName(parts.length > 1 ? parts[1] : "Nameless")
                .setEmail(internet().emailAddress())
                .setPassword(internet().password())
                .setAddress(createAddress())
                .setRole(Role.READER)
                .setPhoneNumber(phoneNumber().phoneNumber())
                .setPicturePermission(bool().bool())
                .setSpecialInfo(chuckNorris().fact())
                .setRegistrationDate(createRegistrationDate())
                .setActivated(true);
    }

    public Observation createObservation(User observer, Company company, Site site, Category category) {
        return new Observation()
                .setObserver(observer)
                .setObservedAt(between(Instant.ofEpochSecond(1641001509), Instant.now()).atZone(ZoneId.systemDefault()))
                .setSite(site)
                .setObservedCompany(company)
                .setImmediateDanger(bool().bool())
                .setCategory(category)
                .setDescription(gameOfThrones().quote())
                .setActionsTaken(witcher().quote())
                .setFurtherActions(yoda().quote())
                .setStatus(choose(OBSERVATION_STATUSES));
    }

    public List<Observation> createObservations(List<User> observers,
                                                List<Company> companies,
                                                List<Site> sites,
                                                List<Category> categories, int n) {
        List<Observation> observations = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            observations.add(createObservation(
                    choose(observers), choose(companies), choose(sites), choose(categories)
            ));
        }
        return observations;
    }

    /**
     * Creates an address with random possible data
     *
     * @return an address for testing
     */
    public Address createAddress() {
        com.github.javafaker.Address addressFaker = address();
        return new Address().setCity(addressFaker.city())
                .setCountry(addressFaker.country())
                .setStreetWithNumber(addressFaker.streetName() + " " + addressFaker.streetAddressNumber())
                .setZipCode(addressFaker.zipCode());
    }

    public List<Company> createCompanies() {
        return new ArrayList<>(createMany(this::createCompany, 8 , 12));
    }

    public List<Site> createSites() {
        return new ArrayList<>(createMany(this::createSite));
    }

    public Company createCompany() {
        String name = company().name();
        Company company = new Company().setName(name);
        if (companyRepository != null) {
            company = companyRepository.save(company);
        }
        return company;
    }

    public Site createSite(String name) {
        Site site = new Site().setName(name);
        if (siteRepository != null) {
            site = siteRepository.save(site);
        }
        return site;
    }

    public Site createSite() {
        return createSite(witcher().location());
    }




    public LocalDate createRegistrationDate() {
        return getLocalDate(date().past(365, TimeUnit.DAYS).toInstant());
    }



    public boolean chanceOf(float chance) {
        return RANDOM.nextFloat() < chance;
    }

}
