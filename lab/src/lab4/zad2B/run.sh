/usr/lib/jvm/jdk-11.0.1/bin/javac Main_B.java -d ./bin

cd bin
echo "1000 10"
/usr/lib/jvm/jdk-11.0.1/bin/java lab4/zad2B/Main_B 1000 10
echo "1000 100"
/usr/lib/jvm/jdk-11.0.1/bin/java lab4/zad2B/Main_B 1000 100
echo "1000 1000"
/usr/lib/jvm/jdk-11.0.1/bin/java lab4/zad2B/Main_B 1000 1000

echo "10000 10"
/usr/lib/jvm/jdk-11.0.1/bin/java lab4/zad2B/Main_B 10000 10
echo "10000 100"
/usr/lib/jvm/jdk-11.0.1/bin/java lab4/zad2B/Main_B 10000 100
echo "10000 1000"
/usr/lib/jvm/jdk-11.0.1/bin/java lab4/zad2B/Main_B 10000 1000

echo "100000 10"
/usr/lib/jvm/jdk-11.0.1/bin/java lab4/zad2B/Main_B 100000 10
echo "100000 100"
/usr/lib/jvm/jdk-11.0.1/bin/java lab4/zad2B/Main_B 100000 100
echo "100000 1000"
/usr/lib/jvm/jdk-11.0.1/bin/java lab4/zad2B/Main_B 100000 1000

cd ..
python plot.pyzad2B/Main_