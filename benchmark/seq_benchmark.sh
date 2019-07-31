# Runs sequential benchmark for D2 algorithm by
# using custom-defined parameters.

OUT_FILE=out

NUM_SEQ=10
INDIR=sequences
TOP_LINES_OCCURANCES=1000

echo "Sequential Benchmark" >> $OUT_FILE
date >> $OUT_FILE

echo "Run SequentialD2" >> $OUT_FILE
java -classpath d2-*.jar benchmark.SequentialBenchmark $INDIR $NUM_SEQ $TOP_LINES_OCCURANCES yes >> $OUT_FILE
echo "DONE" >> $OUT_FILE

echo "----------------------------" >> $OUT_FILE
