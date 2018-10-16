
function sleep(millis)
{
    var date = new Date();
    var curDate = null;
    do { curDate = new Date(); }
    while(curDate-date < millis);
}

function run (){
    // previous data
    var data = {};
    sleep(1000);
    return JSON.stringify(data);
}