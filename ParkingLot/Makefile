JFLAGS = -g
JC = javac
CURRPATH = $(shell dirname $(realpath $(lastword $(MAKEFILE_LIST))))
CLASSPATH = $(CURRPATH)/src

.SUFFIXES: .java .class
.java.class:
	CLASSPATH=$(CLASSPATH) $(JC) $(JFLAGS) $*.java

CLASSES = \
        src/test/SimulationTest.java

go: classes
	CLASSPATH=$(CLASSPATH) java -ea test.SimulationTest $(filter-out $@, $(MAKECMDGOALS))

default: classes

classes: $(CLASSES:.java=.class)

magic: clean classes
	#This magical code goes through each source file in CLASSES and runs it as a test.
	$(foreach var, $(CLASSES), CLASSPATH=$(CLASSPATH) echo -e "\n>>>>>> Running $(var) \n"; echo $(var) | cut -c 5- | rev | cut -c 6- | rev | tr '/' '.' | xargs java -ea ;)
	

# This runs all the tests.
you: clean default go

clean:
	find . -name *.class -exec rm {} \;

