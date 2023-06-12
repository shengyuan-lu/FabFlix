import sys

if __name__ == "__main__":
    TSs = list()
    TJs = list()

    with open(sys.argv[1]) as f:
        for line in f:
            temp = line.split(';')
            TS = int(temp[0].split('-')[1])
            TJ = int(temp[1].split('-')[1].strip("\n"))
            TSs.append(TS)
            TJs.append(TJ)

    average_TS = sum(TSs) / len(TSs) / (10**6)
    average_TJ = sum(TJs) / len(TJs) / (10**6)

    print("Average TS: ", average_TS, " ms")
    print("Average TJ: ", average_TJ, " ms")