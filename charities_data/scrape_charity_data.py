import bs4, re, urllib.request
from bs4 import BeautifulSoup
from time import sleep
from user_agent import generate_user_agent


REG_NUMBER_REGEX = re.compile('^([0-9]+)RR([0-9]+)$')
CRA_SEARCH_LINK = 'http://www.cra-arc.gc.ca/ebci/haip/srch/\
advancedsearchresult-eng.action?b={}&q={}'
CURRENCY_REGEX = re.compile('(\$[0-9,]+)')


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
    receipted_donations = soup.find('li', class_='legend-li-red')
    receipted_donations = '$0' if not receipted_donations \
                           else str(receipted_donations.contents[0])
    data['receipted_donations'] = extract_currency(receipted_donations)
    
    non_receipted_donations = soup.find('li', class_='legend-li-yellow')
    non_receipted_donations = '$0' if not non_receipted_donations \
                              else str(non_receipted_donations.contents[0])
    data['non_receipted_donations'] = extract_currency(non_receipted_donations)
    
    gifts_from_other_charities = soup.find('li', class_='legend-li-blue')
    gifts_from_other_charities = '$0' if not gifts_from_other_charities \
                                 else str(gifts_from_other_charities.contents[0])
    data['gifts_from_other_charities'] = extract_currency(
        gifts_from_other_charities)

    government_funding = soup.find('li', class_='legend-li-green')
    government_funding = '$0' if not government_funding \
                          else str(government_funding.contents[0])
    data['government_funding'] = extract_currency(government_funding)
    
    other = soup.find('li', class_='legend-li-aqua')
    other = '$0' if not other else str(other.contents[0])
    data['other'] = extract_currency(other)

    data['total'] = data['receipted_donations'] + \
                    data['non_receipted_donations'] + \
                    data['gifts_from_other_charities'] + \
                    data['government_funding'] + \
                    data['other']

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
    charitable_program = soup.find('li', class_='legend-li-hot-pink')
    charitable_program = '$0' if not charitable_program \
                           else str(charitable_program.contents[0])
    data['charitable_program'] = extract_currency(charitable_program)
    
    management_and_admin = soup.find('li',class_='legend-li-azure-radiance-blue')
    management_and_admin = '$0' if not management_and_admin \
                              else str(management_and_admin.contents[0])
    data['management_and_admin'] = extract_currency(management_and_admin)
    
    fundraising = soup.find('li', class_='legend-li-pearl-peach')
    fundraising = '$0' if not fundraising \
                  else str(fundraising.contents[0])
    data['fundraising'] = extract_currency(fundraising)

    political_activities = soup.find('li', class_='legend-li-blue-kimberly')
    political_activities = '$0' if not political_activities \
                           else str(political_activities.contents[0])
    data['political_activities'] = extract_currency(political_activities)
    
    gifts_to_other = soup.find('li', class_='legend-li-orange')
    gifts_to_other = '$0' if not gifts_to_other \
                     else ' '.join(remove_tags(gifts_to_other.contents))
    data['gifts_to_other'] = extract_currency(gifts_to_other)

    other = soup.find('li', class_='legend-li-dark-green')
    other = '$0' if not other \
            else ' '.join(remove_tags(other.contents))
    data['other'] = extract_currency(other)

    data['total'] = data['charitable_program'] + \
                    data['management_and_admin'] + \
                    data['fundraising'] + \
                    data['political_activities'] + \
                    data['gifts_to_other'] + \
                    data['other']

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


def scrape_cra_charities(charities):
    '''
    Using the registration numbers from the text file, scrape additional info
    about each charity from the CRA website.

    Args:
        charities (dict) : a dictionary from json in which the keys are the
                           registration numbers and the values are charity info.

    Returns:
        None
    '''
    for reg_number in charities:
        success = 0
        attempts = 0
        match = REG_NUMBER_REGEX.match(reg_number)
        
        if not match:
            print('{} does not match the registration format'.format(reg_number))
            continue

        # Attempt to scrape a website 5 times before moving on if errors occur
        while success == 0 and attempts < 5:
            attempts += 1
            user_agent_random = generate_user_agent(device_type='desktop')
            
            try:
                website = urllib.request.urlopen(
                    urllib.request.Request(
                        CRA_SEARCH_LINK.format(match.group(1), match.group(2)),
                        headers={'User-Agent': user_agent_random}))

                soup = BeautifulSoup(website, 'lxml')

                charities[reg_number]['revenue'] = get_revenue(soup)
                charities[reg_number]['expenses'] = get_expenses(soup)
                charities[reg_number]['ongoing_programs'] = \
                    get_ongoing_programs(soup)
                success = 1

            except Exception as e:
                print(reg_number, ':', e)
                continue

            sleep(1)

