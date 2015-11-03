!define APPNAME "MIDIBlocks"
!define COMPANYNAME "Team 49"
!define DESCRIPTION "A virtual keyboard application"
# These three must be integers
!define VERSIONMAJOR 1
!define VERSIONMINOR 0
!define VERSIONBUILD 0
# This is the size (in kB) of all the files copied into "Program Files"
!define INSTALLSIZE 60
 
InstallDir ".\${COMPANYNAME}\${APPNAME}"
 
# This will be in the installer/uninstaller's title bar
Name "${COMPANYNAME} - ${APPNAME}"
outFile "MIDIBlocksInstaller.exe"
 
!include LogicLib.nsh
 
# Just three pages - license agreement, install location, and installation
page directory
Page instfiles
 
function .onInit
	setShellVarContext all
functionEnd
 
section "install"
	# Files for the install directory - to build the installer, these should be in the same directory as the install script (this file)
	setOutPath $INSTDIR
	# Files added here should be removed by the uninstaller (see section "uninstall")
	file "MIDIBlocks.exe"
	# Add any other files for the install directory (license files, app data, etc) here
 
	# Start Menu
	createDirectory "$SMPROGRAMS\${COMPANYNAME}"
	createShortCut "$SMPROGRAMS\${COMPANYNAME}\${APPNAME}.lnk" "$INSTDIR\MIDIBlocks.exe" "" "$INSTDIR\logo.ico"
	createShortCut "$DESKTOP\${APPNAME}.lnk" "$INSTDIR\MIDIBlocks.exe" ""
sectionEnd