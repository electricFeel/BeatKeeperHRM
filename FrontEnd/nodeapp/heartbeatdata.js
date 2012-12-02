module.exports = function(app){
	console.log('loading heartbeat data functions');
	var mongo = require('mongodb');
	var Server = mongo.Server;
	var Db = mongo.Db; 

	app.post('/hbdata/add', function(req, res){
		var post = req.body;
		var token = post.session_token;
		var data = post.hbData;
		var dataString = data.hbData.toString();
		console.log(dataString);
		var dataDict = JSON.parse(dataString);

	});
}