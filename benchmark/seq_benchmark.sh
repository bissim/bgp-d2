
OUT_FILE=out

MIN_LENGTH_SEQ=100
MAX_LENGTH_SEQ=1000
NUM_SEQ=10
DATABASE_NAME=benchsuite
OUTDIR=tmp_suite
TOP_LINES_OCCURANCES=1000

echo "Sequential Benchmark" >> $OUT_FILE
date >> $OUT_FILE

echo "Run SequentialD2" >> $OUT_FILE
java -classpath d2-0.0.1.jar generator.MainGeneratorMultipleFiles $OUTDIR $TOP_LINES_OCCURANCES yes >> $OUT_FILE
echo "DONE" >> $OUT_FILE


echo "----------------------------" >> $OUT_FILE