#!/usr/bin/perl -w

# ===  REVISION 2 (Jan 25, 2011)

# use module
use strict;
use XML::Simple;

# create object
my $xml = new XML::Simple;

# read XML file
my $manifest = $xml->XMLin("AndroidManifest.xml", ForceArray=>1);

my $vCode = $manifest->{'android:versionCode'};

print $vCode;