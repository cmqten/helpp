import bs4, re, urllib.request
from bs4 import BeautifulSoup
from user_agent import generate_user_agent


CRA_SEARCH_RESULTS = 'http://www.cra-arc.gc.ca/ebci/haip/srch/basicsearchresult\
-eng.action?k={}&s=registered&b=true&f=25&p={}#pageControl'


def search_for_charity(search_term, page=1):
    '''
    Sends a search query to the CRA website and returns the results in a
    dictionary where the keys are the registration number and the values are the
    charity names. Throws an exception.

    Args:
        search_term (str) : term to search for
        page (int) : search results page number

    Returns:
        dict : dictionary of search results
    '''
    # sanitize input
    search_term = search_term.strip()
    search_term = re.sub('[^a-zA-Z0-9\-\s]', '', search_term)
    search_term = re.sub('\s+', '+', search_term)

    if not search_term:
        return dict()

    # get search results
    cra_url = CRA_SEARCH_RESULTS.format(search_term, page)
    website = urllib.request.urlopen(cra_url, timeout=5)
    soup = BeautifulSoup(website, 'html.parser')
    results = soup.find('table')

    if not results:
        return dict()

    # first row of table is some bullshit text so remove it
    results = results.contents
    del results[0]

    # place data in a dictionary and return
    data = dict()
    
    for result in results:
        try:
            result_text = result.find('div').find('strong').find('a')\
                          .contents[0].strip()
            reg_number = result_text[-15:].strip()
            charity_name = result_text[:-18].strip()
            data[reg_number] = charity_name
            
        except:
            pass

    return data
