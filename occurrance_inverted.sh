# occurrance2.sh

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

	echo "Sequenza $x"

	mkdir $DIR_TMP/seq$x
	mkdir $DIR_OUT/seq$x

	for (( y=$KMIN; y<=$KMAX; y++ ))
	do

		echo "---> Sequenza $x, Conteggio k-$y"

		#echo "$x-$y"
		kmc -m$MEM -k$y -fa -ci$CI -cs$CS $DIR_DATASET/seq$x.fasta $DIR_TMP/seq$x/db_k$y.res $DIR_TMP

		kmc_dump_inverted $DIR_TMP/seq$x/db_k$y.res $DIR_OUT/seq$x/k$y.res

		sort -n -r -k1 $DIR_OUT/seq$x/k$y.res -o $DIR_OUT/seq$x/k"$y"-ordered.res

		cat $DIR_OUT/seq$x/k"$y"-ordered.res >> $DIR_OUT/seq"$x"_all.res
		printf "\n" >> $DIR_OUT/seq"$x"_all.res

		rm $DIR_OUT/seq$x/k$y.res
		rm $DIR_OUT/seq$x/k"$y"-ordered.res

		rm $DIR_TMP/seq$x/db_k$y.res*

	done

	rmdir $DIR_OUT/seq$x

done

rm -rf $DIR_TMP/seq*