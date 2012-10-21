JFLAGS = -g
JC = javac
CLASSPATH := $(shell dirname $(realpath $(lastword $(MAKEFILE_LIST))))/src

.SUFFIXES: .java .class
.java.class:
	CLASSPATH=$(CLASSPATH) $(JC) $(JFLAGS) $*.java

CLASSES = \
        src/test/Test.java 

fuck: you

default: classes

classes: $(CLASSES:.java=.class)

go: classes
	CLASSPATH=$(CLASSPATH) java test.Test

you: clean default go

clean:
	find . -name *.class -exec rm {} \;
