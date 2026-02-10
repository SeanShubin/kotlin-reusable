
## Note on checked/unchecked splits
usage frequency matters more than method count percentage when deciding on checked/unchecked splits:
- SystemContract: inheritedChannel() is rarely used → no split needed
- FilesContract: Most methods throw checked exceptions AND are frequently used → split valuable
- Process/ProcessBuilder: Only a few methods throw checked exceptions, BUT those methods (start(), waitFor()) are core operations used in virtually every process execution → split is valuable
