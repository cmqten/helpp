#!/bin/python3

import multiprocessing as mp
import sys
from geopy.geocoders import ArcGIS
from parse import parse_charity_file, save_to_json
from scrape import get_latitude_longitude, scrape_charity_data
from time import sleep


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
    num_scraped = 0
    failed_scrape = []

    print(tid, 'CRA website')

    # Scrape CRA website
    for reg_number in data:
        success = 0
        attempts = 0

        # 5 attempts before moving on
        while success == 0 and attempts < 5:
            attempts += 1
            try:
                data[reg_number].update(scrape_charity_data(reg_number, 2016))
                success = 1
                
            except Exception as e:
                print(tid, reg_number, ':', e)
                
            sleep(1)

        if success == 0:
            failed_scrape.append(reg_number)
            print(tid, reg_number, ': scraping failed')

        else:
            num_scraped += 1
            print(tid, 'scraped:', num_scraped)

    print(tid, 'coordinates')

    geolocator = ArcGIS()
    num_scraped = 0
    failed_location = []

    # Get coordinates
    for reg_number in data:
        success = 0
        attempts = 0

        # 5 attempts before moving on
        while success == 0 and attempts < 5:
            attempts += 1
            try:
                address = '{} {} {} {} {}'.format(
                    data[reg_number]['address'],
                    data[reg_number]['city'],
                    data[reg_number]['province'],
                    data[reg_number]['country'],
                    data[reg_number]['postalCode']
                )
                coordinates = get_latitude_longitude(geolocator, address)
                data[reg_number]['coordinates'] = coordinates
                success = 1
                
            except Exception as e:
                print(tid, reg_number, ':', e)

            sleep(1)

        if success == 0:
            failed_location.append(reg_number)
            print(tid, reg_number, ': coordinates failed')
            
        else:
            num_scraped += 1
            print(tid, 'scraped:', num_scraped)

    print('Failed scrape:', failed_scrape)
    print('Failed coordinates:', failed_location)
        
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

    for p in processes:
        p.join()
        print("join")

    # Update charities data based on info scraped by processes, then save to json
    for i in range(0, NUM_PROCESSES):
        charities.update(output.get())

    save_to_json(charities)


if __name__ == '__main__':
    main(len(sys.argv), sys.argv)
