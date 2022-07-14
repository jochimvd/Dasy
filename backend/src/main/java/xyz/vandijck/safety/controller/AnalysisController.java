package xyz.vandijck.safety.controller;


import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import xyz.vandijck.safety.entity.analysis.AnalysisStartEnd;
import xyz.vandijck.safety.entity.analysis.AnalysisYearMonth;
import xyz.vandijck.safety.entity.analysis.CategoryAnalysisRepresentationModelAssembler;
import xyz.vandijck.safety.service.AnalysisService;


@AllArgsConstructor
@RestController
@RequestMapping("/analyses")
public class AnalysisController {

    private final AnalysisService analysisService;

    private final CategoryAnalysisRepresentationModelAssembler categoryAssembler;


    @RequestMapping(value = "/period", method = RequestMethod.GET)
    public CollectionModel<CategoryAnalysisRepresentationModelAssembler.CategoryAnalysisModel>
    get(@Valid @RequestBody AnalysisStartEnd analysisStartEnd) {
        return categoryAssembler.toCollectionModel(
                analysisService.calculateRiskScores(analysisStartEnd.startDate(), analysisStartEnd.endDate()));
    }

    @RequestMapping(value = "/monthly", method = {RequestMethod.GET, RequestMethod.POST})
    public CollectionModel<CategoryAnalysisRepresentationModelAssembler.CategoryAnalysisModel>
    get(@Valid @RequestBody AnalysisYearMonth analysisYearMonth) {
        return categoryAssembler.toCollectionModel(
                analysisService.calculateRiskScore(analysisYearMonth));
    }
}
