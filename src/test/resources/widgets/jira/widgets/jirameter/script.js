// SURI_TITLE::Widget title::TEXT::Release X.X
// SURI_JQL_ALL::Jira JQL query to count all issue::TEXT::fixVersion = "MCP 1.1" AND project = MTDI
// SURI_JQL_CLOSED::Jira JQL query to count all closed issue::TEXT::fixVersion = "MCP 1.1" AND project = MTDI and status in (3_Closed, Done, "Done Done")
//
function run() {
  var data = {};
  var jsonResponseAll = Packages.call("https://jira_url/jra/rest/api/2/search?jql=" + encodeURIComponent(SURI_JQL_ALL) + "&maxResults=0", "Authorization", "Basic " + Packages.btoa(WIDGET_CONFIG_JIRA_USER + ":" + WIDGET_CONFIG_JIRA_PASSWORD), null);
  if (jsonResponseAll == null) {
    return null;
  }
  var jsonResponseClosed = Packages.call("https://jira_url/jra/rest/api/2/search?jql=" + encodeURIComponent(SURI_JQL_CLOSED) + "&maxResults=0", "Authorization", "Basic " + Packages.btoa(WIDGET_CONFIG_JIRA_USER + ":" + WIDGET_CONFIG_JIRA_PASSWORD), null);
  if (jsonResponseClosed == null) {
    return null;
  }

  var jsonObjectClosed = JSON.parse(jsonResponseClosed);
  var jsonObjectAll = JSON.parse(jsonResponseAll);

  data.value = (jsonObjectClosed.total * 100) / jsonObjectAll.total;
  if (isNaN(data.value)) {
    data.value = 0;
  } else {
    data.value = data.value.toFixed(0);
  }

  return JSON.stringify(data);
}
