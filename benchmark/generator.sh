# This script deals with generating pseudo-genomic sequences and
# calculating occurrences of variable-length k-mers within them
# by using the KMC tool and its modified dump tool.

PATH=$PATH:bin

######## Generator Params ########
MIN_LENGTH_SEQ=90000000
MAX_LENGTH_SEQ=118000000
NUM_SEQ=10
DATABASE_NAME=benchsuiteB # database name for KMC
DIR_DATASET=sequencesB # write directory for KMC
BUF_SIZE=10 # in MB, KMC write buffer
###################################

########### KMC Params ###########
KMIN=3 # min k-mer length
KMAX=15 # max k-mer length

# KMC-specific parameters
# cs = 2^32 - 1
CS=4294967295
CI=1
MEM=2

DIR_OUT=sequencesB_kmers # KMC output directory
DIR_TMP=Xtmp_work # KMC temp directory
###################################

# create folders if they do not exist
mkdir $DIR_DATASET
mkdir $DIR_TMP
mkdir $DIR_OUT

# Generate pseudo-genomic sequences in FASTA format
echo "Making dataset files FASTA"
java -classpath d2-*.jar generator.MainGeneratorMultipleFiles $MIN_LENGTH_SEQ $MAX_LENGTH_SEQ $NUM_SEQ $DATABASE_NAME "$DIR_DATASET/" $BUF_SIZE

# Consider sequences one by one
for (( x=0; x<$NUM_SEQ; x++ ))
do

	echo "Sequenza $x"

	# Create output and temp folders for sequence x
	mkdir $DIR_TMP/seq$x
	mkdir $DIR_OUT/seq$x

	# search for k-mers with length = y
	for (( y=$KMIN; y<=$KMAX; y++ ))
	do

		echo "---> Sequenza $x, Conteggio $y-mer"

		# Print current command
		#echo "$x-$y"
		echo "kmc -m$MEM -k$y -fm -ci$CI -cs$CS $DIR_DATASET/seq$x.fasta $DIR_TMP/seq$x/seq$x.res $DIR_TMP"
		# Create KMC sequences database
		kmc -m$MEM -k$y -fm -ci$CI -cs$CS $DIR_DATASET/seq$x.fasta $DIR_TMP/seq$x/seq$x.res $DIR_TMP

		echo "---> kmc_dump_indexed $DIR_TMP/seq$x/db_k$y.res $DIR_OUT/seq$x/k$y.res"
		# Create files with custom KMC dump tool
		# whose lines are in the format INDEX_SEQ\tNUM_OCCURRENCES\tK-MER
		kmc_dump_indexed $DIR_TMP/seq$x/seq$x.res $DIR_OUT/seq$x/k$y.res

		#sort -n -r -k3 $DIR_OUT/seq$x/k$y.res -o $DIR_OUT/seq$x/k"$y"-ordered.res

		# Append all k-mer occurrences files in the same file
		cat $DIR_OUT/seq$x/k$y.res >> $DIR_OUT/seq"$x"_all.res
		printf "\n" >> $DIR_OUT/seq"$x"_all.res

		# Remove temporary files
		rm $DIR_OUT/seq$x/k$y.res
		#rm $DIR_OUT/seq$x/k"$y"-ordered.res

		rm $DIR_TMP/seq$x/seq$x.res*

	done

	# remove temporary folders
	rmdir $DIR_OUT/seq$x

done

# Remove temporary folder
rm -rf $DIR_TMP/seq*
rmdir $DIR_TMP

# Compress result files
env GZIP=-9 tar cvzf kmers.tar.gz $DIR_OUT
tar cvzf sequences.tar.gz $DIR_DATASET
