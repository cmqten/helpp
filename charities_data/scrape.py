import bs4, re, urllib.request
from bs4 import BeautifulSoup
from geopy.geocoders import ArcGIS
from time import sleep
from user_agent import generate_user_agent


REG_NUMBER_REGEX = re.compile('^([0-9]+)RR([0-9]+)$')
CRA_SEARCH_LINK = 'http://www.cra-arc.gc.ca/ebci/haip/srch/\
advancedsearchresult-eng.action?b={}&q={}&fpe={}-12-31'
CURRENCY_REGEX = re.compile('(\$[0-9,]+)')


def get_latitude_longitude(geolocator, address):
    '''
    Converts an address into latitude and longitude.

    Args:
        geolocator : an instance of a geocoder from the geopy library
        address (str) : an address

    Returns:
        (float, float) : latitude and longitude
    '''
    location = geolocator.geocode(address)
    if not location:
        return (0, 0)
    return (location.latitude, location.longitude)


def remove_tags(contents):
    '''
    The contents of a tag may have additional tags. Remove those tags and
    retrieve the strings in them.

    Args:
        contents (list) : a list containing the contents of a tag

    Return:
        list : a list of strings without the tags
    '''
    contents_clean = []

    for content in contents:
        if isinstance(content, str):
            contents_clean.append(str(content))
        elif isinstance(content, bs4.element.Tag):
            contents_clean += remove_tags(content.contents)

    return contents_clean


def extract_currency(currency_str):
    '''
    Extracts the integer value from a string that represents currency.

    Args:
        currency_str (str) : a string that represents currency

    Return
        int : value as an integer
    '''
    value = CURRENCY_REGEX.search(currency_str)

    if not value:
        return 0

    value = value.group(1)
    value = value.strip().strip('$')
    value = re.sub(',', '', value)

    return int(value)


def get_revenue(soup):
    '''
    Returns the revenue of a charity.

    Args:
        soup (BeautifulSoup) : a data structure that represents an HTML document

    Returns:
        dict : revenue
    '''
    if not soup:
        return dict()

    data = dict()

    # Class names are based on their color on the pie chart, there shouldn't be
    # other tags that used the same class names
    field_class = {'receipted_donations': 'legend-li-red',
                   'non_receipted_donations': 'legend-li-yellow',
                   'gifts_from_other_charities': 'legend-li-blue',
                   'government_funding': 'legend-li-green',
                   'other': 'legend-li-aqua'}

    for field, class_name in field_class.items():
        field_value = soup.find('li', class_=class_name)
        field_value = '$0' if not field_value else \
                      ''.join(remove_tags(field_value.contents))
        data[field] = extract_currency(field_value)

    total = sum(list(data.values()))

    data['total'] = total

    return data


def get_expenses(soup):
    '''
    Returns the expenses of a charity.

    Args:
        soup (BeautifulSoup) : a data structure that represents an HTML document

    Returns:
        dict : expenses
    '''
    if not soup:
        return dict()

    data = dict()

    # Class names are based on their color on the pie chart, there shouldn't be
    # other tags that used the same class names
    field_class = {'charitable_program': 'legend-li-hot-pink',
                   'management_and_admin': 'legend-li-azure-radiance-blue',
                   'fundraising': 'legend-li-pearl-peach',
                   'political_activities': 'legend-li-blue-kimberly',
                   'gifts_to_other': 'legend-li-orange',
                   'other': 'legend-li-dark-green'}

    for field, class_name in field_class.items():
        field_value = soup.find('li', class_=class_name)
        field_value = '$0' if not field_value else \
                      ''.join(remove_tags(field_value.contents))
        data[field] = extract_currency(field_value)

    total = sum(list(data.values()))

    data['total'] = total

    return data


def get_ongoing_programs(soup):
    '''
    Returns the ongoing programs of a charity.

    Args:
        soup (BeautifulSoup) : a data structure that represents an HTML document

    Returns:
        str : ongoing programs
    '''
    if not soup:
        return ''

    contents = soup.find(id='ongoingprograms')

    if not contents:
        return ''

    contents = contents.contents

    # Remove non-strings
    contents_clean = remove_tags(contents)

    # Remove the string 'Ongoing programs:'
    del contents_clean[0]

    if not contents_clean:
        return ''

    # Combine the rest of the elements into one string, and clean up whitespaces
    ongoing_programs = ''
    for content in contents_clean:
        ongoing_programs += content + ''

    return re.sub('\s+', ' ', ongoing_programs).strip()


def scrape_charity_data(reg_number, year):
    '''
    Scrapes expenses, revenue, and ongoing programs for a charity specified by
    the registration number given for a certain year. Throws an exception.

    Args:
        reg_number (str) : charity registration number
        year (int) : year for which the financial info will be scraped

    Returns:
        dict : charity data mentioned above, empty dictionary if reg_number is
               invalid
    '''
    match = REG_NUMBER_REGEX.match(reg_number)

    if not match:
        print(reg_number, 'does not match the registration format')
        return dict()
    
    user_agent_random = generate_user_agent(device_type='desktop')
    
    website = urllib.request.urlopen(
        urllib.request.Request(CRA_SEARCH_LINK.format(match.group(1),
                                                      match.group(2), year),
                               headers={'User-Agent': user_agent_random}),
        timeout=5)

    soup = BeautifulSoup(website, 'html.parser')
    
    return {'revenue': get_revenue(soup),
            'expenses': get_expenses(soup),
            'ongoingPrograms': get_ongoing_programs(soup)}


def scrape_cra_charities(charities, tid=0):
    '''
    Using the registration numbers from the text file, scrape additional info
    about each charity from the CRA website.

    Args:
        charities (dict) : a dictionary from json in which the keys are the
                           registration numbers and the values are charity info.

        tid (int) : thread id in a multithreaded context

    Returns:
        None
    '''
    num_scrape = 0
    failed_scrape = []
    failed_location = []

    print('CRA website')

    # Scrape CRA website
    for reg_number in charities:
        success = 0
        attempts = 0

        # Attempt to scrape a website 5 times before moving on if errors occur
        while success == 0 and attempts < 5:
            attempts += 1
            
            try:
                charities[reg_number].update(scrape_charity_data(reg_number))
                success = 1

            except Exception as e:
                print(tid, reg_number, ':', e)

            sleep(1)

        if success == 0:
            failed_scrape.append(reg_number)
            print(tid, reg_number, ': scraping failed')
            
        else:
            num_scrape += 1
            print(tid, 'scraped:', num_scrape)

    print('Longitude and latitude')

    geolocator = ArcGIS()

    num_scrape = 0

    # Get latitude and longitude
    for reg_number in charities:
        success = 0
        attempts = 0
        match = REG_NUMBER_REGEX.match(reg_number)

        if not match:
            print(reg_number, 'does not match the registration format')
            continue

        while success == 0 and attempts < 5:
            attempts += 1

            try:
                address = charities[reg_number]['address'] + ' ' + \
                          charities[reg_number]['city'] + ' ' + \
                          charities[reg_number]['province'] + ' ' + \
                          charities[reg_number]['country'] + ' ' + \
                          charities[reg_number]['postalCode']

                latitude, longitude = get_latitude_longitude(geolocator, address)
                charities[reg_number]['latitude'] = latitude
                charities[reg_number]['longitude'] = longitude
                success = 1

            except Exception as e:
                print(tid, reg_number, ':', e)

            sleep(1)

        if success == 0:
            failed_location.append(reg_number)
            print(tid, reg_number, ': latitude and longitude failed')
        else:
            num_scrape += 1
            print(tid, 'scraped:', num_scrape)

    print(failed_scrape)
    print(failed_location)
