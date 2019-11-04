/usr/lib/jvm/jdk-11.0.1/bin/javac Main_A.java -d ./bin

cd bin
echo "1000 10"
/usr/lib/jvm/jdk-11.0.1/bin/java lab4/zad2A/Main_A 1000 10
echo "1000 100"
/usr/lib/jvm/jdk-11.0.1/bin/java lab4/zad2A/Main_A 1000 100
echo "1000 1000"
/usr/lib/jvm/jdk-11.0.1/bin/java lab4/zad2A/Main_A 1000 1000

echo "10000 10"
/usr/lib/jvm/jdk-11.0.1/bin/java lab4/zad2A/Main_A 10000 10
echo "10000 100"
/usr/lib/jvm/jdk-11.0.1/bin/java lab4/zad2A/Main_A 10000 100
echo "10000 1000"
/usr/lib/jvm/jdk-11.0.1/bin/java lab4/zad2A/Main_A 10000 1000

echo "100000 10"
/usr/lib/jvm/jdk-11.0.1/bin/java lab4/zad2A/Main_A 100000 10
echo "100000 100"
/usr/lib/jvm/jdk-11.0.1/bin/java lab4/zad2A/Main_A 100000 100
echo "100000 1000"
/usr/lib/jvm/jdk-11.0.1/bin/java lab4/zad2A/Main_A 100000 1000

cd ..
python plot.py