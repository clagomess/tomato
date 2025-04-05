#!/bin/bash

# sudo apt install php php-xml
# git clone --depth=100000 https://github.com/torvalds/linux.git

# %h - abbreviated commit hash
# %an - author name
# %ae - author email
# %at - author date, UNIX timestamp
# %aI - author date, strict ISO 8601 format
# %as - author date, short format (YYYY-MM-DD)
# %D - ref names without the " (", ")" wrapping.
# %s - subject
git --git-dir=/mnt/c/Users/claudio/Downloads/linux/.git \
log --decorate=short -n100000 \
--pretty=format:'%h; %an; %ae; %at; %aI; %as; %D; %s' | php build-large-test-file.php
