//var connect = require('connect');

var express = require('express');
var fs = require('fs');
var mongo = require('mongodb');


/*var app = connect.createServer(
	connect.logger(),
	function(request, response){
		var responseTxt = 'Hello World';
		if(request.url == '/test'){responseTxt = "You've hit the test page";}
		response.writeHead(200, {'Content-Type':'text/plain'});
		response.end(responseTxt);
	}
);*/

var app = express();

app.configure(function(){
		app.use(express.logger());
		app.use(express.bodyParser());
		app.use(express.methodOverride());
		app.use(express.cookieParser());
		app.use(express.session({ secret: 'thissecretrocks', cookie: { maxAge: 60000 } }));
		app.use( express.errorHandler( {
           dumpExceptions: true,
           showStack: true
  } ) );
});

var user_routes = require('./users')(app);

function checkAuth(req, res, next) {
  if (!req.session.user_id) {
    res.send('You are not authorized to view this page');
  } else {
    next();
  }
}

app.get('/my_secret_page', checkAuth, function (req, res) {
  res.send('if you are viewing this page it means you are logged in');
});
app.listen(3002);
console.log("Server is running");