JFLAGS = -g
JC = javac
CURRPATH = $(shell dirname $(realpath $(lastword $(MAKEFILE_LIST))))
CLASSPATH = $(CURRPATH)/src:$(CURRPATH)/src/libs/junit-4.9.jar

.SUFFIXES: .java .class
.java.class:
	CLASSPATH=$(CLASSPATH) $(JC) $(JFLAGS) $*.java

CLASSES = \
        src/test/Test.java \
        src/test/SetupTest.java \
        src/test/ChronosTest.java

fuck: you

default: classes

classes: $(CLASSES:.java=.class)

go: classes
	CLASSPATH=$(CLASSPATH) java -ea test.ChronosTest

you: clean default go

clean:
	find . -name *.class -exec rm {} \;
