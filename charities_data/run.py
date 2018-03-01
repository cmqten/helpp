#!/bin/python3

import multiprocessing as mp
import json, os, sys
from geopy.geocoders import ArcGIS, GoogleV3
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
    geolocator = ArcGIS()
    num_located = 0
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
                data[reg_number]['latitude'] = coordinates[0]
                data[reg_number]['longitude'] = coordinates[1]
                success = 1

            except Exception as e:
                print(tid, reg_number, ':', e)

            sleep(1)

        if success == 0:
            failed_location.append(reg_number)
            print(tid, 'coordinates failed:', reg_number)

        else:
            num_located += 1
            print(tid, 'located:', num_located)

    print('Failed coordinates:', failed_location)

    output.put(data)


def split_charities_json(data):
    '''
    Splits the charities data into multiple json files of 1000 entries each.

    Args:
        data (dict) : dictionary of charity data

    Returns:
        None
    '''
    chunk = None
    i = 0
    count = 0
    for key in data:
        if i % 100 == 0:
            if chunk:
                save_to_json(chunk, 'json_parts/charity_data_{}.json'.format(
                    count))
                count += 1
            chunk = dict()

        chunk[key] = data[key]
        i += 1

    if chunk:
        save_to_json(chunk, 'json_parts/charity_data_{}.json'.format(count))


def join_charities_json(src_paths, dst_path):
    '''
    Combines multiple json files containing charity data into one

    Args:
        src_paths ([str]) : list of paths of json files
        dst_path (str) : output json file

    Returns:
        None
    '''
    data = dict()

    for path in src_paths:
        with open(path, encoding='ISO-8859-15') as file:
            chunk = json.load(file)
            data.update(chunk)

    save_to_json(data, dst_path)


def get_location_info(path):
    '''
    Get location for every charity in the json provided by the path.

    Args:
        path (str) : path to the json file

    Returns:
        None
    '''
    charities = json.load(open(path, encoding='ISO-8859-15'))
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

    # Update charities data based on info scraped by processes, then save to json
    for i in range(0, NUM_PROCESSES):
        charities.update(output.get())

    for p in processes:
        p.join()
        print('join')

    save_to_json(charities, 'json_parts_located/' + os.path.basename(path))
    print('saved to', 'json_parts_located/' + os.path.basename(path))


def get_failed_location(charity_data_path, failed_path):
    '''
    Gathers all charities without location info and returns them in a dictionary

    Args:
        data (dict) : dictionary of charity data
        failed_path : path of text file containing id of charities that failed

    Returns:
        dict : dictionary of charity data without location info
    '''
    failed = dict()

    with open(charity_data_path, encoding='ISO-8859-15') as charity_file:
        data = json.load(charity_file)

        with open(failed_path) as file:
            for line in file:
                line = line.strip()
                if line in data:
                    failed[line] = data[line]

    return failed


def main():
    '''
    for i in range(277, 864):
        get_location_info('json_parts/charity_data_{}.json'.format(i))
    '''
    '''
    join_charities_json(['json_parts_located/charity_data_{}.json'.format(i) \
                         for i in range(864)], 'charity_data_located.json')
    '''
    '''
    geolocator = GoogleV3()
    num_located = 0
    data = get_failed_location('charity_data_located_5.json', 'failed.txt')
    failed = []

    for reg_number in data:
        success = 0
        try:
            address = '{} {} {} {} {}'.format(
                data[reg_number]['address'],
                data[reg_number]['city'],
                data[reg_number]['province'],
                data[reg_number]['country'],
                data[reg_number]['postalCode']
            )
            coordinates = get_latitude_longitude(geolocator, address)
            data[reg_number]['latitude'] = coordinates[0]
            data[reg_number]['longitude'] = coordinates[1]
            num_located += 1
            print('located:', num_located)

        except Exception as e:
            failed.append(reg_number)
            print(reg_number, ':', e)

        sleep(60)

    save_to_json(data, 'charity_data_failed_located_5.json')
    '''

    join_charities_json(['charity_data_located_5.json',
                         'charity_data_failed_located_5.json'],
                         'charity_data_located_6.json')





if __name__ == '__main__':
    #split_charities_json(parse_charity_file(sys.argv[1]))
    main()
