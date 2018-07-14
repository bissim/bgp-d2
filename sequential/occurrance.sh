

PATH=$PATH:bin


KMIN=3
KMAX=15
NUMSEQ=10

# cs = 2^32 - 1
CS=4294967295
CI=1
MEM=4

DIR_DATASET=single_fasta
DIR_OUT=occ
DIR_TMP=tmp_work

mkdir $DIR_TMP
mkdir $DIR_OUT

for (( x=0; x<$NUMSEQ; x++ ))
do

	echo "Sequence $x"

	mkdir $DIR_TMP/seq$x
	mkdir $DIR_OUT/seq$x

	for (( y=$KMIN; y<=$KMAX; y++ ))
	do

		echo "---> Sequence $x, Counter k-$y"

		#echo "$x-$y"
		kmc -m$MEM -k$y -fa -ci$CI -cs$CS $DIR_DATASET/seq$x.fasta $DIR_TMP/seq$x/db_k$y.res $DIR_TMP

		kmc_dump $DIR_TMP/seq$x/db_k$y.res $DIR_OUT/seq$x/k$y.res

		sort -n -r -k2 $DIR_OUT/seq$x/k$y.res -o $DIR_OUT/seq$x/k"$y"-ordered.res

		rm $DIR_OUT/seq$x/k$y.res
		mv $DIR_OUT/seq$x/k"$y"-ordered.res $DIR_OUT/seq$x/k$y.res

		rm $DIR_TMP/seq$x/db_k$y.res*

	done
done

rm -rf $DIR_TMP/seq*
