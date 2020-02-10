import matplotlib.pyplot as plt
import os

measurement_files = os.listdir("./data/")

for file_name in measurement_files:
    if 'data' not in file_name:
        continue
    file = open("./data/" + file_name, "r")
    content = file.readlines()

    plotX = []
    plotY = []
    plotY2 = []

    for line in content:
        data = line.split(" ")
        plotX.append(int(data[0]))
        plotY.append(float(data[1]))
        plotY2.append(float(data[2]))

    plt.cla()
    plt.plot(plotX, plotY, 'b')
    plt.plot(plotX, plotY, 'ro')
    plt.grid(True, which='both')
    plt.savefig('./data/plot/' + file_name + '_sum.png')

    plt.cla()
    plt.plot(plotX, plotY2, 'b')
    plt.plot(plotX, plotY2, 'ro')
    plt.grid(True, which='both')
    plt.savefig('./data/plot/' + file_name + '_avg.png')
    file.close()