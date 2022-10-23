/**
 * Custom Hibernate normalizer & analyzer for names (in a broad context: people, city, ...).
 * Sees the name as a whole token, converts to lowercase to allow case-insensitive search.
 * It is defined here instead of on the entity interface, because you cannot inherit an AnalyzerDef.
 */
@NormalizerDef(
        name = "asciiSortNormalizer",
        filters = {
                @TokenFilterDef(factory = ASCIIFoldingFilterFactory.class), // To handle diacritics such as "é"
                @TokenFilterDef(factory = LowerCaseFilterFactory.class)
        }
)
@NormalizerDef(
        name = "dateSortNormalizer",
        filters = {
                @TokenFilterDef(factory = StandardFilterFactory.class)
        }
)
@AnalyzerDef(name = "namelike", tokenizer = @TokenizerDef(factory = KeywordTokenizerFactory.class), filters = {
        @TokenFilterDef(factory = ASCIIFoldingFilterFactory.class), // To handle diacritics such as "é"
        @TokenFilterDef(factory = LowerCaseFilterFactory.class),
})
package xyz.vandijck.safety.backend.entity;

import org.apache.lucene.analysis.core.KeywordTokenizerFactory;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilterFactory;
import org.apache.lucene.analysis.standard.StandardFilterFactory;
import org.hibernate.search.annotations.AnalyzerDef;
import org.hibernate.search.annotations.NormalizerDef;
import org.hibernate.search.annotations.TokenFilterDef;
import org.hibernate.search.annotations.TokenizerDef;
