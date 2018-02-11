import json, sys
import multiprocessing as mp
from scrape_charity_data import get_revenue, get_expenses, \
     get_ongoing_programs, scrape_cra_charities


NUM_PROCESSES = 4


def parse_charity_file(filename):
    '''
    Parses text file which contains charity data and returns a dictionary of all
    these data.

    Args:
        filename (str) : text file path

    Returns:
        dict : dictionary containing all the data from the text file
    '''
    charity_data = dict()
    
    # file contains French characters
    with open(filename, encoding='ISO-8859-15') as file:
        file.readline()

        limit = 50
        count = 0
        for line in file:
            line_data = line.split('\t')
            registration = line_data[0]
            data = {'name':            line_data[1],
                    'designationCode': line_data[5],
                    'categoryCode':    line_data[6],
                    'address':         line_data[7],
                    'city':            line_data[8],
                    'province':        line_data[9],
                    'country':         line_data[10],
                    'postalCode':      line_data[11]
                   }
            charity_data[registration] = data

            # small data only for testing, comment out when not testing
            count += 1
            if count >= limit: 
                break
    
    return charity_data


def save_to_json(data):
    '''
    Saves data to json file.

    Args:
        data (dict) : dictionary containing data to be saved to json file

    Returns:
        None
    '''
    with open('charity_data_json.json', 'w', encoding='ISO-8859-15') as file:
        json.dump(data, file)


def scrape_cra_charities_thread(data, output):
    '''
    Each thread will scrape their own portion of the charity data from the txt
    file.

    Args:
        data (dict) : dictionary of charity data
        output (multiprocessing.Queue) : output queue of results

    Returns:
        None
    '''
    scrape_cra_charities(data)
    output.put(data)


def main(argc, argv):
    if argc != 2:
        print('Usage: parse_charity_data.py CHARITY_RAW_DATA_FILE')

    charities = parse_charity_file(argv[1])
    charities_count = len(charities)
    chunk_size = int(charities_count / 4)

    charities_keys = list(charities.keys())

    # Split charities dictionary into four parts
    charities_chunks = [dict(), dict(), dict(), dict()]

    for i in range(0, NUM_PROCESSES-1):
        for j in range(chunk_size * i, chunk_size * (i+1)):
            charities_chunks[i][charities_keys[j]] = charities[charities_keys[j]]

    for i in range((chunk_size * (NUM_PROCESSES-1)), charities_count):
        charities_chunks[NUM_PROCESSES-1][charities_keys[i]] = \
            charities[charities_keys[i]]

    # Spawn four processes to do work
    output = mp.Queue()
    processes = [mp.Process(target=scrape_cra_charities_thread,
                            args=(charities_chunks[i], output)) \
                 for i in range(0, NUM_PROCESSES)]

    for p in processes:
        p.start()

    for p in processes:
        p.join()

    # Update charities data based on info scraped by processes, then save to json
    for i in range(0, NUM_PROCESSES):
        charities.update(output.get())

    save_to_json(charities)


if __name__ == '__main__':
    main(len(sys.argv), sys.argv)
