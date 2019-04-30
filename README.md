# bgp-d2

Here is our implementation for *distributed D2 distance algorithm* for k-mers by using the Apache Hadoop framework.

- [bgp-d2](#bgp-d2)
  - [Introduction](#introduction)
  - [Distributed D2](#distributed-d2)
  - [Hadoop cluster configuration](#hadoop-cluster-configuration)
    - [Hardware](#hardware)
    - [Hadoop configuration](#hadoop-configuration)
  - [Repository](#repository)
    - [Installation](#installation)
  - [References](#references)
  - [Authors](#authors)

## Introduction

Among several *alignment-free methods* to calculate similarity between two strings, we picked one (D2) based on word statistics, specifically their frequency in a sequence.

Once all possible k-mers into the two sequences have been determined, to calculate the distance among them we'll use the **D2 function**.

Both sequential and distributed implementation of D2 algorithm take as input the result of **[KMC tool](https://github.com/labgua/KMC/)** output: KMC allows to count k-mers in one or more genomic sequences; in our case, it has been used to count from *k = 3* up to *k = 13*. For each sequence, a k-mer occurrency file has been generated.

## Distributed D2

Distributed D2 implementation consist of a first MapReduce phase (to read k-mers occurrences from KMC output file and calculate partial D2 scores) and an eventual second one where if more than one task is created to sum partial scores.

## Hadoop cluster configuration

### Hardware

Test cluster machines had the following configuration:

- **CPU**: Intel Xeon E3-12xx v2 (Ivy Bridge), 8 cores
- **RAM**: 32 GB
- **OS**: Ubuntu 16.04.4 LTS

### Hadoop configuration

Each Hadoop node had the following configuration:

- ```yarn-site.xml```
  - ```yarn.nodemanager.resource.memory-mb```: 30720
  - ```yarn.nodemanager.resource.cpu-vcores```: 8
- ```hdfs-site.xml```
  - ```dfs.replication```: 1
  - ```dfs.blocksize```: 64m
- ```mapred-site.xml```
  - ```mapreduce.map.memory.mb```: 4096
  - ```mapreduce.reduce.memory.mb```: 7168
  - ```mapreduce.map.java.opts```: -Xmx3276M
  - ```mapreduce.reduce.java.opts```: -Xmx5734M
  - ```mapreduce.[map|reduce].cpu.vcores```: 2

## Repository

The **bgp-d2** repository consist of three main folders:

- [sequential D2 classes](https://github.com/bissim/bgp-d2/tree/master/sequential/d2)
- [distributed D2 classes](https://github.com/bissim/bgp-d2/tree/master/distributed/d2d)
- [benchmark scripts](https://github.com/bissim/bgp-d2/tree/master/benchmark)

### Installation

Both sequential and distributed projects can be built by running the following command:

```bash
mvn clean compile javadoc:javadoc
```

## References

- [Kunin V., Ahren D., Goldovsky L., Janssen P., Ouzounis C. A., "*Measuring genome conservation across taxa: divided strains and united kingdoms*"](https://doi.org/10.1093/nar/gki181)
- [Deorowicz, S., Debudaj-Grabysz, A., Grabowski, Sz., "*Disk-based k-mer counting on a PC*"](https://doi.org/10.1186/1471-2105-14-160)

## Authors

- [Simone Bisogno](https://github.com/bissim)
- [Sergio Guastaferro](https://github.com/labgua)
- [Mariangela Petraglia](https://github.com/marypet91)
