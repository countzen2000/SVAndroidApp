#!/usr/bin/perl -w

# ===  REVISION 2 (Jan 25, 2011)


# use module
use strict;
use XML::Simple;

# create object
my $xml = new XML::Simple;

# read XML file
my $manifest = $xml->XMLin("AndroidManifest.xml", ForceArray=>1);
print "\n *** EXISTINGversionCode: $manifest->{'android:versionCode'} \n *** EXISTINGversionName: $manifest->{'android:versionName'}\n";

# bump version and build number(need to append ".0" since it becomes an integer)
my $vCode = $manifest->{'android:versionCode'} + 1;
#my $vName = ($manifest->{'android:versionName'} + 1) . ".0";

# print out new codes
print " *** NEWversionCode: $vCode \n\n";# *** NEWversionName: $vName\n";

# edit xml object with new values
$manifest->{'android:versionCode'}=$vCode;
#$manifest->{'android:versionName'}=$vName;

# print out new xml, adding in header line and replacing root element
my $xmlheader = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
my $newManifest =  $xml->XMLout($manifest, AttrIndent=>1, RootName=>'manifest');

open (OUTFILE, '>AndroidManifest.xml');
print OUTFILE $xmlheader;
print OUTFILE $newManifest;
close (OUTFILE);

