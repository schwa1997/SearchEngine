# This pip script runs trec_eval all in all our runs and save the output to a text file.
# The output of the trec_eval is saved in runs/experiments/scores/ folder.
# It also saves, for each run, map score in runs/experiments/scores/all_maps.txt

# trec_eval executable have to me in ../trec_eval-9.0.7 folder

import subprocess

if __name__ == '__main__':
    # Chose trec eval script to run with above parameters
    # 1 = train runs
    # 2 = short-term test runs
    # 3 = long-term test runs
    # 4 = held-out test runs
    run = 3

    # Choose your OS
    # 1 = linux
    # 2 = windows
    os = 2

    if os == 1:
        trec_eval_command = '../trec_eval-9.0.7/trec_eval'
    elif os == 2:
        trec_eval_command = "..\\trec_eval-9.0.7\\trec_eval.exe"

    scripts = list()

    if run == 1:
        # trec_eval all_trec commands
        # scripts.append(trec_eval_command+" -m all_trec runs/experiments/qrels.txt runs/experiments/seupd2223-JIHUMING-01_en_en.TRAIN")
        # scripts.append(trec_eval_command+" -m all_trec runs/experiments/qrels.txt runs/experiments/seupd2223-JIHUMING-02_en_en_3gram.TRAIN")
        # scripts.append(trec_eval_command+" -m all_trec runs/experiments/qrels.txt runs/experiments/seupd2223-JIHUMING-03_en_en_4gram.TRAIN")
        # scripts.append(trec_eval_command+" -m all_trec runs/experiments/qrels.txt runs/experiments/seupd2223-JIHUMING-04_en_en_5gram.TRAIN")
        # scripts.append(trec_eval_command+" -m all_trec runs/experiments/qrels.txt runs/experiments/seupd2223-JIHUMING-05_en_en_fr_5gram.TRAIN")
        # scripts.append(trec_eval_command+" -m all_trec runs/experiments/qrels.txt runs/experiments/seupd2223-JIHUMING-06_en_en_4gram_ner.TRAIN")
        scripts.append(trec_eval_command+" -m all_trec runs/experiments/qrels.txt runs/experiments/seupd2223-JIHUMING-07_fr_fr.TRAIN")
        scripts.append(trec_eval_command+" -m all_trec runs/experiments/qrels.txt runs/experiments/seupd2223-JIHUMING-08_fr_fr_3gram.TRAIN")
        scripts.append(trec_eval_command+" -m all_trec runs/experiments/qrels.txt runs/experiments/seupd2223-JIHUMING-09_fr_fr_4gram.TRAIN")
        scripts.append(trec_eval_command+" -m all_trec runs/experiments/qrels.txt runs/experiments/seupd2223-JIHUMING-10_fr_fr_5gram.TRAIN")
        # scripts.append(trec_eval_command+" -m all_trec runs/experiments/qrels.txt runs/experiments/seupd2223-JIHUMING-11_fr_en_fr_5gram.TRAIN")
        scripts.append(trec_eval_command+" -m all_trec runs/experiments/qrels.txt runs/experiments/seupd2223-JIHUMING-12_fr_fr_4gram_ner.TRAIN")

        outFileName = "runs/experiments/scores/scores_TRAIN"
        scoretype = "TRAIN"
    if run == 2:
        # trec_eval all_trec commands - SHORT TERM
        scripts.append(trec_eval_command+" -m all_trec runs/experiments/longeval-relevance-judgements/a-short-july.txt runs/experiments/seupd2223-JIHUMING-07_fr_fr/seupd2223-JIHUMING-07_fr_fr.ST")
        scripts.append(trec_eval_command+" -m all_trec runs/experiments/longeval-relevance-judgements/a-short-july.txt runs/experiments/seupd2223-JIHUMING-08_fr_fr_3gram/seupd2223-JIHUMING-08_fr_fr_3gram.ST")
        scripts.append(trec_eval_command+" -m all_trec runs/experiments/longeval-relevance-judgements/a-short-july.txt runs/experiments/seupd2223-JIHUMING-09_fr_fr_4gram/seupd2223-JIHUMING-09_fr_fr_4gram.ST")
        scripts.append(trec_eval_command+" -m all_trec runs/experiments/longeval-relevance-judgements/a-short-july.txt runs/experiments/seupd2223-JIHUMING-10_fr_fr_5gram/seupd2223-JIHUMING-10_fr_fr_5gram.ST")
        scripts.append(trec_eval_command+" -m all_trec runs/experiments/longeval-relevance-judgements/a-short-july.txt runs/experiments/seupd2223-JIHUMING-12_fr_fr_4gram_ner/seupd2223-JIHUMING-12_fr_fr_4gram_ner.ST")

        outFileName = "runs/experiments/scores/scores_ST"
        scoretype = "ST"
    if run == 3:
        # trec_eval all_trec commands - LONG TERM
        scripts.append(trec_eval_command+" -m all_trec runs/experiments/longeval-relevance-judgements/b-long-september.txt runs/experiments/seupd2223-JIHUMING-07_fr_fr/seupd2223-JIHUMING-07_fr_fr.LT")
        scripts.append(trec_eval_command+" -m all_trec runs/experiments/longeval-relevance-judgements/b-long-september.txt runs/experiments/seupd2223-JIHUMING-08_fr_fr_3gram/seupd2223-JIHUMING-08_fr_fr_3gram.LT")
        scripts.append(trec_eval_command+" -m all_trec runs/experiments/longeval-relevance-judgements/b-long-september.txt runs/experiments/seupd2223-JIHUMING-09_fr_fr_4gram/seupd2223-JIHUMING-09_fr_fr_4gram.LT")
        scripts.append(trec_eval_command+" -m all_trec runs/experiments/longeval-relevance-judgements/b-long-september.txt runs/experiments/seupd2223-JIHUMING-10_fr_fr_5gram/seupd2223-JIHUMING-10_fr_fr_5gram.LT")
        scripts.append(trec_eval_command+" -m all_trec runs/experiments/longeval-relevance-judgements/b-long-september.txt runs/experiments/seupd2223-JIHUMING-12_fr_fr_4gram_ner/seupd2223-JIHUMING-12_fr_fr_4gram_ner.LT")

        outFileName = "runs/experiments/scores/scores_LT"
        scoretype = "LT"
    if run == 4:
        # trec_eval all_trec commands - LONG TERM
        scripts.append(trec_eval_command+" -m all_trec runs/experiments/longeval-relevance-judgements/heldout-test.txt runs/experiments/seupd2223-JIHUMING-07_fr_fr/seupd2223-JIHUMING-07_fr_fr.WT")
        scripts.append(trec_eval_command+" -m all_trec runs/experiments/longeval-relevance-judgements/heldout-test.txt runs/experiments/seupd2223-JIHUMING-08_fr_fr_3gram/seupd2223-JIHUMING-08_fr_fr_3gram.WT")
        scripts.append(trec_eval_command+" -m all_trec runs/experiments/longeval-relevance-judgements/heldout-test.txt runs/experiments/seupd2223-JIHUMING-09_fr_fr_4gram/seupd2223-JIHUMING-09_fr_fr_4gram.WT")
        scripts.append(trec_eval_command+" -m all_trec runs/experiments/longeval-relevance-judgements/heldout-test.txt runs/experiments/seupd2223-JIHUMING-10_fr_fr_5gram/seupd2223-JIHUMING-10_fr_fr_5gram.WT")
        scripts.append(trec_eval_command+" -m all_trec runs/experiments/longeval-relevance-judgements/heldout-test.txt runs/experiments/seupd2223-JIHUMING-12_fr_fr_4gram_ner/seupd2223-JIHUMING-12_fr_fr_4gram_ner.WT")

        outFileName = "runs/experiments/scores/scores_WT"
        scoretype = "WT"

    # Clear file
    with open(outFileName, 'w') as f:
        pass

    for i, script in enumerate(scripts, start=1):
        result = subprocess.run(script, shell=True, capture_output=True, text=True)

        # Save the complete output of trec_eval
        with open('runs/experiments/scores/output' + scoretype + str(i) + '.txt', 'w') as f:
            f.write(result.stdout)

        # Get the lines we are interested in the output (MAP, RPREC, NDCG)
        title_line = result.stdout.split('\n')[0]
        map_line = result.stdout.split('\n')[5]
        rprec_line = result.stdout.split('\n')[7]
        ndcg_line = result.stdout.split('\n')[55]

        #Save the lines we are interested to a text file
        with open(outFileName + '.txt', 'a') as f:
            f.write(f"{title_line}\n{map_line}\n{rprec_line}\n{ndcg_line}\n")