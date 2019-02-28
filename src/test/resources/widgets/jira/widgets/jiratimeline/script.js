// SURI_TITLE::Widget title::TEXT::Timeline
// SURI_PROJECT::Jira PROJECT::TEXT::MTDI

function compareRelease(a, b) {
  return new Date(a.date).getTime() - new Date(b.date).getTime();
}

function run() {
  var jsonResponse = Packages.call("https://Jira_url/jra/rest/api/2/project/" + encodeURIComponent(SURI_PROJECT) + "/versions/", "Authorization", "Basic " + Packages.btoa(WIDGET_CONFIG_JIRA_USER + ":" + WIDGET_CONFIG_JIRA_PASSWORD), null);
  if (jsonResponse == null) {
    return null;
  }
  var jsonObject = JSON.parse(jsonResponse);

  var data = {};
  array = [];
  for (var i in jsonObject) {
    var releaseDate = new Date(jsonObject[i].releaseDate);
    var maxDate = new Date();
    maxDate.setMonth(maxDate.getMonth() + 5);
    if (releaseDate > new Date() && releaseDate < maxDate) {
      var obj = {};
      obj.name = jsonObject[i].name;
      obj.date = jsonObject[i].releaseDate;
      array.push(obj);
    }
  }

  array.sort(compareRelease);

  data.data = JSON.stringify(array);
  return JSON.stringify(data);
}
