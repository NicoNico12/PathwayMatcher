package no.uib.pathwaymatcher.Preprocessing;

import no.uib.pathwaymatcher.PathwayMatcher;
import no.uib.pathwaymatcher.model.Proteoform;

import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;

import static no.uib.pathwaymatcher.PathwayMatcher.logger;
import static no.uib.pathwaymatcher.model.Error.ERROR_INITIALIZING_PEPTIDE_MAPPER;
import static no.uib.pathwaymatcher.model.Warning.*;
import static no.uib.pathwaymatcher.util.InputPatterns.matches_Peptite;

public class PreprocessorPeptides extends Preprocessor {

    public TreeSet<Proteoform> process(List<String> input) throws java.text.ParseException {
        //Note: In this function the duplicate protein identifiers are removed by adding the whole input list to a set.
        logger.log(Level.INFO, "\nPreprocessing input file...");

        PathwayMatcher.logger.log(Level.INFO, "\nLoading peptide mapper...");
        if (!compomics.utilities.PeptideMapping.initializePeptideMapper()) {
            System.out.println(ERROR_INITIALIZING_PEPTIDE_MAPPER.getMessage());
            System.exit(ERROR_INITIALIZING_PEPTIDE_MAPPER.getCode());
        }
        PathwayMatcher.logger.log(Level.INFO, "\nLoading peptide mapper complete.");

        TreeSet<Proteoform> entities = new TreeSet<>();
        int row = 1;
        for (String line : input) {
            line = line.trim();
            row++;
            if (matches_Peptite(line)) {
                //Process line
                for (String id : compomics.utilities.PeptideMapping.getPeptideMapping(line)) {
                    entities.add(new Proteoform(id));
                }
            } else {
                if (line.isEmpty()) sendWarning(EMPTY_ROW, row);
                else sendWarning(INVALID_ROW, row);
            }
        }
        return entities;
    }
}
