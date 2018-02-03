import json, sys


def parse_charity_file(filename):
    """
    Parses text file which contains charity data and returns a dictionary of all
    these data.

    Args:
        filename (str) : text file path

    Returns:
        dict : dictionary containing all the data from the text file
    """
    charity_data = []
    
    # file contains French characters
    with open(filename, encoding='ISO-8859-15') as file:
        file.readline()
        
        for line in file:
            line_data = line.split('\t')
            data = {'registration':    line_data[0],
                    'name':            line_data[1],
                    'status':          line_data[2],
                    'dateOfStatus':    line_data[3],
                    'sanction':        line_data[4],
                    'designationCode': line_data[5],
                    'categoryCode':    line_data[6],
                    'address':         line_data[7],
                    'city':            line_data[8],
                    'province':        line_data[9],
                    'country':         line_data[10],
                    'postalCode':      line_data[11]
                   }
            charity_data.append(data)

    charity_data = {'data': charity_data}
    
    return charity_data


def save_to_json(data):
    """
    Saves data to json file.

    Args:
        data (dict) : dictionary containing data to be saved to json file

    Returns:
        None
    """
    with open('charity_data_json.txt', 'w', encoding='ISO-8859-15') as file:
        json.dump(data, file)


def main(argc, argv):
    if argc != 2:
        print('Usage: parse_charity_data.py CHARITY_RAW_DATA_FILE')

    save_to_json(parse_charity_file(argv[1]))


if __name__ == '__main__':
    main(len(sys.argv), sys.argv)
