#!/bin/bash

rm -rf public/mathjax
mkdir -p public/mathjax
cp -r node_modules/mathjax/es5/tex-chtml.js public/mathjax/
cp -r node_modules/mathjax/es5/tex-chtml.js public/mathjax/

mkdir -p public/mathjax/input/tex
cp -r node_modules/mathjax/es5/input/tex/extensions public/mathjax/input/tex/

mkdir -p public/mathjax/output/chtml/fonts
cp -r node_modules/mathjax/es5/output/chtml/fonts/woff-v2 public/mathjax/output/chtml/fonts/
