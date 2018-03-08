import json


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

        limit = 10
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
                    'postalCode':      line_data[11],
                    'twitter':         'None',
                    'instagram':       'None',
                    'facebook':        'None',
                    'youtube':         'None',
                    'website':         'None',
                    'summary':         'None',
                    'logo':            'None'
                   }
            charity_data[registration] = data

            # small data only for testing, comment out when not testing
            '''
            count += 1
            if count >= limit:
                break
            '''
    return charity_data


def save_to_json(data, path):
    '''
    Saves data to json file.

    Args:
        data (dict) : dictionary containing data to be saved to json file
        path (str) : path to file

    Returns:
        None
    '''
    with open(path, 'w', encoding='ISO-8859-15') as file:
        json.dump(data, file)
