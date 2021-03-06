package no.uib.pathwaymatcher.Preprocessing;

import no.uib.pathwaymatcher.Conf;
import no.uib.pathwaymatcher.model.Proteoform;
import no.uib.pathwaymatcher.model.Snp;

import java.io.FileNotFoundException;
import java.nio.file.NoSuchFileException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;

import static no.uib.pathwaymatcher.Conf.strMap;
import static no.uib.pathwaymatcher.PathwayMatcher.logger;
import static no.uib.pathwaymatcher.model.Error.*;
import static no.uib.pathwaymatcher.model.Warning.*;
import static no.uib.pathwaymatcher.util.InputPatterns.matches_Vcf_Record;

public class PreprocessorVCF extends PreprocessorVariants {

    @Override
    public TreeSet<Proteoform> process(List<String> input) throws ParseException {

        logger.log(Level.INFO, "\nPreprocessing input file...");
        TreeSet<Proteoform> entities = new TreeSet<>();
        HashSet<Snp> snpSet = new HashSet<>();

        try {
            validateVepTables(strMap.get(Conf.StrVars.vepTablesPath));
        } catch (FileNotFoundException e) {
            sendError(ERROR_READING_VEP_TABLES);
        } catch (NoSuchFileException e) {
            sendError(VEP_DIRECTORY_NOT_FOUND);
        }

        int row = 0;

        // Create set of snps
        for (String line : input) {
            row++;
            line = line.trim();

            if (!line.startsWith("#")) {
                continue;
            }

            if (matches_Vcf_Record(line)) {
                Snp snp = getSnpFromVcf(line);
                snpSet.add(snp);
            } else {
                if (line.isEmpty()) sendWarning(EMPTY_ROW, row);
                else sendWarning(INVALID_ROW, row);
            }
        }
        return entities;
    }

    /*
    This method expects the line to be validated already
     */
    private static Snp getSnpFromVcf(String line) {
        String[] fields = line.split(" ");
        Integer chr = Integer.valueOf(fields[0]);
        Long bp = Long.valueOf(fields[1]);
        String rsid = fields[2];

        if (rsid.equals(".")) {
            return new Snp(chr, bp);
        } else {
            return new Snp(chr, bp, rsid);
        }
    }
}
