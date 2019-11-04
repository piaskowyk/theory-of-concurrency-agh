import matplotlib.pyplot as plt
import os

measurement_files = os.listdir("./data/")

for file_name in measurement_files:
    file = open("./data/" + file_name, "r")
    content = file.readlines()

    plotX = []
    plotY = []

    for line in content:
        data = line.split(" ")
        plotX.append(int(data[0]))
        plotY.append(float(data[1]))

    plt.cla()
    plt.plot(plotX, plotY)
    plt.savefig('./output/' + file_name + '.png')
    # plt.show()