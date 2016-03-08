JAVA ?= java
JAVA_ARGS ?= #-agentlib:jdwp=transport=dt_socket,server=y,suspend=n
JAVAC ?= javac

#Compiling for Android, so we use jdk 1.6
JAVAC_ARGS ?= -Xlint:unchecked -source 1.6 -bootclasspath ${HOME}/java/jdk1.6.0_45/jre/lib/rt.jar

PRGM := dioo-commons
PKG := ca/dioo/java/commons
ROOT_DIR := $(dir $(lastword $(MAKEFILE_LIST)))
BUILD_DIR := $(ROOT_DIR)/build
BPATH := $(BUILD_DIR)/$(PKG)
empty :=
space := $(empty) $(empty)

src := $(wildcard src/*.java)
objects := $(patsubst src/%.java,$(BPATH)/%.class,$(src))

test_src := $(wildcard test/*.java)
test_objects := $(patsubst test/%.java,$(BUILD_DIR)/%.class,$(test_src))

libs = $(wildcard libs/*.jar)


.PHONY: jar
jar: $(PRGM).jar


.PHONY: del_test
del_test: $(BUILD_DIR)
	@for i in $(test_objects); do [ ! -e "$$i" ] || rm "$$i"; done


$(PRGM).jar: $(objects) del_test
	jar -cf $@ -C $(BUILD_DIR) .


.PHONY: all
all: $(objects)


$(objects): $(BPATH)/%.class: src/%.java $(libs) $(BUILD_DIR)
	$(JAVAC) $(JAVAC_ARGS) -cp $(subst $(space),:,$(libs)):$(BUILD_DIR) -d $(BUILD_DIR) $<


.PHONY: test
test: $(objects) $(test_objects)
	cat test/server_response.txt test/end_magic.bin test/orig.avi > test/serv.test
	$(JAVA) -ea $(JAVA_ARGS) -cp $(subst $(space),:,$(libs)):$(BUILD_DIR) Test $(ARGS)
	diff -qs test/orig.avi test/serv.out


$(test_objects): $(BUILD_DIR)/%.class: test/%.java $(libs) $(BUILD_DIR)
	$(JAVAC) $(JAVAC_ARGS) -cp $(subst $(space),:,$(libs)):$(BUILD_DIR) -d $(BUILD_DIR) $<



$(BUILD_DIR):
	@[ -d $(BUILD_DIR) ] || mkdir -p $(BUILD_DIR)


.PHONY: clean
clean:
	@[ ! -e $(BUILD_DIR) ] || rm -rv $(BUILD_DIR)


libs:
	@[ -d libs ] || mkdir libs


.PHONY: libjars
libjars: libs $(libs)
