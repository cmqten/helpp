#!/bin/python3

import sys
import multiprocessing as mp
from parse import parse_charity_file, save_to_json
from scrape import scrape_cra_charities


NUM_PROCESSES = 4


def scrape_cra_charities_thread(data, output, tid):
    '''
    Each thread will scrape their own portion of the charity data from the txt
    file.

    Args:
        data (dict) : dictionary of charity data
        output (multiprocessing.Queue) : output queue of results
        tid (int) : thread id in a multithreaded context

    Returns:
        None
    '''
    scrape_cra_charities(data, tid)
    output.put(data)


def main(argc, argv):
    if argc != 2:
        print('Usage: parse_charity_data.py CHARITY_RAW_DATA_FILE')

    charities = parse_charity_file(argv[1])
    charities_keys = list(charities.keys())
    charities_count = len(charities)
    chunk_size = int(charities_count / NUM_PROCESSES)

    # Split charities dictionary into NUM_PROCESSES parts
    charities_chunks = [dict() for i in range(NUM_PROCESSES)]

    for i in range(0, NUM_PROCESSES-1):
        for j in range(chunk_size * i, chunk_size * (i+1)):
            charities_chunks[i][charities_keys[j]] = charities[charities_keys[j]]

    for i in range((chunk_size * (NUM_PROCESSES-1)), charities_count):
        charities_chunks[NUM_PROCESSES-1][charities_keys[i]] = \
            charities[charities_keys[i]]

    # Spawn NUM_PROCESSES processes to do work on each chunk
    output = mp.Queue()
    processes = [mp.Process(target=scrape_cra_charities_thread,
                            args=(charities_chunks[i], output, i)) \
                 for i in range(0, NUM_PROCESSES)]

    for p in processes:
        p.start()

    print("update")

    # Update charities data based on info scraped by processes, then save to json
    for i in range(0, NUM_PROCESSES):
        charities.update(output.get())

    print("update done")

    for p in processes:
        p.join()
        print("join")

    save_to_json(charities)


if __name__ == '__main__':
    main(len(sys.argv), sys.argv)
