// SURI_TITLE::Widget title::STRING::Unassigned issue
// SURI_JQL::Jira JQL query::STRING::BSMT and assignee is EMPTY and status not in (Closed,Completed,Done, "Done Done") ORDER BY createdDate DESC
// SURI_DELAI::Span time to compare widget data in hours::INTEGER::24
//
function run (){
    // previous data
    var data = JSON.parse(SURI_PREVIOUS);
    var jsonResponse = Packages.call("https://jira_url/jra/rest/api/2/search?jql="+encodeURIComponent(SURI_JQL)+"&maxResults=0", "Authorization", "Basic "+Packages.btoa(WIDGET_CONFIG_JIRA_USER+":"+WIDGET_CONFIG_JIRA_PASSWORD), null);
    if (jsonResponse == null) {
        return null;
    }
    var jsonObject = JSON.parse(jsonResponse);

    if (data.old == undefined) {
        data.old = [];
    }

    if (data.old.length != 0){
        if (data.old[0].time < new Date().getTime() - SURI_DELAI * 3600000) {
            data.old.shift();
        }
    }

    var value = {};
    value.data = data.currentValue;
    value.time = new Date().getTime();
    data.old.push(value);

    data.currentValue = jsonObject.total;
    var diff = (((data.currentValue - data.old[0].data) * 100) / data.old[0].data);
    if (isNaN(diff)){
        diff = 0;
    }

    if (data.currentValue == null){
        data.old.shift();
    }

    data.difference = diff.toFixed(1) + "%";
    data.arrow = diff == 0 ? '' : (diff > 0 ? "up" : "down");

    return JSON.stringify(data);
}