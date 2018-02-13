const NewsAPI = require('newsapi');
const newsapi = new NewsAPI('eda0cc6ff1694f47bf3fbbbbd8cdde49');
// To query /v2/top-headlines
// All options passed to topHeadlines are optional, but you need to include at least one of them
newsapi.v2.everything({
  q: 'ACCESSIBLE COMMUNITY COUNSELLING AND EMPLOYMENT SERVICES',
}).then(response => {
  console.log(response);
  /*
    {
      status: "ok",
      articles: [...]
    }
  */
});