rm -r ./data/*
mkdir data/plot

node zad_measurement_asym.js
node zad_measurement_arbiter.js

python plot.py