package nanotools;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ReferenceSequence {
    private String id = null;
    private String name = null;
    private int size = 0;
    private int[] coverage;
    private int[] perfectKmerCounts = new int[1000];
    private int[] readBestPerfectKmer = new int[1000];
    private int[] readCumulativeBestPerfectKmer = new int[1000];
    private int longestPerfectKmer = 0;
    private int nReadsWithAlignments = 0;
    
    public ReferenceSequence(String i, int s, String n) {
        id = i;
        size = s;
        name = n;
        coverage = new int[size];
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public int getSize() {
        return size;
    }
    
    public int getNumberOfReadsWithAlignments() {
        return nReadsWithAlignments;
    }
    
    public int getLongestPerfectKmer() {
        return longestPerfectKmer;
    }

    public void clearStats() {
        for (int i=0; i<NanotoolsOptions.MAX_KMER; i++) {
            perfectKmerCounts[i] = 0;
            readBestPerfectKmer[i] = 0;
            readCumulativeBestPerfectKmer[i] = 0;
        }
        
        for (int i=0; i<size; i++) {
            coverage[i] = 0;
        }
        
        longestPerfectKmer = 0;
        nReadsWithAlignments = 0;
    }    
    
    public void addPerfectKmer(int size) {
        if (size >= 1000) {
            System.out.println("Error: very unlikely situation with perfect kmer of size " + size);
            System.exit(1);
        }
        
        perfectKmerCounts[size]++;
        
        if (size > longestPerfectKmer) {
            longestPerfectKmer = size;
        }
    }
    
    public void addCoverage(int start, int size) {
        for (int i=start; i<(start+size); i++) {
            coverage[i]++;
        }
    }
    
    public void addReadBestKmer(int bestKmer) {
        readBestPerfectKmer[bestKmer]++;
        
        for (int i=1; i<=bestKmer; i++) {
            readCumulativeBestPerfectKmer[i]++;
        }
        
        nReadsWithAlignments++;
    }
    
    public void writeCoverageData(String filename, int binSize) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(filename));            
            for (int i=0; i<(size-binSize); i+=binSize) {
                int count = 0;
                for (int j=0; j<binSize; j++) {
                    count += coverage[i+j];
                }
                pw.printf("%d\t%.2f\n", i, ((double)count / (double)binSize));
            }            
            pw.close();
        } catch (IOException e) {
            System.out.println("writeCoverageData exception:");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void writePerfectKmerHist(String filename) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(filename));            
            for (int i=1; i<=longestPerfectKmer; i++) {
                pw.printf("%d\t%d\n", i, perfectKmerCounts[i]);
            }            
            pw.close();
        } catch (IOException e) {
            System.out.println("writePerfectKmerHist exception:");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void writeBestPerfectKmerHist(String filename) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(filename));            
            for (int i=1; i<=longestPerfectKmer; i++) {
                double pc = 0;

                if ((readBestPerfectKmer[i] > 0) && (nReadsWithAlignments > 0)) {
                    pc = ((double)100.0 * readBestPerfectKmer[i]) / (double)nReadsWithAlignments;
                } 

                pw.printf("%d\t%d\t%.2f\n", i, readBestPerfectKmer[i], pc);
            }            
            pw.close();
        } catch (IOException e) {
            System.out.println("writeBestPerfectKmerHist exception:");
            e.printStackTrace();
            System.exit(1);
        }
    }    

    public void writeBestPerfectKmerHistCumulative(String filename) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(filename));            
            for (int i=1; i<=longestPerfectKmer; i++) {
                double pc = 0;
                
                if ((readCumulativeBestPerfectKmer[i]> 0) && (nReadsWithAlignments > 0)){
                    pc = ((double)100.0 * readCumulativeBestPerfectKmer[i]) / (double)nReadsWithAlignments;
                }
                
                pw.printf("%d\t%d\t%.2f\n", i, readCumulativeBestPerfectKmer[i], pc);
            }            
            pw.close();
        } catch (IOException e) {
            System.out.println("writeBestPerfectKmerHistCumulative exception:");
            e.printStackTrace();
            System.exit(1);
        }
    }    
    
    public void writeSummary(PrintWriter pw, String format) {
        pw.printf(format, name, size, nReadsWithAlignments, longestPerfectKmer);
    }
}