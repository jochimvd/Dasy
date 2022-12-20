package xyz.vandijck.safety.backend.entity.report;


import lombok.AllArgsConstructor;
import lombok.Data;
import xyz.vandijck.safety.backend.entity.Site;

@Data
@AllArgsConstructor
public class SiteReport {

    Site site;

    long observations;

    long positiveObservations;

}
