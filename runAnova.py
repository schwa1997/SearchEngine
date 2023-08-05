# trec_eval executable have to me in ../trec_eval-9.0.7 folder

import sys
import subprocess
import pandas as pd
import statsmodels.api as sm
from statsmodels.formula.api import ols
from bioinfokit.analys import stat

def build_trec_eval_call(arguments, qrels, run):
    return TREC_EVAL_COMMAND+" "+arguments+" "+qrels+" "+run

def get_map_from_line(line):
    return float(line.split('\t')[2])

def get_query_from_line(line):
    return line.split('\t')[1]

if __name__ == '__main__':
    # Chose trec eval script to run with above parameters
    # 1 = train runs
    # 2 = short-term test runs
    # 3 = long-term test runs
    # 4 = held-out test runs
    run = None
    if len(sys.argv) > 1:
        run = int(sys.argv[1])
    else:
        print("No set of runs provided. Exiting...")
        exit()

    # Choose your OS
    # 1 = linux
    # 2 = windows
    os = 2

    TREC_EVAL_COMMAND = ""
    if os == 1:
        TREC_EVAL_COMMAND = '../trec_eval-9.0.7/trec_eval'
    elif os == 2:
        TREC_EVAL_COMMAND = "..\\trec_eval-9.0.7\\trec_eval.exe"

    TREC_EVAL_ARGUMENTS = "-q"

    QRELS_TRAIN = "runs/experiments/qrels.txt"
    QRELS_ST = "runs/experiments/longeval-relevance-judgements/a-short-july.txt"
    QRELS_LT = "runs/experiments/longeval-relevance-judgements/b-long-september.txt"
    QRELS_WT = "runs/experiments/longeval-relevance-judgements/heldout-test.txt"

    sys_calls = list()
    sys_names = ['seupd2223-JIHUMING-07_fr_fr',
                 'seupd2223-JIHUMING-08_fr_fr_3gram',
                 'seupd2223-JIHUMING-09_fr_fr_4gram',
                 'seupd2223-JIHUMING-10_fr_fr_5gram',
                 'seupd2223-JIHUMING-12_fr_fr_4gram_ner']
    sys_aps = [[], [], [], [], []]

    queries = [[], [], [], [], []]

    if run == 1:
        print('[INFO] Executing in training runs')
        sys_calls.append(
            build_trec_eval_call(TREC_EVAL_ARGUMENTS, QRELS_TRAIN, "runs/experiments/seupd2223-JIHUMING-07_fr_fr.TRAIN"))
        sys_calls.append(
            build_trec_eval_call(TREC_EVAL_ARGUMENTS, QRELS_TRAIN, "runs/experiments/seupd2223-JIHUMING-08_fr_fr_3gram.TRAIN"))
        sys_calls.append(
            build_trec_eval_call(TREC_EVAL_ARGUMENTS, QRELS_TRAIN, "runs/experiments/seupd2223-JIHUMING-09_fr_fr_4gram.TRAIN"))
        sys_calls.append(
            build_trec_eval_call(TREC_EVAL_ARGUMENTS, QRELS_TRAIN, "runs/experiments/seupd2223-JIHUMING-10_fr_fr_5gram.TRAIN"))
        sys_calls.append(
            build_trec_eval_call(TREC_EVAL_ARGUMENTS, QRELS_TRAIN, "runs/experiments/seupd2223-JIHUMING-12_fr_fr_4gram_ner.TRAIN"))

    elif run == 2:
        # trec_eval all_trec commands - SHORT TERM
        print('[INFO] Executing in short-term test runs')
        sys_calls.append(
            build_trec_eval_call(TREC_EVAL_ARGUMENTS, QRELS_ST, "runs/experiments/seupd2223-JIHUMING-07_fr_fr/seupd2223-JIHUMING-07_fr_fr.ST"))
        sys_calls.append(
            build_trec_eval_call(TREC_EVAL_ARGUMENTS, QRELS_ST, "runs/experiments/seupd2223-JIHUMING-08_fr_fr_3gram/seupd2223-JIHUMING-08_fr_fr_3gram.ST"))
        sys_calls.append(
            build_trec_eval_call(TREC_EVAL_ARGUMENTS, QRELS_ST, "runs/experiments/seupd2223-JIHUMING-09_fr_fr_4gram/seupd2223-JIHUMING-09_fr_fr_4gram.ST"))
        sys_calls.append(
            build_trec_eval_call(TREC_EVAL_ARGUMENTS, QRELS_ST, "runs/experiments/seupd2223-JIHUMING-10_fr_fr_5gram/seupd2223-JIHUMING-10_fr_fr_5gram.ST"))
        sys_calls.append(
            build_trec_eval_call(TREC_EVAL_ARGUMENTS, QRELS_ST, "runs/experiments/seupd2223-JIHUMING-12_fr_fr_4gram_ner/seupd2223-JIHUMING-12_fr_fr_4gram_ner.ST"))

    elif run == 3:
        # trec_eval all_trec commands - LONG TERM
        print('[INFO] Executing in long-term test runs')
        sys_calls.append(
            build_trec_eval_call(TREC_EVAL_ARGUMENTS, QRELS_LT, "runs/experiments/seupd2223-JIHUMING-07_fr_fr/seupd2223-JIHUMING-07_fr_fr.LT"))
        sys_calls.append(
            build_trec_eval_call(TREC_EVAL_ARGUMENTS, QRELS_LT, "runs/experiments/seupd2223-JIHUMING-08_fr_fr_3gram/seupd2223-JIHUMING-08_fr_fr_3gram.LT"))
        sys_calls.append(
            build_trec_eval_call(TREC_EVAL_ARGUMENTS, QRELS_LT, "runs/experiments/seupd2223-JIHUMING-09_fr_fr_4gram/seupd2223-JIHUMING-09_fr_fr_4gram.LT"))
        sys_calls.append(
            build_trec_eval_call(TREC_EVAL_ARGUMENTS, QRELS_LT, "runs/experiments/seupd2223-JIHUMING-10_fr_fr_5gram/seupd2223-JIHUMING-10_fr_fr_5gram.LT"))
        sys_calls.append(
            build_trec_eval_call(TREC_EVAL_ARGUMENTS, QRELS_LT, "runs/experiments/seupd2223-JIHUMING-12_fr_fr_4gram_ner/seupd2223-JIHUMING-12_fr_fr_4gram_ner.LT"))

    elif run == 4:
        # trec_eval all_trec commands - HELD-OUT TERM
        print('[INFO] Executing in held-out train runs')
        sys_calls.append(
            build_trec_eval_call(TREC_EVAL_ARGUMENTS, QRELS_WT, "runs/experiments/seupd2223-JIHUMING-07_fr_fr/seupd2223-JIHUMING-07_fr_fr.WT"))
        sys_calls.append(
            build_trec_eval_call(TREC_EVAL_ARGUMENTS, QRELS_WT, "runs/experiments/seupd2223-JIHUMING-08_fr_fr_3gram/seupd2223-JIHUMING-08_fr_fr_3gram.WT"))
        sys_calls.append(
            build_trec_eval_call(TREC_EVAL_ARGUMENTS, QRELS_WT, "runs/experiments/seupd2223-JIHUMING-09_fr_fr_4gram/seupd2223-JIHUMING-09_fr_fr_4gram.WT"))
        sys_calls.append(
            build_trec_eval_call(TREC_EVAL_ARGUMENTS, QRELS_WT, "runs/experiments/seupd2223-JIHUMING-10_fr_fr_5gram/seupd2223-JIHUMING-10_fr_fr_5gram.WT"))
        sys_calls.append(
            build_trec_eval_call(TREC_EVAL_ARGUMENTS, QRELS_WT, "runs/experiments/seupd2223-JIHUMING-12_fr_fr_4gram_ner/seupd2223-JIHUMING-12_fr_fr_4gram_ner.WT"))

    for i, call in enumerate(sys_calls, start=1):

        result = subprocess.run(call, shell=True, capture_output=True, text=True)
        result_list = result.stdout.split('\n')

        for line_n, line in enumerate(result_list):
            if line.startswith('map') and 'all' not in line:
                # EXCEPTION detected in our long-term runs
                query = get_query_from_line(line)
                map = get_map_from_line(line)

                # EXCEPTION detected in seupd2223-JIHUMING-07_fr_fr.WT the query q0922511 has not been evaluated
                if run == 3 and i == 1 and line_n == 21198:
                    sys_aps[i - 1].append(0.0)
                    queries[i - 1].append('q0922511')

                sys_aps[i - 1].append(map)
                queries[i - 1].append(query)

    print("[CHECK] Detected {0} topics".format(len(queries[0])))

    run_ap = {
        sys_names[0] : sys_aps[0],
        sys_names[1] : sys_aps[1],
        sys_names[2] : sys_aps[2],
        sys_names[3] : sys_aps[3],
        sys_names[4] : sys_aps[4]
    }

    df = pd.DataFrame(run_ap, index=queries[0])

    # reshape the d dataframe suitable for statsmodels package
    df_melted = pd.melt(df.reset_index(), id_vars=['index'], value_vars=df.columns)
    # replace column names
    df_melted.columns = ['topic', 'system', 'average_precision']

    model = ols("average_precision ~ C(system)", data=df_melted).fit()
    anova_table = sm.stats.anova_lm(model, typ=2)

    #print(anova_table)

    # ANOVA

    res = stat()
    res.anova_stat(df=df_melted, res_var='average_precision', anova_model='average_precision ~ C(system)')

    print(res.anova_summary)

    # TUKEY-HSD

    # perform multiple pairwise comparison (Tukey's HSD)
    # unequal sample size data, tukey_hsd uses Tukey-Kramer test
    res = stat()
    res.tukey_hsd(df=df_melted, res_var='average_precision', xfac_var='system', anova_model='average_precision ~ C(system)')
    print(res.tukey_summary)