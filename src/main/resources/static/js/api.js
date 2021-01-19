const userPath = "/subscribe/user";
const contPath = "/subscribe/";


function send(event){
	event.preventDefault();
	let subpath = $("#subpath").val();
	let subid = $("#subId").val();
	let msg = $("#msgConent").val();
	const destination = subpath+subid;
	$.post('/portal/admin/operation/rest/v1/WebSocketAPI', {Destination : destination, MsgContent : msg}, function(res){ 
     	let obj = JSON.parse(res);
     	console.log(obj);
     	if(obj.code != 200)
     	{
     		alert("Failed!");
     	}
	});
}


var radios =  document.getElementsByName('group');
for(var i = 0, max = radios.length; i < max; i++) {
    radios[i].addEventListener('change', function() {                
        if(this.value=="User")
        {
        	$("#lbtext").text("User ID");
        	$("#subpath").val(userPath);
        	$("#subId").attr("placeholder", "OOOOOOO");
		    $("#subId").val('');
		    $("#subId").focus();
        }
        else
        {
        	$("#lbtext").text("Contract Number");
        	$("#subId").attr("placeholder", "NOOOOOOO");
        	$("#subpath").val(contPath);
        	$("#subId").val('');
		    $("#subId").focus();
        }        
    });
}

$( document ).ready(function() {
    usernameForm.addEventListener('submit', send, true)
    $("#subpath").val(userPath);
    $("#subId").focus();
});