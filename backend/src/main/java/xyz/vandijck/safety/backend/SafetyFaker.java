package xyz.vandijck.safety.backend;

import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.vandijck.safety.backend.entity.*;
import xyz.vandijck.safety.backend.entity.common.Status;
import xyz.vandijck.safety.backend.repository.LocationRepository;

import java.time.*;
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

    private LocationRepository locationRepository;

    @Autowired
    public SafetyFaker(LocationRepository locationRepository) {
        this(locationRepository, false);
    }

    /**
     * @param testEncoding If true it will ensure that there is at least one special character in the generated strings
     *                     produced by this class. This will mainly be used during testing to see if the character
     *                     encoding of all the request and responses stay consistent
     */
    public SafetyFaker(LocationRepository locationRepository, boolean testEncoding) {
        super(RANDOM);
        this.locationRepository = locationRepository;
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
        return Instant.ofEpochSecond((random().nextLong() % (max - min)) + min);
    }

    private <T> T choose(T[] choices) {
        return choices[RANDOM.nextInt(choices.length)];
    }

    private <T> T choose(Collection<T> choices) {
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

    public Observation createObservation(User observer, Location location, Category category) {
        return new Observation()
                .setObserver(observer)
                .setObservedAt(between(Instant.now().minus(Duration.ofDays(365L)), Instant.now()).atZone(ZoneId.systemDefault()))
                .setLocation(location)
                .setObservedCompany(company().name())
                .setImmediateDanger(bool().bool())
                .setCategory(category)
                .setDescription(gameOfThrones().quote())
                .setActionsTaken(witcher().quote())
                .setFurtherActions(yoda().quote())
                .setStatus(choose(OBSERVATION_STATUSES));
    }

    public List<Observation> createObservations(List<User> observers, List<Location> locations,
                                                List<Category> categories, int n) {
        List<Observation> observations = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            observations.add(createObservation(
                    choose(observers), choose(locations), choose(categories)
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

    public List<Location> createLocations() {
        return new ArrayList<>(createMany(this::createLocation));
    }

    public Location createLocation() {
        String name = witcher().location();
        Location location = new Location().setName(name);
        if (locationRepository != null) {
            location = locationRepository.save(location);
        }
        return location;
    }




    public LocalDate createRegistrationDate() {
        return getLocalDate(date().past(365, TimeUnit.DAYS).toInstant());
    }



    public boolean chanceOf(float chance) {
        return RANDOM.nextFloat() < chance;
    }

}
