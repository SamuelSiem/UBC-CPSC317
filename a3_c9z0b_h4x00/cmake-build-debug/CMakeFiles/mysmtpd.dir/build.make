# CMAKE generated file: DO NOT EDIT!
# Generated by "Unix Makefiles" Generator, CMake Version 3.10

# Delete rule output on recipe failure.
.DELETE_ON_ERROR:


#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canonical targets will work.
.SUFFIXES:


# Remove some rules from gmake that .SUFFIXES does not remove.
SUFFIXES =

.SUFFIXES: .hpux_make_needs_suffix_list


# Suppress display of executed commands.
$(VERBOSE).SILENT:


# A target that is always out of date.
cmake_force:

.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

# The shell in which to execute make rules.
SHELL = /bin/sh

# The CMake executable.
CMAKE_COMMAND = /usr/bin/cmake

# The command to remove a file.
RM = /usr/bin/cmake -E remove -f

# Escaping for special characters.
EQUALS = =

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = /mnt/c/Users/samue/Desktop/a3_c9z0b_h4x0b

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = /mnt/c/Users/samue/Desktop/a3_c9z0b_h4x0b/cmake-build-debug

# Include any dependencies generated for this target.
include CMakeFiles/mysmtpd.dir/depend.make

# Include the progress variables for this target.
include CMakeFiles/mysmtpd.dir/progress.make

# Include the compile flags for this target's objects.
include CMakeFiles/mysmtpd.dir/flags.make

CMakeFiles/mysmtpd.dir/mailuser.c.o: CMakeFiles/mysmtpd.dir/flags.make
CMakeFiles/mysmtpd.dir/mailuser.c.o: ../mailuser.c
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/mnt/c/Users/samue/Desktop/a3_c9z0b_h4x0b/cmake-build-debug/CMakeFiles --progress-num=$(CMAKE_PROGRESS_1) "Building C object CMakeFiles/mysmtpd.dir/mailuser.c.o"
	/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -o CMakeFiles/mysmtpd.dir/mailuser.c.o   -c /mnt/c/Users/samue/Desktop/a3_c9z0b_h4x0b/mailuser.c

CMakeFiles/mysmtpd.dir/mailuser.c.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing C source to CMakeFiles/mysmtpd.dir/mailuser.c.i"
	/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -E /mnt/c/Users/samue/Desktop/a3_c9z0b_h4x0b/mailuser.c > CMakeFiles/mysmtpd.dir/mailuser.c.i

CMakeFiles/mysmtpd.dir/mailuser.c.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling C source to assembly CMakeFiles/mysmtpd.dir/mailuser.c.s"
	/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -S /mnt/c/Users/samue/Desktop/a3_c9z0b_h4x0b/mailuser.c -o CMakeFiles/mysmtpd.dir/mailuser.c.s

CMakeFiles/mysmtpd.dir/mailuser.c.o.requires:

.PHONY : CMakeFiles/mysmtpd.dir/mailuser.c.o.requires

CMakeFiles/mysmtpd.dir/mailuser.c.o.provides: CMakeFiles/mysmtpd.dir/mailuser.c.o.requires
	$(MAKE) -f CMakeFiles/mysmtpd.dir/build.make CMakeFiles/mysmtpd.dir/mailuser.c.o.provides.build
.PHONY : CMakeFiles/mysmtpd.dir/mailuser.c.o.provides

CMakeFiles/mysmtpd.dir/mailuser.c.o.provides.build: CMakeFiles/mysmtpd.dir/mailuser.c.o


CMakeFiles/mysmtpd.dir/mysmtpd.c.o: CMakeFiles/mysmtpd.dir/flags.make
CMakeFiles/mysmtpd.dir/mysmtpd.c.o: ../mysmtpd.c
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/mnt/c/Users/samue/Desktop/a3_c9z0b_h4x0b/cmake-build-debug/CMakeFiles --progress-num=$(CMAKE_PROGRESS_2) "Building C object CMakeFiles/mysmtpd.dir/mysmtpd.c.o"
	/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -o CMakeFiles/mysmtpd.dir/mysmtpd.c.o   -c /mnt/c/Users/samue/Desktop/a3_c9z0b_h4x0b/mysmtpd.c

CMakeFiles/mysmtpd.dir/mysmtpd.c.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing C source to CMakeFiles/mysmtpd.dir/mysmtpd.c.i"
	/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -E /mnt/c/Users/samue/Desktop/a3_c9z0b_h4x0b/mysmtpd.c > CMakeFiles/mysmtpd.dir/mysmtpd.c.i

CMakeFiles/mysmtpd.dir/mysmtpd.c.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling C source to assembly CMakeFiles/mysmtpd.dir/mysmtpd.c.s"
	/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -S /mnt/c/Users/samue/Desktop/a3_c9z0b_h4x0b/mysmtpd.c -o CMakeFiles/mysmtpd.dir/mysmtpd.c.s

CMakeFiles/mysmtpd.dir/mysmtpd.c.o.requires:

.PHONY : CMakeFiles/mysmtpd.dir/mysmtpd.c.o.requires

CMakeFiles/mysmtpd.dir/mysmtpd.c.o.provides: CMakeFiles/mysmtpd.dir/mysmtpd.c.o.requires
	$(MAKE) -f CMakeFiles/mysmtpd.dir/build.make CMakeFiles/mysmtpd.dir/mysmtpd.c.o.provides.build
.PHONY : CMakeFiles/mysmtpd.dir/mysmtpd.c.o.provides

CMakeFiles/mysmtpd.dir/mysmtpd.c.o.provides.build: CMakeFiles/mysmtpd.dir/mysmtpd.c.o


CMakeFiles/mysmtpd.dir/netbuffer.c.o: CMakeFiles/mysmtpd.dir/flags.make
CMakeFiles/mysmtpd.dir/netbuffer.c.o: ../netbuffer.c
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/mnt/c/Users/samue/Desktop/a3_c9z0b_h4x0b/cmake-build-debug/CMakeFiles --progress-num=$(CMAKE_PROGRESS_3) "Building C object CMakeFiles/mysmtpd.dir/netbuffer.c.o"
	/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -o CMakeFiles/mysmtpd.dir/netbuffer.c.o   -c /mnt/c/Users/samue/Desktop/a3_c9z0b_h4x0b/netbuffer.c

CMakeFiles/mysmtpd.dir/netbuffer.c.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing C source to CMakeFiles/mysmtpd.dir/netbuffer.c.i"
	/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -E /mnt/c/Users/samue/Desktop/a3_c9z0b_h4x0b/netbuffer.c > CMakeFiles/mysmtpd.dir/netbuffer.c.i

CMakeFiles/mysmtpd.dir/netbuffer.c.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling C source to assembly CMakeFiles/mysmtpd.dir/netbuffer.c.s"
	/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -S /mnt/c/Users/samue/Desktop/a3_c9z0b_h4x0b/netbuffer.c -o CMakeFiles/mysmtpd.dir/netbuffer.c.s

CMakeFiles/mysmtpd.dir/netbuffer.c.o.requires:

.PHONY : CMakeFiles/mysmtpd.dir/netbuffer.c.o.requires

CMakeFiles/mysmtpd.dir/netbuffer.c.o.provides: CMakeFiles/mysmtpd.dir/netbuffer.c.o.requires
	$(MAKE) -f CMakeFiles/mysmtpd.dir/build.make CMakeFiles/mysmtpd.dir/netbuffer.c.o.provides.build
.PHONY : CMakeFiles/mysmtpd.dir/netbuffer.c.o.provides

CMakeFiles/mysmtpd.dir/netbuffer.c.o.provides.build: CMakeFiles/mysmtpd.dir/netbuffer.c.o


CMakeFiles/mysmtpd.dir/server.c.o: CMakeFiles/mysmtpd.dir/flags.make
CMakeFiles/mysmtpd.dir/server.c.o: ../server.c
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/mnt/c/Users/samue/Desktop/a3_c9z0b_h4x0b/cmake-build-debug/CMakeFiles --progress-num=$(CMAKE_PROGRESS_4) "Building C object CMakeFiles/mysmtpd.dir/server.c.o"
	/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -o CMakeFiles/mysmtpd.dir/server.c.o   -c /mnt/c/Users/samue/Desktop/a3_c9z0b_h4x0b/server.c

CMakeFiles/mysmtpd.dir/server.c.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing C source to CMakeFiles/mysmtpd.dir/server.c.i"
	/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -E /mnt/c/Users/samue/Desktop/a3_c9z0b_h4x0b/server.c > CMakeFiles/mysmtpd.dir/server.c.i

CMakeFiles/mysmtpd.dir/server.c.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling C source to assembly CMakeFiles/mysmtpd.dir/server.c.s"
	/usr/bin/cc $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -S /mnt/c/Users/samue/Desktop/a3_c9z0b_h4x0b/server.c -o CMakeFiles/mysmtpd.dir/server.c.s

CMakeFiles/mysmtpd.dir/server.c.o.requires:

.PHONY : CMakeFiles/mysmtpd.dir/server.c.o.requires

CMakeFiles/mysmtpd.dir/server.c.o.provides: CMakeFiles/mysmtpd.dir/server.c.o.requires
	$(MAKE) -f CMakeFiles/mysmtpd.dir/build.make CMakeFiles/mysmtpd.dir/server.c.o.provides.build
.PHONY : CMakeFiles/mysmtpd.dir/server.c.o.provides

CMakeFiles/mysmtpd.dir/server.c.o.provides.build: CMakeFiles/mysmtpd.dir/server.c.o


# Object files for target mysmtpd
mysmtpd_OBJECTS = \
"CMakeFiles/mysmtpd.dir/mailuser.c.o" \
"CMakeFiles/mysmtpd.dir/mysmtpd.c.o" \
"CMakeFiles/mysmtpd.dir/netbuffer.c.o" \
"CMakeFiles/mysmtpd.dir/server.c.o"

# External object files for target mysmtpd
mysmtpd_EXTERNAL_OBJECTS =

mysmtpd: CMakeFiles/mysmtpd.dir/mailuser.c.o
mysmtpd: CMakeFiles/mysmtpd.dir/mysmtpd.c.o
mysmtpd: CMakeFiles/mysmtpd.dir/netbuffer.c.o
mysmtpd: CMakeFiles/mysmtpd.dir/server.c.o
mysmtpd: CMakeFiles/mysmtpd.dir/build.make
mysmtpd: CMakeFiles/mysmtpd.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --bold --progress-dir=/mnt/c/Users/samue/Desktop/a3_c9z0b_h4x0b/cmake-build-debug/CMakeFiles --progress-num=$(CMAKE_PROGRESS_5) "Linking C executable mysmtpd"
	$(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/mysmtpd.dir/link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
CMakeFiles/mysmtpd.dir/build: mysmtpd

.PHONY : CMakeFiles/mysmtpd.dir/build

CMakeFiles/mysmtpd.dir/requires: CMakeFiles/mysmtpd.dir/mailuser.c.o.requires
CMakeFiles/mysmtpd.dir/requires: CMakeFiles/mysmtpd.dir/mysmtpd.c.o.requires
CMakeFiles/mysmtpd.dir/requires: CMakeFiles/mysmtpd.dir/netbuffer.c.o.requires
CMakeFiles/mysmtpd.dir/requires: CMakeFiles/mysmtpd.dir/server.c.o.requires

.PHONY : CMakeFiles/mysmtpd.dir/requires

CMakeFiles/mysmtpd.dir/clean:
	$(CMAKE_COMMAND) -P CMakeFiles/mysmtpd.dir/cmake_clean.cmake
.PHONY : CMakeFiles/mysmtpd.dir/clean

CMakeFiles/mysmtpd.dir/depend:
	cd /mnt/c/Users/samue/Desktop/a3_c9z0b_h4x0b/cmake-build-debug && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" /mnt/c/Users/samue/Desktop/a3_c9z0b_h4x0b /mnt/c/Users/samue/Desktop/a3_c9z0b_h4x0b /mnt/c/Users/samue/Desktop/a3_c9z0b_h4x0b/cmake-build-debug /mnt/c/Users/samue/Desktop/a3_c9z0b_h4x0b/cmake-build-debug /mnt/c/Users/samue/Desktop/a3_c9z0b_h4x0b/cmake-build-debug/CMakeFiles/mysmtpd.dir/DependInfo.cmake --color=$(COLOR)
.PHONY : CMakeFiles/mysmtpd.dir/depend

