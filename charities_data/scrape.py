import bs4, re, urllib.request
from bs4 import BeautifulSoup
from user_agent import generate_user_agent


REG_NUMBER_REGEX = re.compile('^([0-9]+)RR([0-9]+)$')
CRA_SEARCH_LINK = 'http://www.cra-arc.gc.ca/ebci/haip/srch/\
advancedsearchresult-eng.action?b={}&q={}'
CRA_SEARCH_LINK_YEAR = 'http://www.cra-arc.gc.ca/ebci/haip/srch/\
advancedsearchresult-eng.action?b={}&q={}&fpe={}'
CURRENCY_REGEX = re.compile('(\$[0-9,]+)')
DATE_REGEX = re.compile('^[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]$')


def get_latitude_longitude(geolocator, address):
    '''
    Converts an address into latitude and longitude.

    Args:
        geolocator : an instance of a geocoder from the geopy library
        address (str) : an address

    Returns:
        [float, float] : latitude and longitude
    '''
    location = geolocator.geocode(address)
    if not location:
        return [0, 0]
    return [location.latitude, location.longitude]


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


def clean_int_literal(int_str):
    '''
    Removes any whitespaces or commas.
    '''
    if not int_str:
        return ''
    
    return re.sub('[^0-9]', '', int_str)


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
    field_class = {'revenue_receipted_donations': 'legend-li-red',
                   'revenue_non_receipted_donations': 'legend-li-yellow',
                   'revenue_gifts_from_other_charities': 'legend-li-blue',
                   'revenue_government_funding': 'legend-li-green',
                   'revenue_other': 'legend-li-aqua'}

    for field, class_name in field_class.items():
        field_value = soup.find('li', class_=class_name)
        field_value = '$0' if not field_value else \
                      ''.join(remove_tags(field_value.contents))
        data[field] = extract_currency(field_value)

    total = sum(list(data.values()))

    data['revenue_total'] = total

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
    field_class = {'expenses_charitable_program': 'legend-li-hot-pink',
                   'expenses_management_and_admin': \
                       'legend-li-azure-radiance-blue',
                   'expenses_fundraising': 'legend-li-pearl-peach',
                   'expenses_political_activities': 'legend-li-blue-kimberly',
                   'expenses_gifts_to_other': 'legend-li-orange',
                   'expenses_other': 'legend-li-dark-green'}

    for field, class_name in field_class.items():
        field_value = soup.find('li', class_=class_name)
        field_value = '$0' if not field_value else \
                      ''.join(remove_tags(field_value.contents))
        data[field] = extract_currency(field_value)

    total = sum(list(data.values()))

    data['expenses_total'] = total

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


def get_financial_dates(soup):
    '''
    Scrapes all the dates in which the charity submitted financial information.

    Args:
        soup (BeautifulSoup) : a data structure that represents an HTML document

    Returns:
        [str] : list of dates
    '''
    if not soup:
        return []

    list_dates = soup.find('ul', class_='list-unstyled mrgn-lft-md')

    if not list_dates:
        return []

    list_dates = list_dates.find_all('li')

    if not list_dates:
        return []
    
    dates = []
    
    for date in list_dates:
        date_clean = ''.join(remove_tags(date.contents)).strip()
        if DATE_REGEX.match(date_clean):
            dates.append(date_clean)

    return dates


def get_compensation(soup):
    '''
    Scrapes compensation data for a charity.

    Args:
        soup (BeautifulSoup) : a data structure that represents an HTML document

    Returns:
        dict : dictionary containing compensation data
    '''
    data = dict()
    
    if not soup:
        return data

    compensation = soup.find(id='Compensation')

    if not compensation:
        return data

    compensation_data = compensation.find_next_sibling('div')

    if not compensation_data:
        return data

    compensation_list = compensation_data.find('ul')

    if compensation_list:
        compensation_list_data = compensation_list.find_all('li')

        # Don't ask me about this monstrosity
        data['total_compensation'] = \
            extract_currency(''.join(remove_tags(compensation_list_data[0].find(
                'div').findAll('div')[1].contents)).strip())
        
        data['full_time'] = \
            clean_int_literal(''.join(remove_tags(compensation_list_data[1].find(
                'div').findAll('div')[1].contents)).strip())
        
        data['full_time'] = int(data['full_time']) if data['full_time'] else 0
            
        data['part_time'] = \
            clean_int_literal(''.join(remove_tags(compensation_list_data[2].find(
                'div').findAll('div')[1].contents)).strip())

        data['part_time'] = int(data['part_time']) if data['part_time'] else 0
        
        data['professional_consulting_fees'] = \
            extract_currency(''.join(remove_tags(compensation_list_data[3].find(
                'div').findAll('div')[1].contents)).strip())

        compensated_full_time = compensation_list.find_next_sibling('div')

        if compensated_full_time:
            data['compensated_full_time'] = dict()
            salaries = compensated_full_time.findAll('div', class_='row')
            for salary in salaries:
                salary_data = salary.findAll('div')
                salary_range = ''.join(remove_tags(
                    salary_data[0].contents)).strip()
                salary_earners = int(''.join(remove_tags(
                    salary_data[1].contents)).strip())
                data['compensated_full_time'][salary_range] = salary_earners

    return data


def scrape_charity_data(reg_number, date=None):
    '''
    Scrapes expenses, revenue, and ongoing programs for a charity specified by
    the registration number given for a certain year. Throws an exception.

    Args:
        reg_number (str) : charity registration number
        date (str) : date for which the financial info will be scraped

    Returns:
        dict : charity data mentioned above, empty dictionary if reg_number is
               invalid
    '''
    match = REG_NUMBER_REGEX.match(reg_number)

    data = dict()

    if not match:
        print(reg_number, 'does not match the registration format')
        return dict()
    
    user_agent_random = generate_user_agent(device_type='desktop')

    cra_url = CRA_SEARCH_LINK.format(match.group(1), match.group(2)) \
              if not date else \
              CRA_SEARCH_LINK_YEAR.format(match.group(1), match.group(2),
                                          date)
    
    website = urllib.request.urlopen(
        urllib.request.Request(cra_url,
                               headers={'User-Agent': user_agent_random}),
        timeout=5)

    soup = BeautifulSoup(website, 'html.parser')

    data.update(get_revenue(soup))
    data.update(get_expenses(soup))
    data['ongoingPrograms'] = get_ongoing_programs(soup)
    data['financialDates'] = get_financial_dates(soup)
    data['compensation'] = get_compensation(soup)
    
    return data
