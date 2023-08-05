# Search Engines (SE) - Repository Template
[TOC]

This repository is a template repository for the homework to be developed in the [Search Engines](https://iiia.dei.unipd.it/education/search-engines/) course.

The homeworks are carried out by groups of students and consists in participating to one of the labs organized yearly by [CLEF](https://www.clef-initiative.eu/) (Conference and Labs of the Evaluation Forum).

*Search Engines* is a course of the

* [Master Degree in Computer Engineering](https://degrees.dei.unipd.it/master-degrees/computer-engineering/) of the  [Department of Information Engineering](https://www.dei.unipd.it/en/), [University of Padua](https://www.unipd.it/en/), Italy.
* [Master Degree in Data Science](https://datascience.math.unipd.it/) of the  [Department of Mathematics "Tullio Levi-Civita"](https://www.math.unipd.it/en/), [University of Padua](https://www.unipd.it/en/), Italy.

*Search Engines* is part of the teaching activities of the [Intelligent Interactive Information Access (IIIA) Hub](http://iiia.dei.unipd.it/).

## Project Description ##
The project considers both English and French documents and queries from [TrecEval dataset](https://clef-longeval.github.io/tasks/).  
We generate character N-grams to identify common word structures (as prefixes or suffixes) repeated over documents.  
We also use query expansion with synonyms (in English) and the Natural Language Processing (NLP) technique of Named Entity Recognition (NER) to further refine our system. Our system was developed in Java, mainly using the library Lucene.
### Repository organization ###

The repository is organized as follows (only main dir exposed):
```bash
├───code
│   ├───src
│   │   └───main
│   │       ├───java
│   │       │   ├───analyze
│   │       │   ├───index
│   │       │   ├───parse
│   │       │   ├───search
│   │       │   └───topic
│   └───target
│       ├───classes
│       │   ├───analyze
│       │   ├───index
│       │   ├───parse
│       │   ├───prolog
│       │   ├───search
│       │   └───topic
├───homework-1
├───homework-2
├───results
├───runs
│   └───experiments
│       └───scores
└───slides
```

### Group members ###
- Isil Atabek  
- Huimin Chen  
- Jesús Moncada Ramírez  
- Nicolò Santini  
- Giovanni Zago

## Navigate through scores ##
```bash
results\JIHUMING_scores.xlsx
```
provides two tables and relative Pivot tables and charts for a complete view of all used scores for the project. The file is protected, but by using slicers in `Analysis` pages different comparisons can easily be done.
## Scoring runs with trec_eval #
To provide help to evaluate all runs, a Linux-based pip script can be used. Just ensure your trec_eval directory is in ../trec_eval-9.0.7 and run it.  
Variable `run` decide which evaluation will be used:
```bash
# 1 = all runs
# 2 = short term runs
# 3 = long term runs
# 4 = held out test
```
After setting `run` value as you prefer, launch py with this terminal command:
```sh
python3 runTrecEval.py
```
It generates a .txt for each run, plus allmaps.txt, with MAP and NDCG scores from every run in the same file in
```bash
runs/experiments/scores
```
Output files' name generally is `output<score_metrics>.txt` for files with all results and `<score_metrics>_scores.`txt` for sum-up files with just MAP, NDCG and Rprec scores.

### License ###

All the contents of this repository are shared using the [Creative Commons Attribution-ShareAlike 4.0 International License](http://creativecommons.org/licenses/by-sa/4.0/).

![CC logo](https://i.creativecommons.org/l/by-sa/4.0/88x31.png)