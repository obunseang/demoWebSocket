'use strict';

var MessageType = {CHAT:"CHAT",
        JOIN:"JOIN",
        LEAVE:"LEAVE"};

var URL = {wsURL:"/ws", subURL:"/subscribe/", sendURL:"/send/sendMessage"};
var subscriptGroup = 'groupPublic';
var subChannelID = undefined;
var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');

var stompClient = null;
var username = null;

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function connect(event) {
    username = document.querySelector('#name').value.trim();
    if(username) {
        var socket = new SockJS(URL.wsURL);
        stompClient = Stomp.over(socket);
        stompClient.connect({user: username}, onConnected, onError);
    }
    else{
        document.querySelector('#name').focus();
    }
    subscriptGroup = document.querySelector('input[name="group"]:checked').value;
    event.preventDefault();
}


function onConnected() {
    //successfully connected!
    usernamePage.classList.add('hidden');
    chatPage.classList.remove('hidden');
    
    stompClient.subscribe(URL.subURL+username, onMessageReceived);
    
	// Subscribe to the Public Topic
    let subObj = stompClient.subscribe(URL.subURL+subscriptGroup, onMessageReceived);
    subChannelID = subObj.id;
    
    // Tell your username to the server
    stompClient.send(URL.sendURL,{},JSON.stringify({sender: username, type: MessageType.JOIN, group:subscriptGroup}));
    
    document.getElementById("cUserName").innerHTML = username;
    document.querySelector('input[name="ugroup"][value="'+subscriptGroup+'"]').checked = true;
    connectingElement.classList.add('hidden');
}

function onError(error) {
    connectingElement.classList.remove('hidden');
    connectingElement.classList.add('error');
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
}


function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    if(messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageInput.value,
            type: MessageType.CHAT,
            group:subscriptGroup
        };
        stompClient.send(URL.sendURL, {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}

function IsJsonString(str) {
    try {
        JSON.parse(str);
    } catch (e) {
        return false;
    }
    return true;
}

function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);
    var messageElement = document.createElement('li');

    if(message.type === MessageType.JOIN) {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined!';
    } else if (message.type === MessageType.LEAVE) {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' left!';
    } else {
        messageElement.classList.add('chat-message');

        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);

        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }

	var contentText = '';
    var textElement = document.createElement('p');
    if(IsJsonString(message.content))
    {
    	const obj = JSON.parse(message.content);
    	console.log(obj);
    	contentText = JSON.stringify(obj,null,4); 
    }
    else
    {
    	contentText = message.content;
    }    
    var messageText = document.createTextNode(contentText);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}


function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
}


var radios =  document.getElementsByName('ugroup');
for(var i = 0, max = radios.length; i < max; i++) {
    radios[i].addEventListener('change', function() {        
        if(subscriptGroup != this.value)
        {
	        subscriptGroup = this.value;
	        console.log(subscriptGroup);
	        // Subscribe to the Public Topic
	        stompClient.unsubscribe(subChannelID);
	    	const subObj = stompClient.subscribe(URL.subURL+subscriptGroup, onMessageReceived);
	    	subChannelID = subObj.id;
	    }
    });
}

usernameForm.addEventListener('submit', connect, true)
messageForm.addEventListener('submit', sendMessage, true)