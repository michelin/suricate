// SURI_TITLE::Widget title::STRING::Monitor Application
// SURI_APPNAME::Application name in nagios::STRING::VCS
// SURI_BSS::BSS name in nagios::STRING::MT
// SURI_ENV::Environment name in nagios::COMBO::PRD:Production,IND:Indus,CQT:Central Qualification,DEV:Development
//
var nbService = 0;
var nbCritcal = 0;
var nbWarning = 0;

function run (){
    // previous data
    var data = {};
    var jsonResponse = Packages.call("https://nagios_url/nagiosxi/api/v1/application/status?app="+SURI_APPNAME+"&bss="+SURI_BSS+"&env="+SURI_ENV+"&apikey="+WIDGET_CONFIG_NAGIOS_TOKEN, null, null, null);
    if (jsonResponse == null) {
        return null;
    }
    var jsonObject = JSON.parse(jsonResponse);
    if (jsonObject.error !== undefined || jsonObject.errors !== undefined) {
        Packages.throwFatalError("Error with project name or environement:\n\n" + jsonResponse)
    }

    data.host = [];
    var hosts = jsonObject.hostgrouplist.hostgroup.hostlist.host;

    // Extract data
    if (Array.isArray(hosts)){
        for (var i =0; i < hosts.length; i++) {
            data.host.push(extractData(hosts[i]));
        }
    } else {
        data.host.push(extractData(hosts));
    }

    data.ok = jsonObject.hostgrouplist.hostgroup.percent_ok === "100" && data.host.length == 0;
    data.warning = jsonObject.hostgrouplist.hostgroup.percent_warn !== "0";
    if (data.ok) {
        data.status = "green";
    } else if (data.warning) {
        data.status = "yellow";
    } else {
        data.status = "red";
    }

    data.nbService = nbService;
    data.nbCritcal = nbCritcal;
    data.nbWarning = nbWarning;

    data.small = (data.nbCritcal + data.nbWarning) > 3;
    data.minimal = (data.nbCritcal + data.nbWarning) > 10;

    return JSON.stringify(data);
}

function extractData(host){
    var val = {};
    val.host_name = host.host_name
    val.total = parseInt(host.total_services)
    val.total_warn = parseInt(host.total_warn)
    val.total_crit = parseInt(host.total_crit)
    val.total_problem = val.total_warn + val.total_crit;
    val.host_in_error = val.total_problem > 0 || host.state_code != "0";

    nbService += parseInt(host.total_services)
    nbCritcal += parseInt(host.total_crit)
    nbWarning += parseInt(host.total_warn)

    val.services = [];
    var services = host.servicelist.service;
    for (var i = 0; i< services.length; i++ ) {
        if (services[i].state_code !== "0"){
            val.services.push(services[i]);
        }
    }
    return val;
}